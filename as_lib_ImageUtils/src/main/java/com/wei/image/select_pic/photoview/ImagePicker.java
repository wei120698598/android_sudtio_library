package com.wei.image.select_pic.photoview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.wei.image.R;
import com.wei.image.select_pic.utils.SampleSizeUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 图片选取 Created by dudc on 14-5-14.
 *
 * @author dudc
 */
public class ImagePicker {
    private static final String TAG = "ImagePicker";
    public static final int PHOTO_ALBUM_CODE = 1;// 本地
    public static final int PHOTO_CAMERA_CODE = 2;// 拍照
    public static final int PHOTO_CROP_CODE = 3;// 剪切
    public static final int PHOTO_ALBUM_KITKAT_CODE = 11;// 本地

    /**
     * 拍照
     *
     * @param activity
     * @param defaultPhotoDir
     * @return
     */
    public static String PickerFromCamera(Activity activity, String defaultPhotoDir) {
        String path = null;
        String state = Environment.getExternalStorageState();
        File file2 = new File(defaultPhotoDir);
        if (!file2.exists()) {
            file2.mkdirs();
        }
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File photoTakeFile = new File(defaultPhotoDir + getCreatePhotoFileName(File.pathSeparator + "IMG"));
            if (!photoTakeFile.exists()) {
                try {
                    photoTakeFile.createNewFile();
                    path = photoTakeFile.getPath();
                    Intent intentTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intentTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoTakeFile));
                    activity.startActivityForResult(intentTakePhoto, PHOTO_CAMERA_CODE);
                    activity.overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "图片创建失败！", Toast.LENGTH_SHORT).show();
                }
            }
            return path;
        } else {
            Toast.makeText(activity, "SDCard无效或没有插入！", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public static String getCreatePhotoFileName(String startStr) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'" + startStr + "'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpeg";
    }


    /**
     * 相册
     *
     * @param activity
     */
    public static void PickerFromAlbum(Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, PHOTO_ALBUM_CODE);
        activity.overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
    }

    /**
     * 返回结果
     *
     * @param activity
     * @param requestCode
     * @param data
     * @param photoPath
     * @return
     */
    @TargetApi(19)
    public static Bitmap PickerForResult(Activity activity, int requestCode, Intent data, String photoPath) {

        if (requestCode == PHOTO_ALBUM_CODE || requestCode == PHOTO_ALBUM_KITKAT_CODE) {
            if (data == null) {
                Log.e(TAG, "data is null");
                return null;
            }

			/*
             * String filePath = data.getStringExtra("single_path"); Log.e(TAG,
			 * "filePath = " + filePath); if (null != filePath &&
			 * !"".equals(filePath.trim())) { try { String newpath = new
			 * String(filePath.getBytes(), "UTF-8");
			 * 
			 * BitmapFactory.Options option = new BitmapFactory.Options();
			 * option.inJustDecodeBounds = true;
			 * BitmapFactory.decodeFile(newpath, option); option.inSampleSize =
			 * SampleSizeUtil.computeSampleSize(option, -1, 800 * 800);
			 * option.inJustDecodeBounds = false; Bitmap bitmap =
			 * BitmapFactory.decodeFile(newpath, option); if (bitmap != null)
			 * return bitmap;
			 * 
			 * } catch (OutOfMemoryError error) { error.printStackTrace(); }
			 * catch (UnsupportedEncodingException e) { e.printStackTrace(); }
			 * catch (Exception e) { e.printStackTrace(); }
			 * 
			 * 
			 * } else { Log.v(TAG, "filePath is null"); }
			 */
            Uri uri;
			/*
			 * if ( requestCode == PHOTO_ALBUM_KITKAT_CODE){ Uri selectedImage =
			 * data.getData(); String imagePath = getPath(activity,
			 * selectedImage); //获取图片的绝对路径 uri = Uri.parse("file:///" +
			 * imagePath); //将绝对路径转换为URL }else{ uri = data.getData(); }
			 */

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Uri selectedImage = data.getData();
                String imagePath = getPath(activity, selectedImage); // 获取图片的绝对路径
                uri = Uri.parse("file:///" + imagePath); // 将绝对路径转换为URL
            } else {
                uri = data.getData();
            }

            // Uri uri = data.getData();
            Log.e(TAG, "uri = " + uri);
            if (uri != null) {
                try {
                    String filepath = uri.getPath();

                    BitmapFactory.Options option = new BitmapFactory.Options();
                    option.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(filepath, option);
                    option.inSampleSize = SampleSizeUtil.computeSampleSize(option, -1, 800 * 800);
                    option.inJustDecodeBounds = false;
                    Bitmap bitmap = BitmapFactory.decodeFile(filepath, option);
                    if (bitmap != null)
                        return bitmap;
                } catch (OutOfMemoryError error) {
                    error.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else {
                Log.v(TAG, "uri is null");
            }

			/*
			 * if (uri != null) { try { ContentResolver resolver =
			 * activity.getContentResolver(); String[] pojo =
			 * {MediaStore.Images.Media.DATA}; Cursor cursor =
			 * activity.managedQuery(uri, pojo, null, null, null); if (cursor !=
			 * null) { int colunm_index =
			 * cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			 * cursor.moveToFirst(); String path =
			 * cursor.getString(colunm_index);
			 * 
			 * String newpath = new String(path.getBytes(), "UTF-8");
			 * 
			 * try { BitmapFactory.Options option = new BitmapFactory.Options();
			 * option.inJustDecodeBounds = true;
			 * BitmapFactory.decodeFile(newpath, option); option.inSampleSize =
			 * SampleSizeUtil.computeSampleSize(option, -1, 800 * 800);
			 * option.inJustDecodeBounds = false; Bitmap bitmap =
			 * BitmapFactory.decodeFile(newpath, option); if (bitmap != null)
			 * return bitmap; } catch (OutOfMemoryError error) {
			 * error.printStackTrace(); } } else { try { if (resolver != null) {
			 * if (uri.getPath() != null) try { BitmapFactory.Options option =
			 * new BitmapFactory.Options(); option.inJustDecodeBounds = true;
			 * BitmapFactory.decodeFile(uri.getPath(), option);
			 * option.inSampleSize = SampleSizeUtil.computeSampleSize(option,
			 * -1, 800 * 800); option.inJustDecodeBounds = false; Bitmap bitmap
			 * = BitmapFactory.decodeFile(uri.getPath(), option); if (bitmap !=
			 * null) return bitmap; } catch (OutOfMemoryError error) {
			 * error.printStackTrace(); } } else { Log.v(TAG,
			 * "resolver is null"); } } catch (OutOfMemoryError error) {
			 * error.printStackTrace(); } Log.v(TAG, "cursor is null"); } }
			 * catch (Exception e) { e.printStackTrace(); } } else { Log.v(TAG,
			 * "uri is null"); }
			 */
        }

        if (requestCode == PHOTO_CAMERA_CODE) {
            try {
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(photoPath, option);
                option.inSampleSize = SampleSizeUtil.computeSampleSize(option, -1, 800 * 800);
                option.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath, option);
                if (bitmap != null)
                    return bitmap;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public static void startPhotoZoom(Activity activity, Uri uri) {

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
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, PHOTO_CROP_CODE);
    }

    @TargetApi(19)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * 通过地址获取压缩图片
     *
     * @param filePath
     * @param inSampleSize
     * @return
     */
    public static Bitmap getBitmapThumb(String filePath, int inSampleSize) {
        try {
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, option);
            option.inSampleSize = inSampleSize;
            option.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, option);
            if (bitmap != null)
                return bitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return null;
    }

}
