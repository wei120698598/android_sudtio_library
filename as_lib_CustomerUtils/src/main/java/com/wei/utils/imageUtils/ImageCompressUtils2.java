package com.wei.utils.imageUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ImageCompressUtils2 {
    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 1024 * 2) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        try {
            isBm.close();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public Bitmap getimage(String srcPath) {
        try {
            if (srcPath == null) {
                return null;
            }
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inPreferredConfig = Config.RGB_565;
            newOpts.inDither = false;
            newOpts.inPurgeable = true;
            newOpts.inTempStorage = new byte[12 * 1024];
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
            float hh = 720f;// 这里设置高度为800f
            float ww = 720f;// 这里设置宽度为480f
            // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int be = 1;// be=1表示不缩放
            if (w > h && h > hh) {// 如果宽度大的话根据宽度固定大小缩放
                be = (int) (newOpts.outHeight / hh);
            } else if (w < h && w > ww) {// 如果高度高的话根据宽度固定大小缩放
                be = (int) (newOpts.outWidth / ww);
            }
            if (be <= 0)
                be = 1;
            be = be + be / 2;
            newOpts.inSampleSize = be;// 设置缩放比例
            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
            // try {
            // bitmap = BitmapFactory.decodeFileDescriptor(new
            // FileInputStream(new
            // File(srcPath)).getFD(), null, newOpts);
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
            return bitmap;
            // return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String compByPath(String srcPath, String targetPath, String targetName) {
        try {
            float newSize = 720f;
            float be = 1f;

            int degree = ImageUtils.readPictureDegree(srcPath);

            Bitmap bitmap = BitmapFactory.decodeFile(srcPath);


            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();

            if (bitmapWidth > bitmapHeight && bitmapHeight > newSize) {// 如果宽度大的话根据宽度固定大小缩放
                be = (newSize / (float) bitmapHeight);
            } else if (bitmapWidth < bitmapHeight && bitmapWidth > newSize) {// 如果高度高的话根据宽度固定大小缩放
                be = (newSize / (float) bitmapWidth);
            }

            Matrix matrix = new Matrix();
            matrix.postScale(be, be);
            matrix.postRotate(degree);
            // 产生缩放后的Bitmap对象

            Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
            resizeBitmap = compressImage(resizeBitmap);
            // save file
            File filePath = new File(targetPath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            File myCaptureFile = new File(targetPath, targetName);
            if (myCaptureFile.exists()) {
                myCaptureFile.delete();
            }
            FileOutputStream out = new FileOutputStream(myCaptureFile);
            if (resizeBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)) {
                out.flush();
                out.close();
            }
            if (!bitmap.isRecycled()) {
                bitmap.recycle();// 记得释放资源，否则会内存溢出
            }
            if (!resizeBitmap.isRecycled()) {
                resizeBitmap.recycle();
            }
            return targetPath + targetName;
        } catch (Exception ex) {
            ex.printStackTrace();
            return  "";
        }
    }

    public String compByPath2(String srcPath, String targetPath, String targetName) {
        try {
            Bitmap bitmap = getimage(srcPath);
            if (bitmap == null) {
                return targetPath;
            } else {
                File filePath = new File(targetPath);
                if (!filePath.exists()) {
                    filePath.mkdirs();
                }
                File destFile = new File(targetPath, targetName);
                if (destFile.exists()) {
                    destFile.delete();
                }
                OutputStream os = new FileOutputStream(destFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                return targetPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void transImage(String fromFile, String toFile, int width, int height, int quality) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(fromFile);
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            // 缩放图片的尺寸
            float scaleWidth = (float) width / bitmapWidth;
            float scaleHeight = (float) height / bitmapHeight;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 产生缩放后的Bitmap对象
            Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
            // save file
            File myCaptureFile = new File(toFile);
            FileOutputStream out = new FileOutputStream(myCaptureFile);
            if (resizeBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
                out.flush();
                out.close();
            }
            if (!bitmap.isRecycled()) {
                bitmap.recycle();// 记得释放资源，否则会内存溢出
            }
            if (!resizeBitmap.isRecycled()) {
                resizeBitmap.recycle();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String compByBitmap(Bitmap bitmap, String targetPath, String targetName) {
        try {
            if (bitmap == null) {
                return "";
            } else {
                File filePath = new File(targetPath);
                if (!filePath.exists()) {
                    filePath.mkdirs();
                }
                File destFile = new File(targetPath, targetName);
                if (destFile.exists()) {
                    destFile.delete();
                }
                OutputStream os = null;
                os = new FileOutputStream(destFile);
                Bitmap bitmap2 = comp(bitmap);
                bitmap2.compress(Bitmap.CompressFormat.JPEG, 90, os);
                if (!bitmap2.isRecycled()) // 如果没有回收
                    bitmap2.recycle();
                os.flush();
                os.close();
                return targetPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap comp(Bitmap image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            if (!image.isRecycled()) // 如果没有回收
                image.recycle();
            // if (baos.toByteArray().length / 1024 > 1024) {//
            // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            // baos.reset();// 重置baos即清空baos
            // image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//
            // 这里压缩50%，把压缩后的数据存放到baos中
            // }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
            float hh = 720f;// 这里设置高度为800f
            float ww = 720f;// 这里设置宽度为480f
            // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int be = 1;// be=1表示不缩放
            if (w > h && h > hh) {// 如果宽度大的话根据宽度固定大小缩放
                be = (int) (newOpts.outHeight / hh);
            } else if (w < h && w > ww) {// 如果高度高的话根据宽度固定大小缩放
                be = (int) (newOpts.outWidth / ww);
            }
            if (be <= 0)
                be = 1;
            newOpts.inSampleSize = be;// 设置缩放比例
            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            isBm = new ByteArrayInputStream(baos.toByteArray());
            bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);

            baos.close();

            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }
}
