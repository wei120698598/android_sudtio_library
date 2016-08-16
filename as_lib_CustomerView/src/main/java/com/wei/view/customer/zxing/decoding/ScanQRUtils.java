/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.wei.view.customer.zxing.decoding;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.wei.view.customer.zxing.ui.MipcaCaptureActivity;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * 二维码识别生成处理相关工具类，包含二维码扫描、结果处理、二维码生成等
 */
public class ScanQrUtils {
    private static final int IMAGE_HALFWIDTH = 45;// logo宽度值

    public static class ScanQrResult {
        private String resultText;
        private Bitmap resultBitmap;

        public String getResultText() {
            return resultText;
        }

        public void setResultText(String resultText) {
            this.resultText = resultText;
        }

        public Bitmap getResultBitmap() {
            return resultBitmap;
        }

        public void setResultBitmap(Bitmap resultBitmap) {
            this.resultBitmap = resultBitmap;
        }
    }

    /**
     * 接收并处理返回数据
     *
     * @param data
     * @param requestCode
     * @param resultCode
     * @param isGetBitmap true为获取扫描的二维码截图，要和启动时传入的isGetBitmap对应，否则为null
     * @return
     */
    public static ScanQrResult activityResult(Intent data, int requestCode, int resultCode, final boolean isGetBitmap) {
        ScanQrResult scanQrResult = new ScanQrResult();
        try {
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                return null;
            }
            switch (requestCode) {
                case MipcaCaptureActivity.REQUEST_CODE_LOCAL:
                    if (resultCode == Activity.RESULT_OK) {
                        // 显示扫描到的内容
                        scanQrResult.setResultText(bundle.getString(MipcaCaptureActivity.REQUEST_RESULT_TEXT));
                        if (isGetBitmap) {
                            scanQrResult.setResultBitmap((Bitmap) bundle.getParcelable(MipcaCaptureActivity.REQUEST_RESULT_BITMAP));
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scanQrResult;
    }


    /**
     * 生成普通二维码
     *
     * @param content          文字内容
     * @param qr_width         二维码宽度，单位像素
     * @param colorBlack       黑色块颜色，-1表示默认黑色
     * @param colorWhite       白色块颜色，-1表示默认白色
     * @param padding_size_min 白色空白边框，-1表示默认10px
     * @return 返回的是bitmap，建议压缩保存本地后，再进行使用，否则内存消耗较大
     */
    public static Bitmap createQRImage(String content, int qr_width, int colorBlack, int colorWhite, int padding_size_min) {
        try {
            return EncodingHandler.createQRCode(content, qr_width, colorBlack, colorWhite, padding_size_min);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 生成带logo的二维码
     *
     * @param content          文字内容
     * @param qr_width         二维码宽度，单位像素
     * @param colorBlack       黑色块颜色，-1表示默认黑色
     * @param colorWhite       白色块颜色，-1表示默认白色
     * @param padding_size_min 白色空白边框，-1表示默认10px
     * @return 返回的是bitmap，建议压缩保存本地后，再进行使用，否则内存消耗较大
     */
    public static Bitmap createQRImage(String content, int qr_width, int colorBlack, int colorWhite, int padding_size_min, Bitmap logo) {
        try {
            boolean isFirstBlackPoint = false;
            int startX = 0;
            int startY = 0;
            Matrix m = new Matrix();
            float sx = (float) 2 * IMAGE_HALFWIDTH / logo.getWidth();
            float sy = (float) 2 * IMAGE_HALFWIDTH / logo.getHeight();
            m.setScale(sx, sy);// 设置缩放信息
            // 将logo图片按martix设置的信息缩放
            logo = Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), m, false);
            MultiFormatWriter writer = new MultiFormatWriter();
            Hashtable hst = new Hashtable();
            hst.put(EncodeHintType.CHARACTER_SET, "UTF-8");// 设置字符编码
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, qr_width, qr_width, hst);// 生成二维码矩阵信息
            int width = matrix.getWidth();// 矩阵高度
            int height = matrix.getHeight();// 矩阵宽度
            int halfW = width / 2;
            int halfH = height / 2;
            int[] pixels = new int[width * height];// 定义数组长度为矩阵高度*矩阵宽度，用于记录矩阵中像素信息
            for (int y = 0; y < height; y++) {// 从行开始迭代矩阵
                for (int x = 0; x < width; x++) {// 迭代列
                    if (x > halfW - IMAGE_HALFWIDTH && x < halfW + IMAGE_HALFWIDTH && y > halfH - IMAGE_HALFWIDTH && y < halfH + IMAGE_HALFWIDTH) {
                        // 该位置用于存放图片信息
                        // 记录图片每个像素信息
                        pixels[y * width + x] = logo.getPixel(x - halfW + IMAGE_HALFWIDTH, y - halfH + IMAGE_HALFWIDTH);
                    } else {
                        if (matrix.get(x, y)) {// 如果有黑块点，记录信息
                            if (isFirstBlackPoint == false) {
                                isFirstBlackPoint = true;
                                startX = x;
                                startY = y;
                            }
                            if (colorBlack == -1) {
                                pixels[y * width + x] = EncodingHandler.BLACK;// 记录黑块信息
                            } else {
                                pixels[y * width + x] = colorBlack;
                            }
                        } else {
                            if (colorWhite == -1) {
                                pixels[y * width + x] = EncodingHandler.WHITE;// 透明点,白点为0xffffffff
                            } else {
                                pixels[y * width + x] = colorWhite;
                            }
                        }
                    }
                }
            }
            if (logo != null && !logo.isRecycled()) {
                logo.recycle();
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            // 通过像素数组生成bitmap
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            // 剪切中间的二维码区域，减少padding区域
            if (padding_size_min == -1) {
                padding_size_min = EncodingHandler.PADDING_SIZE_MIN;
            }
            if (startX <= padding_size_min)
                return bitmap;

            int x1 = startX - padding_size_min;
            int y1 = startY - padding_size_min;
            if (x1 < 0 || y1 < 0)
                return bitmap;

            int w1 = width - x1 * 2;
            int h1 = height - y1 * 2;

            Bitmap bitmapQR = Bitmap.createBitmap(bitmap, x1, y1, w1, h1);
            return bitmapQR;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成二维码Bitmap
     *
     * @param content 文本内容
     * @param logoBm  二维码中心的Logo图标（可以为null）
     * @return 合成后的bitmap，建议压缩保存本地后，再进行使用，否则内存消耗较大
     */
    public static Bitmap createQRImage(Context context, String content, Bitmap logoBm) {

        try {

            if (content == null || "".equals(content)) {
                return null;
            }

            int widthPix = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
            widthPix = widthPix / 5 * 3;
            int heightPix = widthPix;

            // 配置参数
            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            // 设置空白边距的宽度
            hints.put(EncodeHintType.MARGIN, 3); // default is 4

            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = 0xff000000;
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;
                    }
                }
            }

            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);

            if (logoBm != null) {
                bitmap = addLogo(bitmap, logoBm);
            }

            return bitmap;
            // 必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
            // return bitmap != null &&
            // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new
            // FileOutputStream(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

        // 获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        // logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

}
