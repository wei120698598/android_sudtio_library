package com.wei.view.customer.zxing.decoding;

import android.graphics.Bitmap;

import com.google.zxing.LuminanceSource;

/**
 * @describe
 * 
 * @version 1.0
 * @author leon
 */
public class BitmapLuminanceSource extends LuminanceSource {

	private byte bitmapPixels[];

	protected BitmapLuminanceSource(Bitmap bitmap) {
		super(bitmap.getWidth(), bitmap.getHeight());
		int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
		this.bitmapPixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
		bitmap.getPixels(data, 0, getWidth(), 0, 0, getWidth(), getHeight());
		for (int i = 0; i < data.length; i++) {
			this.bitmapPixels[i] = (byte) data[i];
		}
	}

	@Override
	public byte[] getMatrix() {
		return bitmapPixels;
	}

	@Override
	public byte[] getRow(int y, byte[] row) {
		System.arraycopy(bitmapPixels, y * getWidth(), row, 0, getWidth());
		return row;
	}
}
