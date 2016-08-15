package com.wei.view.customer.zxing.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

final class CameraConfigurationManager {

	private static final String TAG = CameraConfigurationManager.class.getSimpleName();

	private static final int TEN_DESIRED_ZOOM = 26;
	private static final Pattern COMMA_PATTERN = Pattern.compile(",");

	private final Context context;
	private Point screenResolution;
	private Point cameraResolution;
	private int previewFormat;
	private String previewFormatString;

	CameraConfigurationManager(Context context) {
		this.context = context;
	}

	@SuppressWarnings("deprecation")
	void initFromCameraParameters(Camera camera) {
		Parameters parameters = camera.getParameters();
		previewFormat = parameters.getPreviewFormat();
		previewFormatString = parameters.get("preview-format");
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		screenResolution = new Point(display.getWidth(), display.getHeight());

		Point screenResolutionForCamera = new Point();
		screenResolutionForCamera.x = screenResolution.x;
		screenResolutionForCamera.y = screenResolution.y;

		if (screenResolution.x < screenResolution.y) {
			screenResolutionForCamera.x = screenResolution.y;
			screenResolutionForCamera.y = screenResolution.x;
		}
		cameraResolution = getCameraResolution(parameters, screenResolutionForCamera);
	}

	void setDesiredCameraParameters(Camera camera) {
		Parameters parameters = camera.getParameters();
		List<Size> sizes = parameters.getSupportedPreviewSizes();
		Size optimalSize = getOptimalPreviewSize(sizes, cameraResolution.x, cameraResolution.y);
		parameters.setPreviewSize(optimalSize.width, optimalSize.height);
		// parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
		setFlash(parameters);
		setZoom(parameters);

		camera.setDisplayOrientation(90);
		camera.setParameters(parameters);
	}

	@SuppressLint("NewApi")
	void setDesiredCameraParameters(Camera camera, boolean safeMode) {
		camera.setDisplayOrientation(90);
		Parameters parameters = camera.getParameters();

		if (parameters == null) {
			Log.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.");
			return;
		}

		Log.i(TAG, "Initial camera parameters: " + parameters.flatten());

		if (safeMode) {
			Log.w(TAG, "In camera config safe mode -- most settings will not be honored");
		}

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		initializeTorch(parameters, prefs, safeMode);

		String focusMode = null;
		if (true) {
			if (safeMode || false) {
				focusMode = findSettableValue(parameters.getSupportedFocusModes(), Parameters.FOCUS_MODE_AUTO);
			} else {
				focusMode = findSettableValue(parameters.getSupportedFocusModes(), "continuous-picture", // Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
																											// in
																											// 4.0+
						"continuous-video", // Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
											// in 4.0+
						Parameters.FOCUS_MODE_AUTO);
			}
		}
		// Maybe selected auto-focus but not available, so fall through here:
		if (!safeMode && focusMode == null) {
			focusMode = findSettableValue(parameters.getSupportedFocusModes(), Parameters.FOCUS_MODE_CONTINUOUS_PICTURE, "edof"); // Camera.Parameters.FOCUS_MODE_EDOF
			// in
			// 2.2+
		}
		if (focusMode != null) {
			parameters.setFocusMode(focusMode);
		}
		parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
		camera.setParameters(parameters);
	}

	private void initializeTorch(Parameters parameters, SharedPreferences prefs, boolean safeMode) {
		// boolean currentSetting =
		// prefs.getBoolean(PreferencesActivity.KEY_FRONT_LIGHT, false);
		boolean currentSetting = false;
		doSetTorch(parameters, currentSetting, safeMode);
	}

	void setTorch(Camera camera, boolean newSetting) {
		Parameters parameters = camera.getParameters();
		doSetTorch(parameters, newSetting, false);
		camera.setParameters(parameters);
	}

