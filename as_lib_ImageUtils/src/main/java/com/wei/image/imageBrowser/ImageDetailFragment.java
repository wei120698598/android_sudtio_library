/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.wei.image.imageBrowser;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wei.image.R;
import com.wei.image.imageChoose.photoview.PhotoViewAttacher;

import java.io.File;
import java.util.ArrayList;

/**
 * 单张图片显示Fragment
 */
public class ImageDetailFragment extends Fragment {
	private String mImageUrl;
	private ImageView mImageView;
	private ProgressBar progressBar;
	private PhotoViewAttacher mAttacher;

	// 背景图片
	private ImageView iv_image_bg;
	private ReleaseBitmapLoadingListener releaseBitmapLoadingListener;
	private ImageLoader imageLoader;

	public static ImageDetailFragment newInstance(String imageUrl) {
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.listview_image_detail_fragment, container, false);
		mImageView = (ImageView) v.findViewById(R.id.image);

		// 初始化背景图片
		iv_image_bg = (ImageView) v.findViewById(R.id.iv_image_bg);

		mAttacher = new PhotoViewAttacher(mImageView);

		mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				try {
					getActivity().finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		progressBar = (ProgressBar) v.findViewById(R.id.loading);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		releaseBitmapLoadingListener = new ReleaseBitmapLoadingListener();
		if (imageLoader == null)
			imageLoader = ImageLoader.getInstance();
		String path = mImageUrl;
		File file = new File(mImageUrl);
		if (file.isFile() && file.exists())
			path = "file://" + mImageUrl;
		imageLoader.displayImage(path, mImageView, releaseBitmapLoadingListener);
	}

	public class ReleaseBitmapLoadingListener extends SimpleImageLoadingListener {

		private ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();

		public void cleanBitmapList() {
			if (bitmapList.size() > 0) {
				for (Bitmap bitmap : bitmapList) {
					if (bitmap != null && !bitmap.isRecycled()) {
						bitmap.recycle();
					}
				}
			}
			if (imageLoader != null) {
				imageLoader.clearDiscCache();
				imageLoader.clearMemoryCache();
			}
		}

		@Override
		public void onLoadingStarted(String imageUri, View view) {
			progressBar.setVisibility(View.VISIBLE);
			// wei
			// 我自己写的
			// 显示默认九宫格中的小图片
//			Drawable bitmap = NoScrollGridAdapter.getBitmap();
//			iv_image_bg.setBackgroundDrawable(bitmap);
//			iv_image_bg.setVisibility(View.VISIBLE);
		}

		@Override
		public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			String message = null;
			switch (failReason.getType()) {
			case IO_ERROR:
				message = "下载错误";
				break;
			case DECODING_ERROR:
				message = "图片无法显示";
				break;
			case NETWORK_DENIED:
				message = "网络有问题，无法下载";
				break;
			case OUT_OF_MEMORY:
				message = "图片太大无法显示";
				break;
			case UNKNOWN:
				message = "未知的错误";
				break;
			}
			Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
			progressBar.setVisibility(View.GONE);
			iv_image_bg.setVisibility(View.GONE);
		}

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

			progressBar.setVisibility(View.GONE);
			iv_image_bg.setVisibility(View.GONE);
			mAttacher.update();
			bitmapList.add(loadedImage);
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (releaseBitmapLoadingListener != null) {
			releaseBitmapLoadingListener.cleanBitmapList();
		}
	}
}
