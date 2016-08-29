package com.wei.view.customer.view.myView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class VerticalTextView extends TextView {

	private static final Boolean Debug = Boolean.valueOf(false);

	private static final String TAG = VerticalTextView.class.getSimpleName();
	private int mDirection = 3;// 向下滚动0,向左滚动2,向右滚动3,向上滚动1
	private int mScrollSpeed = 1; // 2高速,0低速,1中速

	private int mWholeLen = 0;
	private Paint mPaint = null;
	private final VerticalTextView.TextMoveHandler moveHandler = new VerticalTextView.TextMoveHandler(this);
	private float step = 0.0F;
	private String text = "";
	private List<String> textList = new ArrayList<>();
	private float width = 0.0F;
	private float y_coordinate = 0.0F;

	public VerticalTextView(Context context) {
		super(context);
		this.init((AttributeSet) null, 0);
	}

	public VerticalTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(attrs, 0);
	}

	public VerticalTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.init(attrs, defStyleAttr);
	}

	private void init(AttributeSet context, int attrs) {
		this.mDirection = 1;
		this.mScrollSpeed = 1;
		this.setOnTouchListener(new VerticalTextView.TextTouchListener());
	}

	private void update() {
		this.mPaint = this.getPaint();
		this.mPaint.setColor(this.getCurrentTextColor());
		this.text = this.getText().toString();
	}

	/**
	 * 设置滚动速度
	 */
	public void setScrollSpeed(float scrollSpeed) {
		if (scrollSpeed == 0.2) {
			this.mScrollSpeed = 0;
		} else if (scrollSpeed == 0.5) {
			this.mScrollSpeed = 0;
		} else if (scrollSpeed == 1) {
			this.mScrollSpeed = 1;
		} else if (scrollSpeed == 2) {
			this.mScrollSpeed = 2;
		} else if (scrollSpeed == 3) {
			this.mScrollSpeed = 2;
		} else {
			this.mScrollSpeed = 1;
		}
	}

	/**
	 * 设置滚动方向
	 * 
	 * @param direction
	 */
	public void setDirection(int direction) {
		this.mDirection = direction;
	}

	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (Debug.booleanValue()) {
			Log.d(TAG, "onAttachedToWindow");
		}

		if (this.moveHandler != null) {
			this.moveHandler.sendMessage(this.moveHandler.obtainMessage(0));
		}

	}

	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (Debug.booleanValue()) {
			Log.d(TAG, "onDetachedFromWindow");
		}

		if (this.moveHandler != null) {
			this.moveHandler.removeCallbacksAndMessages((Object) null);
		}

	}

	public void onDraw(Canvas canvas) {
		if (this.textList.size() != 0) {
			if (this.mDirection == 0) {
				for (int defStyleAttr = 0; defStyleAttr < this.textList.size(); ++defStyleAttr) {
					canvas.drawText((String) this.textList.get(defStyleAttr), 0.0F, (float) this.getHeight() + (float) (defStyleAttr + 1)
							* this.mPaint.getTextSize() - this.step, this.mPaint);
				}

				switch (this.mScrollSpeed) {
				case 0:
					this.step += 0.5F;
					break;
				case 1:
					++this.step;
					break;
				case 2:
					this.step += 2.0F;
				}

				if (this.step >= (float) this.getHeight() + (float) this.textList.size() * this.mPaint.getTextSize()) {
					this.step = 0.0F;
					return;
				}
			} else if (this.mDirection == 1) {
				for (int attrs = 0; attrs < this.textList.size(); ++attrs) {
					canvas.drawText((String) this.textList.get(attrs), 0.0F,
							(float) (-(this.textList.size() - attrs)) * this.mPaint.getTextSize() + this.step, this.mPaint);
				}

				switch (this.mScrollSpeed) {
				case 0:
					this.step += 0.5F;
					break;
				case 1:
					++this.step;
					break;
				case 2:
					this.step += 2.0F;
				}

				if (this.step >= (float) this.getHeight() + (float) this.textList.size() * this.mPaint.getTextSize()) {
					this.step = 0.0F;
					return;
				}
			} else {
				if (this.mDirection == 2) {
					if (this.step >= (float) (this.getWidth() + this.mWholeLen)) {
						this.step = 0.0F;
					}

					switch (this.mScrollSpeed) {
					case 0:
						this.step += 0.5F;
						break;
					case 1:
						++this.step;
						break;
					case 2:
						this.step += 2.0F;
					}

					canvas.drawText(this.text, (float) (-this.mWholeLen) + this.step, this.y_coordinate, this.mPaint);
					return;
				}

				if (this.mDirection == 3) {
					if (this.step >= (float) (this.getWidth() + this.mWholeLen)) {
						this.step = 0.0F;
					}

					switch (this.mScrollSpeed) {
					case 0:
						this.step += 0.5F;
						break;
					case 1:
						++this.step;
						break;
					case 2:
						this.step += 2.0F;
					}

					canvas.drawText(this.text, (float) this.getWidth() - this.step, this.y_coordinate, this.mPaint);
					return;
				}
			}
		}
	}

	protected void onMeasure(int context, int attrs) {
		super.onMeasure(context, attrs);
		if (Debug.booleanValue()) {
			Log.d(TAG, "onMeasure");
		}

		this.update();
		this.width = (float) MeasureSpec.getSize(context);
		if (MeasureSpec.getMode(context) != MeasureSpec.EXACTLY) {
			throw new IllegalStateException("ScrollLayout only can mCurScreen run at EXACTLY mode!");
		} else {
			float defStyleAttr = 0.0F;
			if (this.text != null && this.text.length() != 0) {
				this.textList.clear();
				StringBuilder stringbuilder = new StringBuilder();

				for (int i = 0; i < this.text.length(); ++i) {
					if (defStyleAttr < this.width) {
						stringbuilder.append(this.text.charAt(i));
						defStyleAttr += this.mPaint.measureText(this.text.substring(i, i + 1));
						if (i == -1 + this.text.length()) {
							this.textList.add(stringbuilder.toString());
						}
					} else {
						this.textList.add(stringbuilder.toString().substring(0, -1 + stringbuilder.toString().length()));
						stringbuilder.delete(0, -1 + stringbuilder.length());
						defStyleAttr = this.mPaint.measureText(this.text.substring(i, i + 1));
						--i;
					}
				}

				this.mWholeLen = 0;

				for (int k = 0; k < this.text.length(); ++k) {
					this.mWholeLen = (int) ((float) this.mWholeLen + this.mPaint.measureText(this.text.substring(k, k + 1)));
				}

				if (Debug.booleanValue()) {
					Log.d(TAG, "mWholeLen" + this.mWholeLen);
				}

				Paint.FontMetricsInt var7 = this.mPaint.getFontMetricsInt();
				this.y_coordinate = (float) (MeasureSpec.getSize(attrs) / 2 - (var7.bottom - var7.top) / 2 - var7.top);
				if (Debug.booleanValue()) {
					Log.d(TAG,
							"y_coordinate:\t" + this.y_coordinate + "\n" + "height:\t" + this.getHeight() + "\n" + "width:\t"
									+ this.getWidth() + "\n" + "measureWidth:\t" + MeasureSpec.getSize(context) + "\n" + "measureHeight:\t"
									+ MeasureSpec.getSize(attrs) + "\n");
					return;
				}
			}

		}
	}

	protected void onVisibilityChanged(View context, int attrs) {
		super.onVisibilityChanged(context, attrs);
		if (attrs != INVISIBLE && attrs != GONE) {
			if (attrs == VISIBLE) {
				if (Debug.booleanValue()) {
					Log.d(TAG, "onVisibilityChanged:\tVISIBLE");
				}

				if (this.moveHandler != null) {
					this.moveHandler.sendMessage(this.moveHandler.obtainMessage(0));
					return;
				}
			}
		} else {
			if (Debug.booleanValue()) {
				Log.d(TAG, "onVisibilityChanged:\tINVISIBLE/GONE");
			}

			if (this.moveHandler != null) {
				this.moveHandler.removeCallbacksAndMessages((Object) null);
			}
		}
	}

	private static class TextMoveHandler extends Handler {
		private final WeakReference<VerticalTextView> mScroll;

		public TextMoveHandler(VerticalTextView context) {
			this.mScroll = new WeakReference<VerticalTextView>(context);
		}

		public void handleMessage(Message context) {
			switch (context.what) {
			case 0:
				VerticalTextView attrs = (VerticalTextView) this.mScroll.get();
				if (attrs != null) {
					attrs.invalidate();
				}

				this.sendMessageDelayed(this.obtainMessage(0), 33L);
				return;
			default:
				super.handleMessage(context);
			}
		}
	}

	private class TextTouchListener implements OnTouchListener {
		private static final int DRAG_MODE = 1;
		private static final int NONE_MODE = 0;
		private int mMode;
		private PointF startPoint;
		private float start_step;

		private TextTouchListener() {
			this.startPoint = null;
			this.start_step = 0.0F;
			this.mMode = 0;
		}

		public boolean onTouch(View context, MotionEvent event) {
			switch (255 & event.getAction()) {
			case 0:
				if (VerticalTextView.Debug.booleanValue()) {
					Log.d(VerticalTextView.TAG, "ACTION_DOWN");
				}

				this.startPoint = new PointF(event.getX(), event.getY());
				this.start_step = VerticalTextView.this.step;
				this.mMode = 1;
				return true;
			case 1:
				if (VerticalTextView.Debug.booleanValue()) {
					Log.d(VerticalTextView.TAG, "ACTION_UP");
				}

				this.startPoint = null;
				this.start_step = 0.0F;
				this.mMode = 0;
				return true;
			case 2:
				if (VerticalTextView.Debug.booleanValue()) {
					Log.d(VerticalTextView.TAG, "ACTION_MOVE");
				}

				if (this.mMode == 1) {
					int direction = VerticalTextView.this.mDirection;
					float i = 0.0F;
					switch (direction) {
					case 0:
						i = this.startPoint.y - event.getY();
						break;
					case 1:
						i = event.getY() - this.startPoint.y;
						break;
					case 2:
						i = event.getX() - this.startPoint.x;
						break;
					case 3:
						i = this.startPoint.x - event.getX();
					}

					VerticalTextView.this.step = i + this.start_step;
					VerticalTextView.this.invalidate();
					return true;
				}
			default:
				return true;
			}
		}
	}
}