	boolean getTorchState(Camera camera) {
		if (camera != null) {
			Parameters parameters = camera.getParameters();
			if (parameters != null) {
				String flashMode = camera.getParameters().getFlashMode();
				return flashMode != null
						&& (Parameters.FLASH_MODE_ON.equals(flashMode) || Parameters.FLASH_MODE_TORCH.equals(flashMode));
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	private void doSetTorch(Parameters parameters, boolean newSetting, boolean safeMode) {
		String flashMode;
		if (newSetting) {
			flashMode = findSettableValue(parameters.getSupportedFlashModes(), Parameters.FLASH_MODE_TORCH, Parameters.FLASH_MODE_ON);
		} else {
			flashMode = findSettableValue(parameters.getSupportedFlashModes(), Parameters.FLASH_MODE_OFF);
		}
		if (flashMode != null) {
			parameters.setFlashMode(flashMode);
		}

		/*
		 * SharedPreferences prefs =
		 * PreferenceManager.getDefaultSharedPreferences(context); if
		 * (!prefs.getBoolean(PreferencesActivity.KEY_DISABLE_EXPOSURE, false))
		 * { if (!safeMode) { ExposureInterface exposure = new
		 * ExposureManager().build(); exposure.setExposure(parameters,
		 * newSetting); } }
		 */
	}

	private Point findBestPreviewSizeValue(Parameters parameters, Point screenResolution) {

		List<Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
		if (rawSupportedSizes == null) {
			Log.w(TAG, "Device returned no supported preview sizes; using default");
			Size defaultSize = parameters.getPreviewSize();
			return new Point(defaultSize.width, defaultSize.height);
		}

		// Sort by size, descending
		List<Size> supportedPreviewSizes = new ArrayList<>(rawSupportedSizes);
		Collections.sort(supportedPreviewSizes, new Comparator<Size>() {
			@Override
			public int compare(Size a, Size b) {
				int aPixels = a.height * a.width;
				int bPixels = b.height * b.width;
				if (bPixels < aPixels) {
					return -1;
				}
				if (bPixels > aPixels) {
					return 1;
				}
				return 0;
			}
		});

		double screenAspectRatio = (double) screenResolution.x / (double) screenResolution.y;

		// Remove sizes that are unsuitable
		Iterator<Size> it = supportedPreviewSizes.iterator();
		while (it.hasNext()) {
			Size supportedPreviewSize = it.next();
			int realWidth = supportedPreviewSize.width;
			int realHeight = supportedPreviewSize.height;
			if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {
				it.remove();
				continue;
			}

			boolean isCandidatePortrait = realWidth < realHeight;
			int maybeFlippedWidth = isCandidatePortrait ? realHeight : realWidth;
			int maybeFlippedHeight = isCandidatePortrait ? realWidth : realHeight;
			double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
			double distortion = Math.abs(aspectRatio - screenAspectRatio);
			if (distortion > MAX_ASPECT_DISTORTION) {
				it.remove();
				continue;
			}

			if (maybeFlippedWidth == screenResolution.x && maybeFlippedHeight == screenResolution.y) {
				Point exactPoint = new Point(realWidth, realHeight);
				Log.i(TAG, "Found preview size exactly matching screen size: " + exactPoint);
				return exactPoint;
			}
		}

		// If no exact match, use largest preview size. This was not a great
		// idea on older devices
		// because
		// of the additional computation needed. We're likely to get here on
		// newer Android 4+ devices,
		// where
		// the CPU is much more powerful.
		if (!supportedPreviewSizes.isEmpty()) {
			Size largestPreview = supportedPreviewSizes.get(0);
			Point largestSize = new Point(largestPreview.width, largestPreview.height);
			Log.i(TAG, "Using largest suitable preview size: " + largestSize);
			return largestSize;
		}

		// If there is nothing at all suitable, return current preview size
		Size defaultPreview = parameters.getPreviewSize();
		Point defaultSize = new Point(defaultPreview.width, defaultPreview.height);
		Log.i(TAG, "No suitable preview sizes, using default: " + defaultSize);
		return defaultSize;
	}

	private static final int MIN_PREVIEW_PIXELS = 480 * 320;

	// private static final float MAX_EXPOSURE_COMPENSATION = 1.5f;
	// private static final float MIN_EXPOSURE_COMPENSATION = 0.0f;
	private static final double MAX_ASPECT_DISTORTION = 0.15;

	private static String findSettableValue(Collection<String> supportedValues, String... desiredValues) {
		Log.i(TAG, "Supported values: " + supportedValues);
		String result = null;
		if (supportedValues != null) {
			for (String desiredValue : desiredValues) {
				if (supportedValues.contains(desiredValue)) {
					result = desiredValue;
					break;
				}
			}
		}
		Log.i(TAG, "Settable value: " + result);
		return result;
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.05;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;
		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;
		int targetHeight = h;
		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}
		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	Point getCameraResolution() {
		return cameraResolution;
	}

	Point getScreenResolution() {
		return screenResolution;
	}

	int getPreviewFormat() {
		return previewFormat;
	}

	String getPreviewFormatString() {
		return previewFormatString;
	}

	private static Point getCameraResolution(Parameters parameters, Point screenResolution) {

		String previewSizeValueString = parameters.get("preview-size-values");
		if (previewSizeValueString == null) {
			previewSizeValueString = parameters.get("preview-size-value");
		}

		Point cameraResolution = null;

		if (previewSizeValueString != null) {
			cameraResolution = findBestPreviewSizeValue(previewSizeValueString, screenResolution);
		}

		if (cameraResolution == null) {
			cameraResolution = new Point((screenResolution.x >> 3) << 3, (screenResolution.y >> 3) << 3);
		}

		return cameraResolution;
	}

	private static Point findBestPreviewSizeValue(CharSequence previewSizeValueString, Point screenResolution) {
		int bestX = 0;
		int bestY = 0;
		int diff = Integer.MAX_VALUE;
		for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {

			previewSize = previewSize.trim();
			int dimPosition = previewSize.indexOf('x');
			if (dimPosition < 0) {
				continue;
			}

			int newX;
			int newY;
			try {
				newX = Integer.parseInt(previewSize.substring(0, dimPosition));
				newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
			} catch (NumberFormatException nfe) {
				continue;
			}

			int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
			if (newDiff == 0) {
				bestX = newX;
				bestY = newY;
				break;
			} else if (newDiff < diff) {
				bestX = newX;
				bestY = newY;
				diff = newDiff;
			}

		}

		if (bestX > 0 && bestY > 0) {
			return new Point(bestX, bestY);
		}
		return null;
	}

	private static int findBestMotZoomValue(CharSequence stringValues, int tenDesiredZoom) {
		int tenBestValue = 0;
		for (String stringValue : COMMA_PATTERN.split(stringValues)) {
			stringValue = stringValue.trim();
			double value;
			try {
				value = Double.parseDouble(stringValue);
			} catch (NumberFormatException nfe) {
				return tenDesiredZoom;
			}
			int tenValue = (int) (10.0 * value);
			if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom - tenBestValue)) {
				tenBestValue = tenValue;
			}
		}
		return tenBestValue;
	}

	private void setFlash(Parameters parameters) {
		if (Build.MODEL.contains("Behold II") && CameraManager.SDK_INT == 3) { // 3
			parameters.set("flash-value", 1);
		} else {
			parameters.set("flash-value", 2);
		}
		parameters.set("flash-mode", "off");
	}

	private void setZoom(Parameters parameters) {

		String zoomSupportedString = parameters.get("zoom-supported");
		if (zoomSupportedString != null && !Boolean.parseBoolean(zoomSupportedString)) {
			return;
		}

		int tenDesiredZoom = TEN_DESIRED_ZOOM;

		String maxZoomString = parameters.get("max-zoom");
		if (maxZoomString != null) {
			try {
				int tenMaxZoom = (int) (10.0 * Double.parseDouble(maxZoomString));
				if (tenDesiredZoom > tenMaxZoom) {
					tenDesiredZoom = tenMaxZoom;
				}
			} catch (NumberFormatException nfe) {
				Log.w(TAG, "Bad max-zoom: " + maxZoomString);
			}
		}

		String takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max");
		if (takingPictureZoomMaxString != null) {
			try {
				int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
				if (tenDesiredZoom > tenMaxZoom) {
					tenDesiredZoom = tenMaxZoom;
				}
			} catch (NumberFormatException nfe) {
				Log.w(TAG, "Bad taking-picture-zoom-max: " + takingPictureZoomMaxString);
			}
		}

		String motZoomValuesString = parameters.get("mot-zoom-values");
		if (motZoomValuesString != null) {
			tenDesiredZoom = findBestMotZoomValue(motZoomValuesString, tenDesiredZoom);
		}

		String motZoomStepString = parameters.get("mot-zoom-step");
		if (motZoomStepString != null) {
			try {
				double motZoomStep = Double.parseDouble(motZoomStepString.trim());
				int tenZoomStep = (int) (10.0 * motZoomStep);
				if (tenZoomStep > 1) {
					tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
				}
			} catch (NumberFormatException nfe) {
				// continue
			}
		}

		// Set zoom. This helps encourage the user to pull back.
		// Some devices like the Behold have a zoom parameter
		if (maxZoomString != null || motZoomValuesString != null) {
			parameters.set("zoom", String.valueOf(tenDesiredZoom / 10.0));
		}

		// Most devices, like the Hero, appear to expose this zoom parameter.
		// It takes on values like "27" which appears to mean 2.7x zoom
		if (takingPictureZoomMaxString != null) {
			parameters.set("taking-picture-zoom", tenDesiredZoom);
		}
	}

}
