/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.wei.test;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.wei.image.imageCrop.CropImageUtils;
import com.wei.image.imageUtils.ImageUtils2;
import com.wei.image.imageChoose.Bimp;
import com.wei.image.imageChoose.GridAlumAdapter;
import com.wei.image.imageChoose.ImageItem;
import com.wei.image.imageChoose.SelectPhotoPop;
import com.wei.image.imageChoose.photoview.ImagePicker;
import com.wei.view.customer.view.gridView.NoScrollGridViewClickBlank;

import java.io.File;
import java.util.ArrayList;


/**
 * 使用系统原生图片选择器进行图片选择，支持图片压缩，获取缩略图，方形裁剪，圆形裁剪，自定义裁剪框比例、大小
 *
 *
 * Created by Wei on 2016/8/16.
 */

public class SelectPicNativeActivity extends AppCompatActivity {
    private NoScrollGridViewClickBlank gridView;
    private SelectPhotoPop selectPhotoPop;
    private GridAlumAdapter gridAlumAdapter;
    private ListView listView;
    private ArrayList<String> texts = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private Uri uri;

    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pic);
        gridView = (NoScrollGridViewClickBlank) findViewById(R.id.gridView);
        imageView = (ImageView) findViewById(R.id.imageView);
        listView = (ListView) findViewById(R.id.listView);
        selectPhotoPop = new SelectPhotoPop(this, SelectPhotoPop.NATIVE_ALUM);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.PHOTO_ALBUM_CODE) {// ablum
            if (data == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Uri selectedImage = data.getData();
                String imagePath = ImagePicker.getPath(this, selectedImage); // 获取图片的绝对路径
                uri = Uri.parse("file:///" + imagePath); // 将绝对路径转换为URL
            } else {
                uri = data.getData();
            }
            if (uri != null) {
//                ImagePicker.startPhotoZoom(this, uri);
                String imagePath = ImagePicker.getPath(this, uri);
                File file = new File(imagePath);
                if (file.exists()) {
                    ImageItem imageItem = new ImageItem();
                    imageItem.setImagePath(imagePath);
                    imageItem.setBitmap(ImageUtils2.getThumbnailBitmap(this, imageItem.getImagePath()));
                    Bimp.tempSelectBitmap.add(imageItem);
                    texts.add(imagePath);
                    adapter.notifyDataSetChanged();
                }
            }
        } else if (requestCode == ImagePicker.PHOTO_CAMERA_CODE) { // camera
            uri = SelectPhotoPop.photoUri;
            CropImageUtils.startPhotoZoom(this, uri);
        } else if (requestCode == ImagePicker.PHOTO_CROP_CODE) {// crop
            File file = new File(ImagePicker.cropPicPath);
            if (file.exists()) {
                ImageItem imageItem = new ImageItem();
                imageItem.setImagePath(ImagePicker.cropPicPath);
                imageItem.setBitmap(ImageUtils2.getThumbnailBitmap(this, imageItem.getImagePath()));
                Bimp.tempSelectBitmap.add(imageItem);
                texts.add(ImagePicker.cropPicPath);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
