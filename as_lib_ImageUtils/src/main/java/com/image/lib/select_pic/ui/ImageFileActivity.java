package com.image.lib.select_pic.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.image.lib.R;
import com.image.lib.select_pic.AlbumHelper;
import com.image.lib.select_pic.Bimp;
import com.image.lib.select_pic.CommonUtils;
import com.image.lib.select_pic.ImageBucket;
import com.image.lib.select_pic.ImageItem;
import com.image.lib.select_pic.photoview.FolderAdapter;
import com.image.lib.select_pic.photoview.PublicWay;

import java.util.ArrayList;

/**
 * 这个类主要是用来进行显示包含图片的文件夹
 * 
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日 下午11:48:06
 */
public class ImageFileActivity extends Activity {

	private FolderAdapter folderAdapter;
	private Context context;
	private ProgressDialog dialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View view = View.inflate(this, R.layout.activity_plugin_camera_image_file, null);
		setContentView(view);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			view.setFitsSystemWindows(true);
//			SetSystemTint.initSystemBar(this, R.color.title_bg);
//		}

		findViewById(R.id.iv_back).setVisibility(View.GONE);
		((TextView) findViewById(R.id.tv_title)).setText("相册");
		TextView tv_menu = (TextView) findViewById(R.id.tv_menu);
		tv_menu.setText("取消");
		tv_menu.setOnClickListener(new CancelListener());
		tv_menu.setVisibility(View.VISIBLE);
		PublicWay.activityList.add(this);
		this.context = this;
		dialog = new ProgressDialog(context);
		scanPic();
		GridView gridView = (GridView) findViewById(R.id.fileGridView);
		folderAdapter = new FolderAdapter(this);
		gridView.setAdapter(folderAdapter);

	}

	private void scanPic() {
		dialog.show();
		AlbumActivity.helper = AlbumHelper.getHelper();
		AlbumActivity.helper.init(getApplicationContext());
		AlbumActivity.contentList = AlbumActivity.helper.getImagesBucketList(this, false);
		AlbumActivity.dataList = new ArrayList<ImageItem>();
		for (int i = 0; i < AlbumActivity.contentList.size(); i++) {
			AlbumActivity.dataList.addAll(AlbumActivity.contentList.get(i).imageList);
		}
		goToCamera();
	}

	private void goToCamera() {
		if (!CommonUtils.isEmpty(AlbumActivity.contentList)) {
			for (ImageBucket imageBucket : AlbumActivity.contentList) {
				String folderName = imageBucket.bucketName;
				if (folderName.contains("Camera")) {
					ShowAllPhoto.dataList = (ArrayList<ImageItem>) imageBucket.imageList;
					Intent intent = new Intent();
					intent.putExtra("folderName", folderName);
					intent.setClass(this, ShowAllPhoto.class);
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
					startActivity(intent);
					break;
				}
			}
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}

	private class CancelListener implements OnClickListener {// 取消按钮的监听
		public void onClick(View v) {
			// 清空选择的图片
			ShowAllPhoto.tempList.clear();
			Intent intent = new Intent();
			intent.setClass(context, Bimp.clazz);
			startActivity(intent);
			PublicWay.finishActivity();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ShowAllPhoto.tempList.clear();
			Intent intent = new Intent();
			intent.setClass(context, Bimp.clazz);
			startActivity(intent);
			PublicWay.finishActivity();
		}

		return true;
	}

}
