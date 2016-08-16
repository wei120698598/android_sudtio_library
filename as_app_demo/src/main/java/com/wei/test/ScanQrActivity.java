package com.wei.test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wei.utils.ui.DensityUtil;
import com.wei.view.customer.zxing.decoding.ScanQrUtils;
import com.wei.view.customer.zxing.ui.MipcaCaptureActivity;

/**
 * Created by Wei on 2016/8/16.
 */

public class ScanQrActivity extends Activity {

    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        Button button = new Button(this);
        button.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        button.setText("扫描");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MipcaCaptureActivity.startActivity(ScanQrActivity.this, true);
            }
        });
        linearLayout.addView(button);
        setContentView(linearLayout);
        Bitmap bitmap = ScanQrUtils.createQRImage("我是测试内容", DensityUtil.dip2px(this, 300), 0xffff0000, 0xff00ff00, -1, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        linearLayout.addView(imageView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ScanQrUtils.ScanQrResult scanQrResult = ScanQrUtils.activityResult(data, requestCode, resultCode, true);
        if (scanQrResult != null) {
            if (scanQrResult.getResultText() != null) {
                TextView textView = new TextView(this);
                textView.setText("扫描返回的结果：" + scanQrResult.getResultText());
                textView.setGravity(Gravity.CENTER);
                linearLayout.addView(textView, -1, -2);
            }

            if (scanQrResult.getResultBitmap() != null) {
                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(scanQrResult.getResultBitmap());
                linearLayout.addView(imageView);
            }
        }
    }
}
