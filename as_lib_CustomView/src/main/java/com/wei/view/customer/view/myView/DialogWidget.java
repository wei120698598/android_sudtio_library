package com.wei.view.customer.view.myView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * 
 * @author LanYan
 * 
 */
public class DialogWidget extends Dialog {
	Activity activity;
	private View view;
	private boolean isOutSideTouch = false;

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public boolean isOutSideTouch() {
		return isOutSideTouch;
	}

	private void setOutSideTouch(boolean isOutSideTouch) {
		this.isOutSideTouch = isOutSideTouch;
	}

	public DialogWidget(Context context, int theme) {
		super(context, theme);
	}

	public DialogWidget(Context context) {
		this(context, 0);
	}

	public DialogWidget(Activity activity, View view) {
		super(activity, android.R.style.Theme_Dialog);
		this.activity = activity;
		this.view = view;
	}

	public DialogWidget(Activity activity, View view, int theme) {
		super(activity, theme);
		this.activity = activity;
		this.view = view;
	}

	public DialogWidget(Activity activity, View view, int theme, boolean isOutSide) {
		super(activity, theme);
		this.activity = activity;
		this.view = view;
		this.isOutSideTouch = isOutSide;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(view);
		setCancelable(isOutSideTouch);
		setCanceledOnTouchOutside(isOutSideTouch);
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();
		layoutParams.width = screenWidth;
		layoutParams.height = screenHeight - 60;
		this.getWindow().setAttributes(layoutParams);
	}

}
