package com.foamtrace.photopicker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.foamtrace.photopicker.widget.ViewPagerFixed;

import java.util.ArrayList;

import static com.foamtrace.photopicker.PhotoPickerActivity.mDesireImageCount;

/**
 * Created by foamtrace on 2015/8/25.
 */
public class PhotoPreviewActivity extends AppCompatActivity implements PhotoPagerAdapter.PhotoViewClickListener {

    public static final String EXTRA_PHOTOS = "extra_photos";
    public static final String EXTRA_ALL_PHOTOS = "extra_all_photos";
    public static final String EXTRA_CURRENT_ITEM = "extra_current_item";

    /**
     * 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合
     */
    public static final String EXTRA_RESULT = "preview_result";

    /**
     * 预览请求状态码
     */
    public static final int REQUEST_PREVIEW = 99;

    private ArrayList<String> selectPaths = new ArrayList<>();
    private ArrayList<String> allPaths = new ArrayList<>();
    private ViewPagerFixed mViewPager;
    private PhotoPagerAdapter mPagerAdapter;
    private int currentItem = 0;

    private MenuItem menuItem;

    private TextView tv_choose;
    private boolean isClickImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_preview);

        initViews();

        ArrayList<String> pathArr = getIntent().getStringArrayListExtra(EXTRA_PHOTOS);
        if (pathArr != null) {
            selectPaths.addAll(pathArr);
        }

        ArrayList<String> pathArr2 = getIntent().getStringArrayListExtra(PhotoPreviewActivity.EXTRA_ALL_PHOTOS);
        if (pathArr2 != null) {
            allPaths.addAll(pathArr2);
        }

        isClickImage = getIntent().getBooleanExtra(PhotoPickerActivity.EXTRA_CLICK_IMAGE, false);

        findViewById(R.id.rl_choose).setVisibility(isClickImage ? View.VISIBLE : View.GONE);

        tv_choose = (TextView) findViewById(R.id.tv_choose);

        tv_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_choose.isSelected()) {
                    tv_choose.setSelected(false);
                    String path = allPaths.get(mViewPager.getCurrentItem());
                    if (selectPaths.contains(path)) {
                        selectPaths.remove(path);
                    }
                } else {
                    tv_choose.setSelected(true);
                    if (selectPaths.size() < mDesireImageCount) {
                        String path = allPaths.get(mViewPager.getCurrentItem());
                        if (!selectPaths.contains(path))
                            selectPaths.add(path);
                    } else {
                        Toast.makeText(PhotoPreviewActivity.this, R.string.msg_amount_limit, Toast.LENGTH_SHORT).show();
                    }
                }
                menuItem.setTitle(getString(R.string.done_with_count, selectPaths.size(), mDesireImageCount));
            }
        });

        currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);

        mPagerAdapter = new PhotoPagerAdapter(this, allPaths);
        mPagerAdapter.setPhotoViewClickListener(this);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateActionBarTitle();
            }

            @Override
            public void onPageSelected(int position) {
                menuItem.setTitle(getString(R.string.done_with_count, selectPaths.size(), mDesireImageCount));
                if (selectPaths.contains(allPaths.get(position))) {
                    tv_choose.setSelected(true);
                } else {
                    tv_choose.setSelected(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setCurrentItem(currentItem);
        if (selectPaths.contains(allPaths.get(currentItem))) {
            tv_choose.setSelected(true);
        } else {
            tv_choose.setSelected(false);
        }
        updateActionBarTitle();
    }

    private void initViews() {
        mViewPager = (ViewPagerFixed) findViewById(R.id.vp_photos);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.pickerToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void OnPhotoTapListener(View view, float v, float v1) {
        onBackPressed();
    }

    public void updateActionBarTitle() {
        getSupportActionBar().setTitle(
                getString(R.string.image_index, mViewPager.getCurrentItem() + 1, allPaths.size()));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT, selectPaths);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preview, menu);
        menuItem = menu.findItem(R.id.action_discard);
        if (isClickImage) {
            menuItem.setIcon(0);
            menuItem.setTitle(getString(R.string.done_with_count, selectPaths.size(), mDesireImageCount));
        }else{
            menuItem.setTitle("删除");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (isClickImage && item.getItemId() == R.id.action_discard) {
            onBackPressed();
            return true;
        }


        // 删除当前照片
        if (item.getItemId() == R.id.action_discard) {
            final int index = mViewPager.getCurrentItem();
            final String deletedPath = selectPaths.get(index);
            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), R.string.deleted_a_photo,
                    Snackbar.LENGTH_LONG);
            if (selectPaths.size() <= 1) {
                // 最后一张照片弹出删除提示
                // show confirm dialog
                new AlertDialog.Builder(this)
                        .setTitle(R.string.confirm_to_delete)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                selectPaths.remove(index);
                                if (!isClickImage) {
                                    allPaths.remove(index);
                                }
                                onBackPressed();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            } else {
                snackbar.show();
                selectPaths.remove(index);
                if (!isClickImage) {
                    allPaths.remove(index);
                }
                mPagerAdapter.notifyDataSetChanged();
            }

            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectPaths.size() > 0) {
                        selectPaths.add(index, deletedPath);
                        if (!isClickImage) {
                            allPaths.add(index, deletedPath);
                        }
                    } else {
                        selectPaths.add(deletedPath);
                        if (!isClickImage) {
                            allPaths.add(deletedPath);
                        }
                    }
                    mPagerAdapter.notifyDataSetChanged();
                    mViewPager.setCurrentItem(index, true);
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
}
