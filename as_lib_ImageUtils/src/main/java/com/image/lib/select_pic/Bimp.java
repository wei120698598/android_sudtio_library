package com.image.lib.select_pic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.image.lib.select_pic.ui.ShowAllPhoto;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Bimp {
	public static int max = 0;

	public static Bitmap bitmap;
	public static Class clazz;

	public static ArrayList<ImageItem> tempSelectBitmap = new ArrayList<ImageItem>(); // 选择的图片的临时列表

	public static Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 1000) && (options.outHeight >> i <= 1000)) {
				in = new BufferedInputStream(new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				in.close();
				break;
			}
			i += 1;
		}
		return bitmap;
	}

	public static void clearCache() {
		Bimp.tempSelectBitmap.clear();
		ShowAllPhoto.tempList.clear();
		Bimp.max = 0;
		clazz = null;
	}
}
