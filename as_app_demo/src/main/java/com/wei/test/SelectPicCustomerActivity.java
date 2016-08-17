package com.wei.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wei.image.imageChoose.Bimp;
import com.wei.image.imageChoose.GridAlumAdapter;
import com.wei.image.imageChoose.ImageItem;
import com.wei.image.imageChoose.SelectPhotoPop;
import com.wei.image.imageChoose.photoview.ImagePicker;
import com.wei.image.imageChoose.photoview.PublicWay;
import com.wei.image.imageUtils.ImageCompressUtils2;
import com.wei.view.customer.view.gridView.NoScrollGridViewClickBlank;

import java.util.ArrayList;

/**
 * 图片选择测试页面,需要为此Activity配置singleTask
 * <p>
 * Created by wei on 2016/7/18.
 */
public class SelectPicCustomerActivity extends AppCompatActivity {

    //    @BindView(R.id.gridView)
    private NoScrollGridViewClickBlank gridView;
    private SelectPhotoPop selectPhotoPop;
    private GridAlumAdapter gridAlumAdapter;
    private ListView listView;
    private ArrayList<String> texts = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pic);
//        ButterKnife.bind(this);
        gridView = (NoScrollGridViewClickBlank) findViewById(R.id.gridView);
        listView = (ListView) findViewById(R.id.listView);
        selectPhotoPop = new SelectPhotoPop(this, SelectPhotoPop.CUSTOMER_ALUM);
        gridAlumAdapter = new GridAlumAdapter(this, gridView, selectPhotoPop);
        gridAlumAdapter.resume();
        gridView.setAdapter(gridAlumAdapter);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, texts);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gridAlumAdapter != null) {
            gridAlumAdapter.resume();
            texts.clear();
            for (ImageItem imageItem : Bimp.tempSelectBitmap) {
                texts.add(imageItem.getImagePath());
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        selectPhotoPop.clearBitmap();
        super.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ImagePicker.PHOTO_CAMERA_CODE:// 拍照
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
