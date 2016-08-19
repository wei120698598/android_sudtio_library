package com.wei.view.customer.view.myView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Created by wei on 2016/7/21.
 */
public class TipsLayout extends RelativeLayout {

    private Context context;
    private TextView tv_content;

    public TipsLayout(Context context) {
        super(context);
        setView(context);
    }

    public TipsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setView(context);
    }

    public TipsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setView(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void setView(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_tips, null);
        int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        view.setLayoutParams(new ViewGroup.LayoutParams(widthPixels, 40));
        addView(view);
        tv_content = (TextView) view.findViewById(R.id.tv_content);
    }


    public void setContent(String text) {
        tv_content.setText(text);
    }

}
