package com.wei.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wei.image.select_pic.Bimp;
import com.wei.image.select_pic.GridAlumAdapter;
import com.wei.image.select_pic.ImageItem;
import com.wei.image.select_pic.SelectPhotoPop;
import com.wei.image.select_pic.photoview.ImagePicker;
import com.wei.image.select_pic.photoview.PublicWay;
import com.wei.utils.imageUtils.ImageCompressUtils2;
import com.wei.view.customer.view.gridView.NoScrollGridViewClickBlank;

/**
 * 图片选择测试页面,需要为此Activity配置singleTask
 * Created by wei on 2016/7/18.
 */
public class SelectPicActivity extends AppCompatActivity {

    //    @BindView(R.id.gridView)
    NoScrollGridViewClickBlank gridView;
    private SelectPhotoPop selectPhotoPop;
    private GridAlumAdapter gridAlumAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pic);
//        ButterKnife.bind(this);
        gridView = (NoScrollGridViewClickBlank) findViewById(R.id.gridView);
        selectPhotoPop = new SelectPhotoPop(this, SelectPhotoPop.CUSTOMER_ALUM);
        gridAlumAdapter = new GridAlumAdapter(this, gridView, selectPhotoPop);
        gridAlumAdapter.resume();
        gridView.setAdapter(gridAlumAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gridAlumAdapter != null){
            gridAlumAdapter.resume();
        }
    }

    @Override
    protected void onDestroy() {
        selectPhotoPop.clearBitmap();
        super.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ImagePicker.PHOTO_ALBUM_CODE:
            case ImagePicker.PHOTO_CAMERA_CODE:// 照片
                if (Bimp.tempSelectBitmap.size() < PublicWay.num && resultCode == RESULT_OK) {
                    String imagePath2 = ImagePicker.getPath(this, SelectPhotoPop.photoUri);
                    ImageItem takePhoto = new ImageItem();
                    Bitmap bitmap = new ImageCompressUtils2().getimage(imagePath2);
                    if (bitmap == null) {
                        return;
                    }
                    takePhoto.setBitmap(bitmap);
                    takePhoto.setImagePath(imagePath2);
                    takePhoto.setImageName("");
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;

        }
    }
}
