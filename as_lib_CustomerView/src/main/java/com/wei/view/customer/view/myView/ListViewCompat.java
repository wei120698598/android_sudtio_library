package com.wei.view.customer.view.myView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class ListViewCompat extends ListView {

	private static final String TAG = "ListViewCompat";

	private SlideView mFocusedItemView;

	private int x;

	private int y;
	private int type = 0;

	private int position;

	public ListViewCompat(Context context) {
		super(context);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ListViewCompat(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListViewCompat(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void shrinkListItem(int position) {
		View item = getChildAt(position);

		if (item != null) {
			try {
				((SlideView) item).shrink();
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			x = (int) event.getX();
			y = (int) event.getY();

			break;
		case MotionEvent.ACTION_MOVE:
			int disX = (int) Math.abs(event.getX() - x);
			int disY = (int) Math.abs(event.getY() - y);

			if (disX > disY && disX > 20) {
				position = pointToPosition(x, y);
				Log.e(TAG, "postion=" + position);

				if (position != INVALID_POSITION) {
					if (type == 0) {
					} else if (type == 1) {
					} else if (type == 2) {
					}
				}
			} else {
				mFocusedItemView = null;
			}
			break;
		case MotionEvent.ACTION_UP:
			break;
		}

		if (mFocusedItemView != null) {
			mFocusedItemView.onRequireTouchEvent(event);
			this.clearFocus();
			return true;
		} else {
			this.clearFocus();
			return super.onTouchEvent(event);
		}
	}

}
