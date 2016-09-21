package com.wei.utils.ui;

import android.graphics.Bitmap;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScreenShot {
	/**
	 * 
	 * 根据view来生成bitmap图片，可用于截图功能
	 */

	public static Bitmap getViewBitmap(View v) {
		v.clearFocus(); //
		v.setPressed(false); //
		// 能画缓存就返回false
		boolean willNotCache = v.willNotCacheDrawing();
		v.setWillNotCacheDrawing(false);
		int color = v.getDrawingCacheBackgroundColor();
		v.setDrawingCacheBackgroundColor(0);
		if (color != 0) {
			v.destroyDrawingCache();
		}
		v.buildDrawingCache();
		Bitmap cacheBitmap = v.getDrawingCache();
		if (cacheBitmap == null) {
			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
		// Restore the view
		v.destroyDrawingCache();
		v.setWillNotCacheDrawing(willNotCache);
		v.setDrawingCacheBackgroundColor(color);
		return bitmap;
	}

	/**
	 * 
	 * 保存Bitmap图片为本地文件
	 */

	public static void saveFile(Bitmap bitmap, String filename) {
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(filename);
			if (fileOutputStream != null) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
