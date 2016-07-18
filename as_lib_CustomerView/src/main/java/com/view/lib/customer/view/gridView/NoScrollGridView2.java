package com.view.lib.customer.view.gridView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 自定义的“九宫格”——用在显示帖子详情的图片集合 解决的问题：GridView显示不全，只显示了一行的图片，比较奇怪，尝试重写GridView来解决
 * 
 * @author lichao
 * @since 2014-10-16 16:41
 * 
 */
public class NoScrollGridView2 extends GridView {

	private int heightSpec;

	public NoScrollGridView2(Context context) {
		super(context);
	}

	public NoScrollGridView2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NoScrollGridView2(Context context, AttributeSet attrs, int defStyle) {
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
