/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.wei.image.imageCrop;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.wei.image.R;
import com.wei.image.imageUtils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;


/**
 * The activity can crop specific region of interest from an image.
 */
public class CropImage extends MonitoredActivity {
	private static final String TAG = "CropImage";

	// These are various options can be specified in the intent.
	private Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG; // only
																				// used
																				// with
																				// mSaveUri
	private Uri mSaveUri = null;
	private boolean mSetWallpaper = false;
	private float mAspectX, mAspectY;
	private boolean mDoFaceDetection = true;
	private boolean mCircleCrop = false;
	private final Handler mHandler = new Handler();

	// These options specifiy the output image size and whether we should
	// scale the output to fit it (or just crop it).
	private int mOutputX, mOutputY;
	private boolean mScale;
	private boolean mScaleUp = true;

	boolean mWaitingToPick; // Whether we are wait the user to pick a face.
	boolean mSaving; // Whether the "save" button is already clicked.

	private CropImageView mImageView;
	private ContentResolver mContentResolver;

	private Bitmap mBitmap;
	HighlightView mCrop;

	private IImageList mAllImages;
	private IImage mIImage;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mContentResolver = getContentResolver();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cropimage);

		mImageView = (CropImageView) findViewById(R.id.image);
		// mImageView.setImageResource(R.drawable.psb);
		// Date date = new Date();
		// SimpleDateFormat formatter = new
		// SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		// String mPicName = formatter.format(date);
		// File file = getFiles(mPicName + ".jpg");
		// mBitmap =
		// ((BitmapDrawable)getResources().getDrawable(R.drawable.psb)).getBitmap();
		// mSaveUri = Uri.fromFile(file);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();

		if (mSaveUri != null) {
			String outputFormatString = Bitmap.CompressFormat.JPEG.toString();
			if (outputFormatString != null) {
				mOutputFormat = Bitmap.CompressFormat.valueOf(outputFormatString);
			}
		}

		if (extras != null) {
			if (extras.getString("circleCrop") != null) {
				mCircleCrop = true;
				mAspectX = 1;
				mAspectY = 1;
			}
			mSaveUri = (Uri) extras.getParcelable(MediaStore.EXTRA_OUTPUT);
			if (mSaveUri != null) {
				String outputFormatString = extras.getString("outputFormat");
				if (outputFormatString != null) {
					mOutputFormat = Bitmap.CompressFormat.valueOf(outputFormatString);
				}
			} else {
				mSetWallpaper = extras.getBoolean("setWallpaper");
			}
			mBitmap = (Bitmap) extras.getParcelable("data");
			mAspectX = extras.getFloat("aspectX");
			mAspectY = extras.getFloat("aspectY");
			mOutputX = extras.getInt("outputX");
			mOutputY = extras.getInt("outputY");
			mScale = extras.getBoolean("scale", true);
			mScaleUp = extras.getBoolean("scaleUpIfNeeded", true);
			mDoFaceDetection = extras.containsKey("noFaceDetection") ? !extras.getBoolean("noFaceDetection") : true;
		}

		if (mBitmap == null) {
			Uri target = intent.getData();
			mAllImages = ImageManager.makeImageList(mContentResolver, target, ImageManager.SORT_ASCENDING);
			mIImage = mAllImages.getImageForUri(target);
			if (mIImage != null) {
				// Don't read in really large bitmaps. Use the (big) thumbnail
				// instead.
				// TODO when saving the resulting bitmap use the
				// decode/crop/encode api so we don't lose any resolution.
				String path = ImageUtils.getPath(getApplicationContext(), target);
				degree = ImageUtils.readPictureDegree(path);
				Bitmap thumbBitmap = mIImage.thumbBitmap(IImage.ROTATE_AS_NEEDED);
				// 旋转图片
				mBitmap = ImageUtils.rotaingImageView(thumbBitmap, degree);
			}
		}

		if (mBitmap == null) {
			finish();
			return;
		}

		// Make UI fullscreen.
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		View save = findViewById(R.id.save);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			save.setAlpha(0.5f);
		}
		save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onSaveClicked();
			}
		});

		startFaceDetection();
	}

	private File getFiles(String fileName) {
		File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
		if (file != null) {
			return file;
		}
		return null;
	}

	private void startFaceDetection() {
		if (isFinishing()) {
			return;
		}

		// 设置是否重置旋转角度 原来的都是true，自定义成false
		mImageView.setImageBitmapResetBase(mBitmap, 0, false);

		Util.startBackgroundJob(this, null, getResources().getString(R.string.runningFaceDetection), new Runnable() {
			public void run() {
				final CountDownLatch latch = new CountDownLatch(1);
				final Bitmap b = (mIImage != null) ? mIImage.fullSizeBitmap(IImage.UNCONSTRAINED, 1024 * 1024) : mBitmap;
				mHandler.post(new Runnable() {
					public void run() {
						if (b != mBitmap && b != null) {
							// 旋转图片
							mBitmap.recycle();
							mBitmap = ImageUtils.rotaingImageView(b, degree);
							mImageView.setImageBitmapResetBase(mBitmap, 0, false);
						}
						if (mImageView.getScale() == 1F) {
							mImageView.center(true, true);
						}
						latch.countDown();
					}
				});
				try {
					latch.await();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				mRunFaceDetection.run();
			}
		}, mHandler);
	}

	private void onSaveClicked() {
		// TODO this code needs to change to use the decode/crop/encode single
		// step api so that we don't require that the whole (possibly large)
		// bitmap doesn't have to be read into memory
		if (mCrop == null) {
			return;
		}

		if (mSaving)
			return;
		mSaving = true;

		Bitmap croppedImage;

		// mBitmap = ImageUtils.rotaingImageView(mBitmap, degree);

		// If the output is required to a specific size, create an new image
		// with the cropped image in the center and the extra space filled.
		if (mOutputX != 0 && mOutputY != 0 && !mScale) {
			// Don't scale the image but instead fill it so it's the
			// required dimension
			croppedImage = Bitmap.createBitmap(mOutputX, mOutputY, Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(croppedImage);
			Rect srcRect = mCrop.getCropRect();
			Rect dstRect = new Rect(0, 0, mOutputX, mOutputY);

			int dx = (srcRect.width() - dstRect.width()) / 2;
			int dy = (srcRect.height() - dstRect.height()) / 2;

			// If the srcRect is too big, use the center part of it.
			srcRect.inset(Math.max(0, dx), Math.max(0, dy));

			// If the dstRect is too big, use the center part of it.
			dstRect.inset(Math.max(0, -dx), Math.max(0, -dy));

			// Draw the cropped bitmap in the center
			canvas.drawBitmap(mBitmap, srcRect, dstRect, null);

			// Release bitmap memory as soon as possible
			mImageView.clear();
			mBitmap.recycle();
		} else {
			Rect r = mCrop.getCropRect();

			int width = r.width();
			int height = r.height();

			// If we are circle cropping, we want alpha channel, which is the
			// third param here.
			croppedImage = Bitmap.createBitmap(width, height, mCircleCrop ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);

			Canvas canvas = new Canvas(croppedImage);

			Rect dstRect = new Rect(0, 0, width, height);
			canvas.drawBitmap(mBitmap, r, dstRect, null);

			// Release bitmap memory as soon as possible
			mImageView.clear();
			mBitmap.recycle();

			if (mCircleCrop) {
				// OK, so what's all this about?
				// Bitmaps are inherently rectangular but we want to return
				// something that's basically a circle. So we fill in the
				// area around the circle with alpha. Note the all important
				// PortDuff.Mode.CLEAR.
				Canvas c = new Canvas(croppedImage);
				Path p = new Path();
				p.addCircle(width / 2F, height / 2F, width / 2F, Path.Direction.CW);
				c.clipPath(p, Region.Op.DIFFERENCE);
				c.drawColor(0xffffffff, PorterDuff.Mode.CLEAR);
			}

			// If the required dimension is specified, scale the image.
			if (mOutputX != 0 && mOutputY != 0 && mScale) {

				croppedImage = Util.transform(new Matrix(), croppedImage, mOutputX, mOutputY, mScaleUp, Util.RECYCLE_INPUT);
			}
		}

		mImageView.setImageBitmapResetBase(croppedImage, 0, false);
		mImageView.center(true, true);
		mImageView.mHighlightViews.clear();

		// Return the cropped image directly or save it to the specified URI.
		Bundle myExtras = getIntent().getExtras();
		if (myExtras != null && (myExtras.getParcelable("data") != null || myExtras.getBoolean("return-data"))) {
			Bundle extras = new Bundle();
			extras.putParcelable("data", croppedImage);
			setResult(RESULT_OK, (new Intent()).setAction("inline-data").putExtras(extras));
			finish();
		} else {
			final Bitmap b = croppedImage;
			final int msdId = mSetWallpaper ? R.string.wallpaper : R.string.savingImage;
			Util.startBackgroundJob(this, null, getResources().getString(msdId), new Runnable() {
				public void run() {
					saveOutput(b);
				}
			}, mHandler);
		}
	}

	private void saveOutput(Bitmap croppedImage) {
		if (mSaveUri != null) {
			OutputStream outputStream = null;
			try {
				outputStream = mContentResolver.openOutputStream(mSaveUri);
				if (outputStream != null) {
					croppedImage.compress(mOutputFormat, 75, outputStream);
				}
			} catch (IOException ex) {
				// TODO: report error to caller
				Log.e(TAG, "Cannot open file: " + mSaveUri, ex);
			} finally {
				Util.closeSilently(outputStream);
			}
			Bundle extras = new Bundle();
			setResult(RESULT_OK, new Intent(mSaveUri.toString()).putExtras(extras));
		} else if (mSetWallpaper) {
			try {
				WallpaperManager.getInstance(this).setBitmap(croppedImage);
				setResult(RESULT_OK);
			} catch (IOException e) {
				Log.e(TAG, "Failed to set wallpaper.", e);
				setResult(RESULT_CANCELED);
			}
		} else if (mIImage != null) {
			Bundle extras = new Bundle();
			extras.putString("rect", mCrop.getCropRect().toString());

			File oldPath = new File(mIImage.getDataPath());
			File directory = new File(oldPath.getParent());

			int x = 0;
			String fileName = oldPath.getName();
			fileName = fileName.substring(0, fileName.lastIndexOf("."));

			// Try file-1.jpg, file-2.jpg, ... until we find a filename which
			// does not exist yet.
			while (true) {
				x += 1;
				String candidate = directory.toString() + "/" + fileName + "-" + x + ".jpg";
				boolean exists = (new File(candidate)).exists();
				if (!exists) {
					break;
				}
			}

			try {
				int[] degree = new int[1];
				Uri newUri = ImageManager.addImage(mContentResolver, mIImage.getTitle(), mIImage.getDateTaken(), null, directory.toString(), fileName
						+ "-" + x + ".jpg", croppedImage, null, degree);

				setResult(RESULT_OK, new Intent().setAction(newUri.toString()).putExtras(extras));
			} catch (Exception ex) {
				Log.e(TAG, "store image fail, continue anyway", ex);
			}
		} else {
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			String mPicName = formatter.format(date);
			File file = getFiles(mPicName + ".jpg");
			mSaveUri = Uri.fromFile(file);
			saveOutput(croppedImage);
		}

		final Bitmap b = croppedImage;
		mHandler.post(new Runnable() {
			public void run() {
				mImageView.clear();
				b.recycle();
			}
		});

		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (mAllImages != null) {
			mAllImages.close();
		}
		super.onDestroy();
	}

	Runnable mRunFaceDetection = new Runnable() {
		@SuppressWarnings("hiding")
		float mScale = 1F;
		Matrix mImageMatrix;
		FaceDetector.Face[] mFaces = new FaceDetector.Face[3];
		int mNumFaces;

		// For each face, we create a HightlightView for it.
		private void handleFace(FaceDetector.Face f) {
			PointF midPoint = new PointF();

			int r = ((int) (f.eyesDistance() * mScale)) * 2;
			f.getMidPoint(midPoint);
			midPoint.x *= mScale;
			midPoint.y *= mScale;

			int midX = (int) midPoint.x;
			int midY = (int) midPoint.y;

			HighlightView hv = new HighlightView(mImageView);

			int width = mBitmap.getWidth();
			int height = mBitmap.getHeight();

			Rect imageRect = new Rect(0, 0, width, height);

			RectF faceRect = new RectF(midX, midY, midX, midY);
			faceRect.inset(-r, -r);
			if (faceRect.left < 0) {
				faceRect.inset(-faceRect.left, -faceRect.left);
			}

			if (faceRect.top < 0) {
				faceRect.inset(-faceRect.top, -faceRect.top);
			}

			if (faceRect.right > imageRect.right) {
				faceRect.inset(faceRect.right - imageRect.right, faceRect.right - imageRect.right);
			}

			if (faceRect.bottom > imageRect.bottom) {
				faceRect.inset(faceRect.bottom - imageRect.bottom, faceRect.bottom - imageRect.bottom);
			}

			hv.setup(mImageMatrix, imageRect, faceRect, mCircleCrop, mAspectX != 0 && mAspectY != 0);

			mImageView.add(hv);
		}

		// Create a default HightlightView if we found no face in the picture.
		private void makeDefault() {
			HighlightView hv = new HighlightView(mImageView);

			int width = mBitmap.getWidth();
			int height = mBitmap.getHeight();

			Rect imageRect = new Rect(0, 0, width, height);

			// make the default size about 4/5 of the width or height
			int cropWidth = Math.min(width, height) * 4 / 5;
			int cropHeight = cropWidth;

			if (mAspectX != 0 && mAspectY != 0) {
				if (mAspectX > mAspectY) {
					cropHeight = (int) (cropWidth * (mAspectY / mAspectX));
				} else {
					cropWidth = (int) (cropHeight * (mAspectX / mAspectY));
				}
			}

			int x = (width - cropWidth) / 2;
			int y = (height - cropHeight) / 2;

			RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
			hv.setup(mImageMatrix, imageRect, cropRect, mCircleCrop, mAspectX != 0 && mAspectY != 0);
			mImageView.add(hv);
		}

		// Scale the image down for faster face detection.
		private Bitmap prepareBitmap() {
			if (mBitmap == null) {
				return null;
			}

			// 256 pixels wide is enough.
			if (mBitmap.getWidth() > 256) {
				mScale = 256.0F / mBitmap.getWidth();
			}
			Matrix matrix = new Matrix();
			matrix.setScale(mScale, mScale);
			Bitmap faceBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
			return faceBitmap;
		}

		public void run() {
			mImageMatrix = mImageView.getImageMatrix();
			Bitmap faceBitmap = prepareBitmap();

			mScale = 1.0F / mScale;
			if (faceBitmap != null && mDoFaceDetection) {
				FaceDetector detector = new FaceDetector(faceBitmap.getWidth(), faceBitmap.getHeight(), mFaces.length);
				mNumFaces = detector.findFaces(faceBitmap, mFaces);
			}

			if (faceBitmap != null && faceBitmap != mBitmap) {
				faceBitmap.recycle();
			}

			mHandler.post(new Runnable() {
				public void run() {
					mWaitingToPick = mNumFaces > 1;
					if (mNumFaces > 0) {
						for (int i = 0; i < mNumFaces; i++) {
							handleFace(mFaces[i]);
						}
					} else {
						makeDefault();
					}
					mImageView.invalidate();
					if (mImageView.mHighlightViews.size() == 1) {
						mCrop = mImageView.mHighlightViews.get(0);
						mCrop.setFocus(true);
					}

					if (mNumFaces > 1) {
						Toast t = Toast.makeText(CropImage.this, R.string.multiface_crop_help, Toast.LENGTH_SHORT);
						t.show();
					}
				}
			});
		}
	};

	private int degree;
}
