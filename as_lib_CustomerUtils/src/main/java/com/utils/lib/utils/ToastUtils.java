package com.utils.lib.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.utils.lib.R;


public class ToastUtils {
	private static Toast toast;
	private static Toast toast1;


	/**
	 * 显示toast
	 * @param context
	 * @param msg
	 * @return
     */
	public static Toast showToast(final Context context, final String msg) {
		try {
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(new Runnable() {
				public void run() {
					if (toast == null) {
						toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
					} else {
						toast.setText(msg);
					}
					toast.show();
				}
			});
			return toast;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}





	public static void showNumberAddress(final Context context, final String msg) {
		final View toastView;
		final WindowManager wm = null;
		final WindowManager.LayoutParams params = new WindowManager.LayoutParams();

		// 自定义的toast
		toastView = View.inflate(context, R.layout.toast_address, null);
		// 设置参数
		params.height = WindowManager.LayoutParams.WRAP_CONTENT; // 包括内容
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;

		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;

		// 设置窗体类型
		// params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		params.setTitle("Toast");

		// 设置当前窗口的位置
		params.gravity = Gravity.LEFT + Gravity.TOP;

		// 调用此toast
		wm.addView(toastView, params);

		// 监听触摸
		toastView.setOnTouchListener(new View.OnTouchListener() {
			private int downX;
			private int downY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					downX = (int) event.getRawX();
					downY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					// 移动距离
					int disX = (int) event.getRawX() - downX;
					int disY = (int) event.getRawY() - downY;
					// 更新view位置坐标
					params.x += disX;
					params.y += disY;
					// 刷新view位置
					wm.updateViewLayout(toastView, params);
					// 重置按下位置坐标
					downX = (int) event.getRawX();
					downY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					break;
				}
				return true;
			}
		});
		toastView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PopupWindow pop = new PopupWindow(context);
				TextView view = new TextView(context);
				view.setText(msg);
				TranslateAnimation ra = new TranslateAnimation(0, 0, 0, Animation.RELATIVE_TO_SELF);
				ra.setDuration(300);
				view.startAnimation(ra);
				pop.setWidth(LayoutParams.WRAP_CONTENT);
				pop.setHeight(LayoutParams.WRAP_CONTENT);
				pop.setBackgroundDrawable(new BitmapDrawable());
				pop.setFocusable(true);
				pop.setOutsideTouchable(true);
				pop.setContentView(view);
				pop.showAsDropDown(v);
			}
		});

	}
}
