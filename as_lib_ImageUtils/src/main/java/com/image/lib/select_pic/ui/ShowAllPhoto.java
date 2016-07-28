package com.image.lib.select_pic.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.image.lib.R;
import com.image.lib.select_pic.Bimp;
import com.image.lib.select_pic.ImageItem;
import com.image.lib.select_pic.photoview.AlbumGridViewAdapter;
import com.image.lib.select_pic.photoview.PublicWay;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;


/**
 * 这个是显示一个文件夹里面的所有图片时的界面
 */
public class ShowAllPhoto extends Activity {
	private GridView gridView;
	private ProgressBar progressBar;
	private AlbumGridViewAdapter gridImageAdapter;
	// 完成按钮
	private TextView okButton;
	// 预览按钮
	private TextView preview;
	// 返回按钮
	private ImageView back;
	// 取消按钮
	private TextView cancel;
	// 标题
	private TextView headTitle;
	private Intent intent;
	private Context context;
	public static ArrayList<ImageItem> dataList = new ArrayList<ImageItem>();
	public static ArrayList<ImageItem> tempList = new ArrayList<ImageItem>();
	public static ArrayList<ImageItem> adapterDatalist = new ArrayList<ImageItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View view = View.inflate(this, R.layout.activity_plugin_camera_show_all_photo, null);
		setContentView(view);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			view.setFitsSystemWindows(true);
//			SetSystemTint.initSystemBar(this, R.color.title_bg);
//		}

		PublicWay.activityList.add(this);
		this.context = this;
		back = (ImageView) findViewById(R.id.iv_back);
		cancel = (TextView) findViewById(R.id.tv_menu);
		cancel.setVisibility(View.VISIBLE);
		preview = (TextView) findViewById(R.id.showallphoto_preview);
		okButton = (TextView) findViewById(R.id.showallphoto_ok_button);
		headTitle = (TextView) findViewById(R.id.tv_title);
		this.intent = getIntent();
		String folderName = intent.getStringExtra("folderName");
		if (folderName.length() > 8) {
			folderName = folderName.substring(0, 9) + "...";
		}
		headTitle.setText(folderName);
		cancel.setOnClickListener(new CancelListener());
		cancel.setText("取消");
		back.setOnClickListener(new BackListener());
		preview.setOnClickListener(new PreviewListener());
		init();
		initListener();
		isShowOkBt();
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			gridImageAdapter.notifyDataSetChanged();
		}
	};

	private class PreviewListener implements OnClickListener {
		public void onClick(View v) {
			if (Bimp.tempSelectBitmap.size() + tempList.size() > 0) {
				intent.putExtra("position", "2");
				intent.putExtra("ID", 0);
				intent.setClass(ShowAllPhoto.this, GalleryActivity.class);
				startActivity(intent);
			}
		}

	}

	private class BackListener implements OnClickListener {// 返回按钮监听

		public void onClick(View v) {
			tempList.clear();
			finish();
		}

	}

	private class CancelListener implements OnClickListener {// 取消按钮的监听
		public void onClick(View v) {
			tempList.clear();
			intent.setClass(context, Bimp.clazz);
			startActivity(intent);
			PublicWay.finishActivity();
		}
	}

	private void init() {
		IntentFilter filter = new IntentFilter("data.broadcast.action");
		registerReceiver(broadcastReceiver, filter);
		progressBar = (ProgressBar) findViewById(R.id.showallphoto_progressbar);
		progressBar.setVisibility(View.GONE);
		gridView = (GridView) findViewById(R.id.showallphoto_myGrid);

		adapterDatalist.addAll(Bimp.tempSelectBitmap);
		adapterDatalist.addAll(tempList);
		
		
		gridImageAdapter = new AlbumGridViewAdapter(this, dataList, adapterDatalist);
		gridView.setAdapter(gridImageAdapter);
		okButton = (TextView) findViewById(R.id.showallphoto_ok_button);
	}

	private void initListener() {

		gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
			public void onItemClick(final ToggleButton toggleButton, int position, boolean isChecked, Button button) {
				if (Bimp.tempSelectBitmap.size() + tempList.size() >= PublicWay.num && isChecked) {
					button.setVisibility(View.GONE);
					toggleButton.setChecked(false);
					Toast.makeText(ShowAllPhoto.this, getResources().getString(R.string.only_choose_num), Toast.LENGTH_SHORT).show();
					return;
				}

				if (isChecked) {
					button.setVisibility(View.VISIBLE);
					tempList.add(dataList.get(position));
					okButton.setText("完成(" + (Bimp.tempSelectBitmap.size() + tempList.size()) + ")");
				} else {
					button.setVisibility(View.GONE);
					try {
						boolean isOk = Bimp.tempSelectBitmap.remove(dataList.get(position));
						if (!isOk) {
							tempList.remove(dataList.get(position));
						} else {
							Bimp.max--;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					okButton.setText("完成(" + (Bimp.tempSelectBitmap.size() + tempList.size()) + ")");
				}
				isShowOkBt();
			}
		});

		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				okButton.setClickable(false);
				Bimp.tempSelectBitmap.addAll(tempList);
				Bimp.max += tempList.size();

				Logger.d(tempList.size()+"=="+Bimp.tempSelectBitmap.size());

				tempList.clear();
				intent.setClass(context,Bimp.clazz);
				startActivity(intent);
				PublicWay.finishActivity();
			}
		});

	}

	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() + tempList.size() >= 0) {
			okButton.setText("完成(" + (Bimp.tempSelectBitmap.size() + tempList.size()) + ")");
			preview.setClickable(true);
			okButton.setClickable(true);
		} else {
			okButton.setText("完成(" + (Bimp.tempSelectBitmap.size() + tempList.size()) + ")");
			preview.setClickable(false);
			okButton.setClickable(false);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			tempList.clear();
			finish();
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		adapterDatalist.clear();
		adapterDatalist.addAll(Bimp.tempSelectBitmap);
		adapterDatalist.addAll(tempList);
		gridImageAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onRestart() {
		isShowOkBt();
		super.onRestart();
	}

}
