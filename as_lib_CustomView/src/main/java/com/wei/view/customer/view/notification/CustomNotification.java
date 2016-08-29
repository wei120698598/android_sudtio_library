package com.wei.view.customer.view.notification;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.wei.view.R;


/**
 * Created by wei on 2016/7/22.
 */
public class CustomNotification {

    private String content;
    private View.OnClickListener clickListener;


    private WindowManager wm;
    private WindowManager.LayoutParams params;
    private Handler handler;
    private Context mContext;
    private TipsLayout toastView;
    private Runnable dismissRunnable;

    private CustomNotification(Context context) {
        this.mContext = context;
        initNotifi();
    }


    private static CustomNotification instance;

    public static CustomNotification getInstance(Context context) {
        if (instance == null) {
            synchronized (CustomNotification.class) {
                if (instance == null) {
                    instance = new CustomNotification(context);
                }
            }
        }
        return instance;
    }

    private void initNotifi() {
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        handler = new Handler(Looper.getMainLooper());

        // 设置参数
        params.height = WindowManager.LayoutParams.WRAP_CONTENT; // 包括内容
        params.width = WindowManager.LayoutParams.MATCH_PARENT;

        params.flags = WindowManager.LayoutParams.SCREEN_ORIENTATION_CHANGED | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = R.style.NotificationAnim;
        // 设置窗体类型
//         params.type = WindowManager.LayoutParams.TYPE_TOAST;
//         params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        params.setTitle("Toast");

        // 设置当前窗口的位置
        params.gravity = Gravity.LEFT + Gravity.TOP;

        dismissRunnable = new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        };

        toastView = new TipsLayout(mContext);
        // 监听触摸
        toastView.setOnTouchListener(new View.OnTouchListener() {
            private int downY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downY = (int) event.getRawY();
                        if (isAutoDisimiss)
                            handler.removeCallbacks(dismissRunnable);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 移动距离
                        int disY = (int) event.getRawY() - downY;
                        // 更新view位置坐标
                        if (disY < 0 || params.y + disY <= 0) {
                            params.y += disY;
                            wm.updateViewLayout(toastView, params);
                            downY = (int) event.getRawY();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (Math.abs(params.y) <= 3 && onClickListener != null) {
                            onClickListener.onClick(v);
                        }
                        if (params.y < -v.getHeight() / 2) {
                            dismiss();
                        } else {
                            params.y = 0;
                            wm.updateViewLayout(toastView, params);
                            if (isAutoDisimiss) {
                                handler.postDelayed(dismissRunnable, 3000);
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    private boolean isShow = false;
    private boolean isAutoDisimiss = false;
    private View.OnClickListener onClickListener;

    public void show(final boolean isAutoDisimiss) {
        this.isAutoDisimiss = isAutoDisimiss;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (!isShow) {
                    params.y = 0;
                    wm.addView(toastView, params);
                }
                if (isAutoDisimiss)
                    handler.postDelayed(dismissRunnable, 2000);
                isShow = true;
            }
        });
    }


    public CustomNotification updateView(String content, View.OnClickListener onClickListener) {
        if (toastView != null) {
            if (content != null)
                toastView.setContent(content);
            this.onClickListener = onClickListener;
            handler.removeCallbacks(dismissRunnable);
        }
        return instance;
    }

    private void dismiss() {
        wm.removeView(toastView);
        isShow = false;
    }
}
