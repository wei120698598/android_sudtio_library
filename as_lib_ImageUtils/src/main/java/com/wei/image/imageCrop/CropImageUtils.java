/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.wei.image.imageCrop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.wei.image.imageUtils.FileUtils;

import java.io.File;

import static com.wei.image.imageChoose.photoview.ImagePicker.PHOTO_CROP_CODE;
import static com.wei.image.imageChoose.photoview.ImagePicker.cropPicPath;

/**
 * Created by Wei on 2016/8/17.
 */

public class CropImageUtils {
    /**
     * 自定义工具裁剪图片
     * @param activity 上下文
     * @param uri 图片地址
     * @param aspectX x比例
     * @param aspectY y比例
     * @param outputX 宽度
     * @param outputY 高度
     * @param path 保存地址
     */
    public static void startPhotoZoom(Activity activity, Uri uri, float aspectX, float aspectY, int outputX, int outputY, String path) {
//		Intent intent = new Intent("com.android.camera.action.CROP");
//		Intent intent = new Intent(Intent.ACTION_PICK);
        Intent intent = new Intent(activity, CropImage.class);
        intent.setDataAndType(uri, "image/*");
        try {
            boolean createNewFile = true;
            createNewFile = FileUtils.createFile(FileUtils.cropPicDirPath, "tempCropPic.png");
            if (createNewFile) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(TextUtils.isEmpty(path) ? cropPicPath : path)));
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", aspectX);
                intent.putExtra("aspectY", aspectY);
                intent.putExtra("outputX", outputX);
                intent.putExtra("outputY", outputY);
                intent.putExtra("noFaceDetection", true);
                intent.putExtra("return-data", false);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                activity.startActivityForResult(intent, PHOTO_CROP_CODE);
            } else {
                Toast.makeText(activity, "创建缓存图片失败", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity,"裁剪图片失败",Toast.LENGTH_SHORT).show();
        }
    }

    public static void startPhotoZoom(Activity activity, Uri uri) {
        startPhotoZoom(activity, uri, 1f, 1f, 720, 720, null);
    }


    /**
     * 调用系统裁剪图片方法实现，
     *
     * @param uri
     */
    public static void startPhotoZoomByNative(Activity activity, Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 120);
        intent.putExtra("outputY", 120);
        //设置true直接返回bitmap，但是是缩略图，可以在activity中的onActivityResult中进行接收，设置false需要设置MediaStore.EXTRA_OUTPUT，如上
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, PHOTO_CROP_CODE);
    }
}
