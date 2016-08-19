package com.wei.view.customer.view.myView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.wei.view.R;
/**
 * 圆角ImageView
 * 
 * @author skg
 * 
 */
public class RoundImageView extends ImageView {

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0, 0);
	}

	public RoundImageView(Context context) {
		super(context);
		init(context, null, 0, 0);
	}

	@SuppressLint("NewApi")
	public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context, attrs, defStyleAttr, defStyleRes);
	}

	public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr, 0);
	}

	private final RectF roundRect = new RectF();
	private float rect_adius = 0;
	private final Paint maskPaint = new Paint();
	private final Paint zonePaint = new Paint();

	private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		maskPaint.setAntiAlias(true);
		maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		//
		zonePaint.setAntiAlias(true);
		zonePaint.setColor(Color.WHITE);
		//
		float density = getResources().getDisplayMetrics().density;
		rect_adius = rect_adius * density;

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChangeRectAdius);

		rect_adius = a.getDimensionPixelSize(R.styleable.ChangeRectAdius_rect_adius, Math.round(rect_adius));
		a.recycle();
	}

	public void setRectAdius(float adius) {
		rect_adius = adius;
		invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int w = getWidth();
		int h = getHeight();
		roundRect.set(0, 0, w, h);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.saveLayer(roundRect, zonePaint, Canvas.ALL_SAVE_FLAG);
		canvas.drawRoundRect(roundRect, rect_adius, rect_adius, zonePaint);
		//
		canvas.saveLayer(roundRect, maskPaint, Canvas.ALL_SAVE_FLAG);
		super.draw(canvas);
		canvas.restore();
	}

}