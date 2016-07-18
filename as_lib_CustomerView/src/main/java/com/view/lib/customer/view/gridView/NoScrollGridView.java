package com.view.lib.customer.view.gridView;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * 自定义的“九宫格”——用在显示帖子详情的图片集合 解决的问题：GridView显示不全，只显示了一行的图片，比较奇怪，尝试重写GridView来解决
 * 
 * @author lichao
 * @since 2014-10-16 16:41
 * 
 */
public class NoScrollGridView extends GridView {

	private int heightSpec;
	private OnTouchInvalidPositionListener mTouchInvalidPosListener;

	public NoScrollGridView(Context context) {
		super(context);
		setCacheColorHint(Color.TRANSPARENT);
	}

	public NoScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setCacheColorHint(Color.TRANSPARENT);
	}

	public NoScrollGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setCacheColorHint(Color.TRANSPARENT);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		// if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
		// heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
		// MeasureSpec.AT_MOST);
		// } else {
		// heightSpec = heightMeasureSpec;
		// }

		super.onMeasure(widthMeasureSpec, heightSpec);
	}

	
	
	
	public interface OnTouchInvalidPositionListener {
		/**
		 * motionEvent 可使用 MotionEvent.ACTION_DOWN 或者
		 * MotionEvent.ACTION_UP等来按需要进行判断
		 * 
		 * @return 是否要终止事件的路由
		 */
		boolean onTouchInvalidPosition(int motionEvent);
	}

	/**
	 * 点击空白区域时的响应和处理接口
	 */
	public void setOnTouchInvalidPositionListener(OnTouchInvalidPositionListener listener) {
		mTouchInvalidPosListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mTouchInvalidPosListener == null) {
			return super.onTouchEvent(event);
		}
		if (!isEnabled()) {
			// A disabled view that is clickable still consumes the touch
			// events, it just doesn't respond to them.
			return isClickable() || isLongClickable();
		}
		final int motionPosition = pointToPosition((int) event.getX(), (int) event.getY());
		if (motionPosition == INVALID_POSITION) {
			super.onTouchEvent(event);
			return mTouchInvalidPosListener.onTouchInvalidPosition(event.getActionMasked());
		}
		return super.onTouchEvent(event);
	}
}
