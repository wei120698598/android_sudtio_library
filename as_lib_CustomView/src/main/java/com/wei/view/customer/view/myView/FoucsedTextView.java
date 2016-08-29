package com.wei.view.customer.view.myView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class FoucsedTextView extends TextView {

	public FoucsedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FoucsedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FoucsedTextView(Context context) {
		super(context);
	}

	@Override
	public boolean isFocused() {
		return true;
	}

}
