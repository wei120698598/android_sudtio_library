package com.wei.view.customer.view.myView;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;

import com.wei.view.R;


public class CustomDateTimePick extends AlertDialog implements OnDateChangedListener {

	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String DAY = "day";

	private final DatePicker mDatePicker;
	private final OnDateSetListener mCallBack;
	private View view;

	/**
	 * The callback used to indicate the user is done filling in the date.
	 */
	public interface OnDateSetListener {
		void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth);
	}

	public CustomDateTimePick(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
		this(context, 0, callBack, year, monthOfYear, dayOfMonth);
	}

	public CustomDateTimePick(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
		super(context, theme);

		mCallBack = callBack;

		Context themeContext = getContext();
		// 將設置alertdialog的settitle和setButton方法注释掉，因为下面要使用setContentView（View
		// view）方法
		// 代替原有的setView（View
		// view）方法，区别是替换的范围不同。替换后就不需要设置alertdialog的title和button了。
		// setButton(BUTTON_POSITIVE,
		// themeContext.getText(R.string.date_time_set), this);
		// setButton(BUTTON_NEGATIVE, themeContext.getText(R.string.cancel),
		// (OnClickListener) null);
		// setIcon(0);
		// setTitle(R.string.date_picker_dialog_title);

		LayoutInflater inflater = (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 注意，其实datepickerdialog就是把系统提供的日期选择器DatePicker包装一下而已，所以只需要实现自己需要的布局样式
		// 然后把DatePicker嵌套进去就可以了。
		view = inflater.inflate(R.layout.date_picker_dialog, null);
		view.setBackgroundColor(Color.BLUE);
		// setView(view);
		mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
		mDatePicker.init(year, monthOfYear, dayOfMonth, this);

		// 实现自己的标题和ok按钮
		setTitle("选择日期:");
		setButton();
	}

	public void myShow() {
		// 自己实现show方法，主要是为了把setContentView方法放到show方法后面，否则会报错。
		show();
		setContentView(view);
	}

	private void setTitle(String title) {
		// 获取自己定义的title布局并赋值。
		((TextView) view.findViewById(R.id.date_picker_title)).setText(title);
	}

	private void setButton() {
		// 获取自己定义的响应按钮并设置监听，直接调用构造时传进来的CallBack接口（为了省劲，没有自己写接口，直接用之前本类定义好的）同时关闭对话框。
		view.findViewById(R.id.date_picker_ok).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCallBack != null) {
					mDatePicker.clearFocus();
					mCallBack.onDateSet(mDatePicker, mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
				}
				dismiss();
			}
		});
	}

	public void onDateChanged(DatePicker view, int year, int month, int day) {
		mDatePicker.init(year, month, day, null);
	}

	/**
	 * Gets the {@link DatePicker} contained in this dialog.
	 * 
	 * @return The calendar view.
	 */
	public DatePicker getDatePicker() {
		return mDatePicker;
	}

	public void updateDate(int year, int monthOfYear, int dayOfMonth) {
		mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
	}

	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt(YEAR, mDatePicker.getYear());
		state.putInt(MONTH, mDatePicker.getMonth());
		state.putInt(DAY, mDatePicker.getDayOfMonth());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int year = savedInstanceState.getInt(YEAR);
		int month = savedInstanceState.getInt(MONTH);
		int day = savedInstanceState.getInt(DAY);
		mDatePicker.init(year, month, day, this);
	}
}
