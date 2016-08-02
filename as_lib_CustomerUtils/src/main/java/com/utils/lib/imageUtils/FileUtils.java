package com.utils.lib.imageUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.utils.lib.utils.ToastUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    /**
     * 图片缓存路径
     */
    public static String bitmap_cache;
    /**
     * 图片临时缓存路径，用过可能会被立即清空
     */
    public static String bitmap_temp;
    /**
     * 图片裁剪缓存路径
     */
    public static String cropPicDirPath;


    /**
     * 初始化应用常用图片存储路径
     *
     * @param context
     */
    public static void initCacheDir(Context context) {
        String packageName = context.getPackageName();
        bitmap_cache = context.getExternalCacheDir() + "/" + packageName + "/bitmap_cache/";
        bitmap_temp = context.getExternalCacheDir() + "/" + packageName + "/bitmap_temp/";
        cropPicDirPath = context.getExternalCacheDir() + "/" + packageName + "/crop/";
    }


    /**
     * 保存bitMap到本地的图册中
     *
     * @param context
     * @param bitmap
     * @param desc    图片描述
     * @param name    图片名称
     * @return 保存成功与否
     */
    public static boolean savePicBySystem(Context context, Bitmap bitmap, String name, String desc) {
        String url = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, name, desc);
        if (url != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(url)));
            } else {
                // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath()
                // + "/" + fileName
                MediaScannerConnection.scanFile(context, new String[]{ImageUtils.uri2Path(context, Uri.parse(url))}, null, null);
            }

            return true;
        }
        return false;
    }

    /**
     * 清空图片临时缓存的目录
     */
    public static void delDirTemp() {
        File dir = new File(FileUtils.bitmap_temp);
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.exists() && file.isFile()) {
                    file.delete();
                }
            }
        }
    }

    /**
     * 保存bitmap到本地
     *
     * @param context
     * @param bm
     * @param path
     * @param picName
     * @return
     */
    public static String saveBitmap(Context context, Bitmap bm, String path, String picName) {

        try {
            if (bm == null) {
                return null;
            }
            if (TextUtils.isEmpty(path)) {
                path = bitmap_temp;
            }
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                ToastUtils.showToast(context, "外置储存无效");
                return null;
            } else {
                File filePath = new File(path);
                if (!filePath.exists()) {
                    filePath.mkdirs();
                }
                File destFile = new File(path, picName);
                if (destFile.exists()) {
                    destFile.delete();
                }
                FileOutputStream out = new FileOutputStream(destFile);
                bm.compress(Bitmap.CompressFormat.PNG, 80, out);
                out.flush();
                out.close();
                if (bm != null && !bm.isRecycled()) {
                    bm.recycle();
                }
                return path + picName;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除指定文件从bitmap_cache
     *
     * @param fileName
     */
    public static void delFile(String fileName) {
        File file = new File(bitmap_cache + fileName);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.exists();
    }

    /**
     * 清除bitmap_cache缓存
     */
    public static void deleteDir() {
        File dir = new File(bitmap_cache);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory())
                deleteDir();
        }
        dir.delete();
    }

    /**
     * 判断文件或者文件夹是否存在
     *
     * @param path
     * @return
     */
    public static boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }


    /**
     * 创建文件
     *
     * @param targetPath
     * @param targetName
     * @return
     */
    public static boolean createFile(String targetPath, String targetName) {
        File filePath = new File(targetPath);
        boolean mkdirs = true;
        boolean delete = true;
        if (!filePath.exists()) {
            mkdirs = filePath.mkdirs();
        }
        File destFile = new File(targetPath, targetName);
        if (mkdirs && destFile.exists()) {
            delete = destFile.delete();
        }
        return delete;
    }

    /**
     * 创建文件在系统根目录，主要用来升级app使用
     *
     * @param app_name
     */
    public static boolean createFile(String app_name) {

        File updateDir = null;
        File updateFile = null;
        boolean isCreateFileSucess;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            isCreateFileSucess = true;
            updateDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            updateFile = new File(updateDir + "/" + app_name);
            if (!updateDir.exists()) {
                updateDir.mkdirs();
            }
            if (!updateFile.exists()) {
                try {
                    updateFile.createNewFile();
                } catch (IOException e) {
                    isCreateFileSucess = false;
                    e.printStackTrace();
                }
            }

        } else {
            isCreateFileSucess = false;
        }
        return isCreateFileSucess;
    }

    /**
     * 复制单个文件,优先使用oldPath，在olapath为空时使用oldfile
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(Context context, File oldFile, String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            if (oldFile == null && !TextUtils.isEmpty(oldPath)) {
                oldFile = new File(oldPath);
            } else {
                oldPath = oldFile.getAbsolutePath();
            }
            File file = new File(newPath);
            String path = newPath.substring(0, newPath.lastIndexOf(File.separator));

            File fileDir = new File(path);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            if (file.isFile() && file.exists()) {
                file.delete();
            }
            if (oldFile.exists() && oldFile.canRead()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.flush();
                fs.close();
            } else {
                Toast.makeText(context, "文件读写出错", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "复制单个文件操作出错", Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }

    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }
}
