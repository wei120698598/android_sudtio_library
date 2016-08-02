package com.wei.view.customer.view.gridView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 自定义的“九宫格”——用在显示帖子详情的图片集合
 * 实现在高度为WRAP_CONTENT时，自动测量控件所需高度，使内容显示全
 *
 * @author wei
 */
public class NoScrollGridViewOnWrap extends GridView {

	private int heightSpec;

	public NoScrollGridViewOnWrap(Context context) {
		super(context);
	}

	public NoScrollGridViewOnWrap(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NoScrollGridViewOnWrap(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
			heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		} else {
			heightSpec = heightMeasureSpec;
		}

		super.onMeasure(widthMeasureSpec, heightSpec);
	}
}
