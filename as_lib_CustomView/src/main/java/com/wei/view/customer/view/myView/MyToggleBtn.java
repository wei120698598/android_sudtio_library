package com.wei.view.customer.view.myView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import com.wei.view.R;



public class MyToggleBtn extends View implements OnClickListener {

	private Bitmap bgBitmap;

	private Bitmap slideBitmap;

	private Paint paint;

	/**
	 * 在布局文件中声明该控件时，调用此方法
	 * 
	 * @param context
	 * @param attrs
	 */
	public MyToggleBtn(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		bgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_point);
		slideBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_point);

		// 滑动图片，左边界的最大值
		slideLeftMax = bgBitmap.getWidth() - slideBitmap.getWidth();
		slideLeft = slideLeftMax;
		paint = new Paint();
		paint.setAntiAlias(true);// 抗矩齿

		// 添加点击事件

		setOnClickListener(this);

	}

	/**
	 * 当前的开关状态 true 为开 false 为关
	 */
	private boolean currState = true;

	public boolean isCurrState() {
		return currState;
	}

	public void setCurrState(boolean currState) {
		this.currState = currState;
	}

	private int slideLeft = 0;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 绘制背景图
		canvas.drawBitmap(bgBitmap, 0, 0, paint);

		// 绘制滑动图片
		canvas.drawBitmap(slideBitmap, slideLeft, 0, paint);

	}

	@Override
	/**
	 * 指定view的大小
	 */
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// 当前view的宽度，就和背景图的大小一致
		int measuredWidth = bgBitmap.getWidth();
		int measuredHeight = bgBitmap.getHeight();

		// 指定测量的宽高
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	@Override
	/**
	 * 响应view的点击事件
	 */
	public void onClick(View v) {
		// 如果发生了滑动的动作，就不执行以下代码
		if (isSliding) {
			return;
		}

		// 切换按钮的开关状态
		currState = !currState;
		flushState();
	}

	/**
	 * 刷新状态 根据当前的状态，刷新页面
	 */
	private void flushState() {
		if (currState) {// 开状态
			slideLeft = slideLeftMax;
		} else {
			// 关状态
			slideLeft = 0;
		}
		flushView();
	}

	/**
	 * 上一个事件中的X坐标
	 */
	private int lastX;
	/**
	 * down事件中的X坐标
	 */
	private int downX;

	private int slideLeftMax;

	/**
	 * 判断触摸时，是否发生滑动事件
	 */
	private boolean isSliding = false;

	@Override
	/**
	 * 重写该方法，处理触摸事件
	 * 如果该view消费了事件，那么，返回true
	 */
	public boolean onTouchEvent(MotionEvent event) {
		// super 注释掉以后，onclick事件，就失效了，因为，点击这个动作，也是从onTouchEvent
		// 方法中解析出来，符合一定的要求，就是一个点击事件
		// 系统中，如果发现，view产生了up事件，就认为，发生了onclick动作,就行执行listener.onClick方法
		super.onTouchEvent(event);
		/*
		 * 点击切换开关，与触摸滑动切换开关，就会产生冲突
		 * 我们自己规定，如果手指在屏幕上移动，超过15个象素，就按滑动来切换开关，同时禁用点击切换开关的动作
		 */
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			downX = lastX = (int) event.getX(); // 获得相对于当前view的坐标
			// event.getRawX(); // 是相对于屏幕的坐标
			// down 事件发生时，肯定不是滑动的动作
			isSliding = false;
			break;
		case MotionEvent.ACTION_MOVE:

			// 获得距离
			int disX = (int) (event.getX() - lastX);
			// 改变滑动图片的左边界
			slideLeft += disX;
			flushView();

			// 为lastX重新赋值
			lastX = (int) event.getX();

			// 判断是否发生滑动事件
			if (Math.abs(event.getX() - downX) > 15) { // 手指在屏幕上滑动的距离大于15象素
				isSliding = true;
			}

			break;
		case MotionEvent.ACTION_UP:
			// 只有发生了滑动，才执行以下代码
			if (isSliding) {

				// 如果slideLeft > 最大值的一半 当前是开状态
				// 否则就是关的状态
				if (slideLeft > slideLeftMax / 2) { // 开状态
					currState = true;
				} else {
					currState = false;
				}
				flushState();
			}
			break;
		}
		return true;
	}

	/**
	 * 刷新页面
	 */
	private void flushView() {
		// 保证 slideLeft >=0 同时 <= slideLeftMax
		if (slideLeft < 0) {
			slideLeft = 0;
		}
		if (slideLeft > slideLeftMax) {
			slideLeft = slideLeftMax;
		}
		invalidate();// 刷新页面
	}

}
