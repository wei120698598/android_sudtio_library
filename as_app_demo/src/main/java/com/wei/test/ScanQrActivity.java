package com.wei.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wei.image.zxing.decoding.ScanQrUtils;
import com.wei.image.zxing.ui.MipcaCaptureActivity;
import com.wei.utils.ui.DensityUtil;


/**
 * Created by Wei on 2016/8/16.
 */

public class ScanQrActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private ImageView result_img;
    private TextView result_text;
    private ImageView qr_image;

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

        qr_image = new ImageView(this);
        qr_image.setImageBitmap(bitmap);
        linearLayout.addView(qr_image);

        result_img = new ImageView(this);
        result_text = new TextView(this);
        linearLayout.addView(result_text, -1, -2);
        linearLayout.addView(result_img);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ScanQrUtils.ScanQrResult scanQrResult = ScanQrUtils.activityResult(data, requestCode, resultCode, true);
        if (scanQrResult != null) {
            if (scanQrResult.getResultText() != null) {
                result_text.setText("扫描返回的结果：" + scanQrResult.getResultText());
                result_text.setGravity(Gravity.CENTER);
            }

            if (scanQrResult.getResultBitmap() != null) {
                result_img.setImageBitmap(scanQrResult.getResultBitmap());
            }
        }
    }
}
