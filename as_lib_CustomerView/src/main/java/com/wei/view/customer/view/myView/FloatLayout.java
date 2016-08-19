package com.wei.view.customer.view.myView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.RelativeLayout;


/**
 * Created by wei on 2016/7/21.
 * <p/>
 * 悬浮按钮
 * <p/>
 * 可以通过windowManage添加view实现应用内部悬浮按钮
 * 可以通过在布局页面中使用自定义view实现页面内部悬浮按钮
 * 可以通过service添加view实现应用内外悬浮按钮
 */
public class FloatLayout extends RelativeLayout {
    private WindowManager mWindowManager;
    private Context mContext;
    private int mWindowHeight;
    private int mWindowWidth;
    private int statusBarHeight;
    private int mWidth;
    private int mHeight;

    private int marginTop = 10;// 默认距离屏幕顶部和底部的margin,单位dp

    private int marginLeft = 10; //  默认距离屏幕左部和右部的margin,单位dp

    private int marginPress = 10;// 点击时按钮显示距离屏幕距离,单位dp


    private boolean isDrag = true;
    private int marginTopP;
    private int marginLeftP;

    /**
     * 设置是否可拖拽
     *
     * @param drag
     */
    public void setDrag(boolean drag) {
        isDrag = drag;
        if (isDrag) {
            super.setOnClickListener(null);
        }
    }

    /**
     * 默认距离屏幕左部和右部的margin,单位dp
     *
     * @param marginLeft
     */
    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
        marginLeftP = (marginLeft == 0 ? 0 : dip2px(marginLeft));
    }

    /**
     * 默认距离屏幕顶部和底部的margin,单位dp
     *
     * @param marginTop
     */
    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
        marginTopP = (marginTop == 0 ? 0 : dip2px(marginTop));
    }

    public FloatLayout(Context context) {
        super(context);
        mContext = context;
    }

    public FloatLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public FloatLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        measure();
        setViewShowAll(false);
    }


    private void measure() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWindowWidth = mWindowManager.getDefaultDisplay().getWidth();
        mWindowHeight = mWindowManager.getDefaultDisplay().getHeight();
        Rect outRect = new Rect();
        ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        statusBarHeight = outRect.top;
        mWidth = getWidth();
        mHeight = getHeight();

        marginLeftP = marginLeft == 0 ? 0 : dip2px(marginLeft);

        marginTopP = marginTop == 0 ? 0 : dip2px(marginTop);


    }

    private float downX;
    private float downY;
    private float viewX = 0;
    private float viewY = 0;
    private boolean isMove = false;

    @SuppressLint("NewApi")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        long startTime = System.currentTimeMillis();
        if (!isDrag) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    setAlpha(1.0f);
                    break;
                case MotionEvent.ACTION_UP:
                    setAlpha(0.5f);
                    break;
            }
            return super.onTouchEvent(event);
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isMove = false;
                    downX = event.getRawX();
                    downY = event.getRawY();
                    setAlpha(1.0f);
                    setViewShowAll(true);
                    viewX = getX();
                    viewY = getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float moveY = event.getRawY();
                    float moveX = event.getRawX();
                    if (isMove || Math.abs(moveX - downX) > 10 || Math.abs(moveY - downY) > 10) {
                        setX(getX() + (moveX - downX));
                        setY(getY() + (moveY - downY));
                        downX = moveX;
                        downY = moveY;
                        isMove = true;
                        return true;
                    } else {
                        return false;
                    }
                case MotionEvent.ACTION_UP:
                    setAlpha(0.5f);

                    if (Math.abs(viewX - getX()) < 10 && Math.abs(viewY - getY()) < 10) {
                        if (mClickListener != null) {
                            mClickListener.onClick(this);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setViewShowAll(false);
                            }
                        }, 300);
                        return false;
                    } else {// 处理靠边悬浮
                        trantlateY();
                        return true;
                    }
                default:
                    break;
            }
            return true;
        }
    }

    @SuppressLint("NewApi")
    private void trantlateY() {
        float x = getX();
        float y = getY();

        if (y < mHeight + marginTopP) {// 竖直方向超出屏幕上边缘
            setY(marginTopP);
        } else if (y > mWindowHeight - statusBarHeight - marginTopP - mHeight) {// 竖直方向超出屏幕下边缘
            setY(mWindowHeight - marginTopP - mHeight - statusBarHeight);
        }

        if (x < marginLeftP || x <= (mWindowWidth - mWidth) / 2) {// 水平方向超出屏幕左边缘或者水平方向在左半屏幕
            setX(marginLeftP);
        } else if (x > mWindowWidth - marginLeftP - mWidth || x > (mWindowWidth - mWidth) / 2) {// 水平方向超出屏幕右边缘或者水平方向在右半屏幕
            setX(mWindowWidth - marginLeftP - mWidth);
        }
    }

    @SuppressLint("NewApi")
    private void setViewShowAll(boolean isShow) {
        float x = getX();
        if (x <= (mWindowWidth - mWidth) / 2) {// 水平方向在左半屏幕
            setX(isShow ? marginPress : marginLeft);
        } else if (x > (mWindowWidth - mWidth) / 2) {// 水平方向在右半屏幕
            setX(mWindowWidth - (isShow ? marginPress : marginLeft) - mWidth);
        }
    }


    private OnClickListener mClickListener = null;
    private OnLongClickListener mOnLongClickListener = null;

    @Override
    public void setOnClickListener(OnClickListener l) {
        if (!isDrag) {
            super.setOnClickListener(l);
        }
        mClickListener = l;
    }


    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        super.setOnLongClickListener(l);
        mOnLongClickListener = l;
    }

    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int px2dip(float pxValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
