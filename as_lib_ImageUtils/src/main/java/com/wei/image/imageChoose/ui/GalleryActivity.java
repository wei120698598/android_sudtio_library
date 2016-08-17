package com.wei.image.imageChoose.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wei.image.R;
import com.wei.image.imageChoose.Bimp;
import com.wei.image.imageChoose.CommonUtils;
import com.wei.image.imageChoose.ImageItem;
import com.wei.image.imageChoose.photoview.FolderAdapter;
import com.wei.image.imageChoose.photoview.PhotoView;
import com.wei.image.imageChoose.photoview.PublicWay;
import com.wei.image.imageChoose.zoom.ViewPagerFixed;

import java.util.ArrayList;

/**
 * 这个是用于进行图片浏览时的界面
 */
public class GalleryActivity extends Activity {
	private TextView send_bt;
	private TextView del_bt;
	private int location = 0;
	private ImageView iv_back;
	private TextView tv_title;

	private ArrayList<View> listViews = null;
	private ViewPagerFixed pager;
	private MyPageAdapter adapter;

	private Context context;

	RelativeLayout photo_relativeLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = View.inflate(this, R.layout.activity_plugin_camera_gallery, null);
		setContentView(view);// 切屏到主界面
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		// view.setFitsSystemWindows(true);
		// SetSystemTint.initSystemBar(this, R.color.title_bg);
		// }

		PublicWay.activityList.add(this);
		this.context = this;
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(new BackListener());
		send_bt = (TextView) findViewById(R.id.send_button);
		del_bt = (TextView) findViewById(R.id.gallery_del);
		send_bt.setOnClickListener(new GallerySendListener());
		del_bt.setOnClickListener(new DelListener());
		pager = (ViewPagerFixed) findViewById(R.id.gallery01);
		pager.setOnPageChangeListener(pageChangeListener);
		for (int i = 0; !CommonUtils.isEmpty(Bimp.tempSelectBitmap) && i < Bimp.tempSelectBitmap.size(); i++) {
			initListViews(Bimp.tempSelectBitmap.get(i));
		}
		for (int i = 0; !CommonUtils.isEmpty(ShowAllPhoto.tempList) && i < ShowAllPhoto.tempList.size(); i++) {
			initListViews(ShowAllPhoto.tempList.get(i));
		}
		isShowOkBt();
		adapter = new MyPageAdapter(listViews);
		pager.setAdapter(adapter);
		pager.setPageMargin((int) getResources().getDimensionPixelOffset(R.dimen.ui_10_dip));
		try {
			int i = getIntent().getIntExtra("ID", 0);
			tv_title = (TextView) findViewById(R.id.tv_title);
			tv_title.setText((i + 1) + "/" + listViews.size());
			pager.setCurrentItem(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

		public void onPageSelected(int arg0) {
			location = arg0;
			tv_title.setText((location + 1) + "/" + listViews.size());
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageScrollStateChanged(int arg0) {

		}
	};

	@SuppressLint("NewApi")
	private void initListViews(ImageItem item) {
		if (listViews == null)
			listViews = new ArrayList<View>();
		PhotoView img = new PhotoView(this);

		int degree = FolderAdapter.readPictureDegree(item.getImagePath());
		img.setRotation(degree);
		img.setBackgroundColor(0xff000000);
		img.setImageBitmap(item.getBitmap());
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		listViews.add(img);
	}

	// 返回按钮添加的监听器
	private class BackListener implements OnClickListener {

		public void onClick(View v) {
			finish();
		}
	}

	// 删除按钮添加的监听器
	private class DelListener implements OnClickListener {

		public void onClick(View v) {
			if (listViews.size() == 1) {
				Bimp.tempSelectBitmap.clear();
				ShowAllPhoto.tempList.clear();
				Bimp.max = 0;
				send_bt.setText("完成(" + (Bimp.tempSelectBitmap.size() + ShowAllPhoto.tempList.size()) + ")");
				Intent intent = new Intent("data.broadcast.action");
				sendBroadcast(intent);
				finish();
			} else {
				try {
					if (!CommonUtils.isEmpty(Bimp.tempSelectBitmap)) {
						Bimp.tempSelectBitmap.remove(location);
						Bimp.max--;
					} else {
						if (!CommonUtils.isEmpty(ShowAllPhoto.tempList)) {
							ShowAllPhoto.tempList.remove(location);
						}
					}
				} catch (Exception e) {
					if (!CommonUtils.isEmpty(ShowAllPhoto.tempList)) {
						ShowAllPhoto.tempList.remove(location);
					}
				}
				listViews.remove(location);
				pager.removeAllViews();
				adapter.setListViews(listViews);
				send_bt.setText("完成(" + (Bimp.tempSelectBitmap.size() + ShowAllPhoto.tempList.size()) + ")");
				adapter.notifyDataSetChanged();
			}
			tv_title.setText((location + 1) + "/" + listViews.size());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
	}

	// 完成按钮的监听
	private class GallerySendListener implements OnClickListener {
		public void onClick(View v) {
			Bimp.tempSelectBitmap.addAll(ShowAllPhoto.tempList);
			ShowAllPhoto.tempList.clear();
			startActivity(new Intent(context, Bimp.clazz));
			PublicWay.finishActivity();
		}
	}

	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() + ShowAllPhoto.tempList.size() >= 0) {
			send_bt.setText("完成(" + (Bimp.tempSelectBitmap.size() + ShowAllPhoto.tempList.size()) + ")");
			send_bt.setClickable(true);
		} else {
			send_bt.setClickable(false);
		}
	}

	/**
	 * 监听返回按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}

	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;

		private int size;

		public MyPageAdapter(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {
			try {
				((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
}
