package com.wei.view.customer.zxing.decoding;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

/**
 * @author Ryan Tang
 */
public final class EncodingHandler {
	public static final int BLACK = 0xff51648e;
	public static final int WHITE = 0xffffffff;

	public static final int PADDING_SIZE_MIN = 10; // 最小留白长度, 单位: px

	public static Bitmap createQRCode(String str, int widthAndHeight, int colorBlack, int colorWhite, int padding_size_min) throws Exception {
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, hints);

		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[] pixels = new int[width * height];

		boolean isFirstBlackPoint = false;
		int startX = 0;
		int startY = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					if (isFirstBlackPoint == false) {
						isFirstBlackPoint = true;
						startX = x;
						startY = y;
					}
					if (colorBlack == -1) {
						pixels[y * width + x] = BLACK;
					} else {
						pixels[y * width + x] = colorBlack;
					}
				} else {
					if (colorWhite == -1) {
						pixels[y * width + x] = WHITE;// 透明点,白点为0xffffffff
					} else {
						pixels[y * width + x] = colorWhite;
					}
				}
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

		// 剪切中间的二维码区域，减少padding区域
		if (padding_size_min == -1) {
			padding_size_min = PADDING_SIZE_MIN;
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
	}
}