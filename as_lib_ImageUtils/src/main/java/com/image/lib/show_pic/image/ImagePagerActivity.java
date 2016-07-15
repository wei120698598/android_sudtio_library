package com.image.lib.show_pic.image;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.image.lib.R;

import java.util.ArrayList;

/**
 * 图片查看器
 */
public class ImagePagerActivity extends FragmentActivity {
    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_URLS = "image_urls";
    public static final String DATALIST = "dataList";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_LIST_INDEX = "list_index";


    private com.image.lib.show_pic.image.HackyViewPager mPager;
    private int positionGrid;
    private TextView indicator;
    private ImagePagerAdapter mAdapter;
    private int positionList;

    private TextView tv_content;
    private ImageView iv_detail;


    private ArrayList<String> imageUrls;
    private int frag_type;

    private LinearLayout ll_point_group;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = View.inflate(this, R.layout.listview_image_detail_pager, null);
        setContentView(view);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            view.setFitsSystemWindows(true);
//            SetSystemTint.initSystemBar(this, android.R.color.black);
//        }

        positionGrid = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);


        ll_point_group = (LinearLayout) findViewById(R.id.ll_point_group);

        imageUrls = (ArrayList<String>) getIntent().getSerializableExtra(EXTRA_IMAGE_URLS);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
        params.setMargins(0, 0, 15, 0);
        for (int i = 0; i < imageUrls.size(); i++) {
            ImageView point = new ImageView(this);
            point.setLayoutParams(params);
            point.setBackgroundResource(R.drawable.point_bg);
            ll_point_group.addView(point);
            if (i == lastPosition) {
                point.setEnabled(true);
            } else {
                point.setEnabled(false);
            }
        }

        mPager = (com.image.lib.show_pic.image.HackyViewPager) findViewById(R.id.pager);
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), imageUrls);
        mPager.setAdapter(mAdapter);
        indicator = (TextView) findViewById(R.id.indicator);
        // indicator.setVisibility(View.VISIBLE);
        CharSequence text = getString(R.string.viewpager_indicator, 1, mPager.getAdapter().getCount());
        indicator.setText(text);
        // 更新下标
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int position) {
                CharSequence text = getString(R.string.viewpager_indicator, position + 1, mPager.getAdapter().getCount());
                indicator.setText(text);
                ll_point_group.getChildAt(lastPosition).setEnabled(false);
                ll_point_group.getChildAt(position).setEnabled(true);
                lastPosition = position;
            }

        });
        if (savedInstanceState != null) {
            positionGrid = savedInstanceState.getInt(STATE_POSITION);
        }
        mPager.setCurrentItem(positionGrid);
    }

    private int lastPosition = 0;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public ArrayList<String> fileList;

        public ImagePagerAdapter(FragmentManager fm, ArrayList<String> fileList) {
            super(fm);
            this.fileList = fileList;
        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.size();
        }

        @Override
        public Fragment getItem(int position) {
            String url = fileList.get(position);
            if (url.contains("sma")) {
                url = url.replace("sma", "org");
            } else if (url.contains("mid")) {
                url = url.replace("mid", "org");
            }
            return  ImageDetailFragment.newInstance(url);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }




    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
