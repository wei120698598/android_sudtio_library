/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.wei.image.zxing.camera;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * This class is used to activate the weak light on some camera phones (not
 * flash) in order to illuminate surfaces for scanning. There is no official way
 * to do this, but, classes which allow access to this function still exist on
 * some devices. This therefore proceeds through a great deal of reflection.
 * 
 * See <a href=
 * "http://almondmendoza.com/2009/01/05/changing-the-screen-brightness-programatically/"
 * > http://almondmendoza.com/2009/01/05/changing-the-screen-brightness-
 * programatically/</a> and <a href=
 * "http://code.google.com/p/droidled/source/browse/trunk/src/com/droidled/demo/DroidLED.java"
 * > http://code.google.com/p/droidled/source/browse/trunk/src/com/droidled/demo
 * /DroidLED.java</a>. Thanks to Ryan Alford for pointing out the availability
 * of this class.
 */
final class FlashlightManager {

	private static final String TAG = FlashlightManager.class.getSimpleName();

	private static final Object iHardwareService;
	private static final Method setFlashEnabledMethod;
	static {
		iHardwareService = getHardwareService();
		setFlashEnabledMethod = getSetFlashEnabledMethod(iHardwareService);
		if (iHardwareService == null) {
			Log.v(TAG, "This device does supports control of a flashlight");
		} else {
			Log.v(TAG, "This device does not support control of a flashlight");
		}
	}

	private FlashlightManager() {
	}

	// FIXME
	static void enableFlashlight() {
		setFlashlight(true);
	}

	static void disableFlashlight() {
		setFlashlight(false);
	}

	private static Object getHardwareService() {
		Class<?> serviceManagerClass = maybeForName("android.os.ServiceManager");
		if (serviceManagerClass == null) {
			return null;
		}

		Method getServiceMethod = maybeGetMethod(serviceManagerClass, "getService", String.class);
		if (getServiceMethod == null) {
			return null;
		}

		Object hardwareService = invoke(getServiceMethod, null, "hardware");
		if (hardwareService == null) {
			return null;
		}

		Class<?> iHardwareServiceStubClass = maybeForName("android.os.IHardwareService$Stub");
		if (iHardwareServiceStubClass == null) {
			return null;
		}

		Method asInterfaceMethod = maybeGetMethod(iHardwareServiceStubClass, "asInterface", IBinder.class);
		if (asInterfaceMethod == null) {
			return null;
		}

		return invoke(asInterfaceMethod, null, hardwareService);
	}

	private static Method getSetFlashEnabledMethod(Object iHardwareService) {
		if (iHardwareService == null) {
			return null;
		}
		Class<?> proxyClass = iHardwareService.getClass();
		return maybeGetMethod(proxyClass, "setFlashlightEnabled", boolean.class);
	}

	private static Class<?> maybeForName(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException cnfe) {
			// OK
			return null;
		} catch (RuntimeException re) {
			Log.w(TAG, "Unexpected error while finding class " + name, re);
			return null;
		}
	}

	private static Method maybeGetMethod(Class<?> clazz, String name, Class<?>... argClasses) {
		try {
			return clazz.getMethod(name, argClasses);
		} catch (NoSuchMethodException nsme) {
			// OK
			return null;
		} catch (RuntimeException re) {
			Log.w(TAG, "Unexpected error while finding method " + name, re);
			return null;
		}
	}

	private static Object invoke(Method method, Object instance, Object... args) {
		try {
			return method.invoke(instance, args);
		} catch (IllegalAccessException e) {
			Log.w(TAG, "Unexpected error while invoking " + method, e);
			return null;
		} catch (InvocationTargetException e) {
			Log.w(TAG, "Unexpected error while invoking " + method, e.getCause());
			return null;
		} catch (RuntimeException re) {
			Log.w(TAG, "Unexpected error while invoking " + method, re);
			return null;
		}
	}

	private static void setFlashlight(boolean active) {
		if (iHardwareService != null) {
			invoke(setFlashEnabledMethod, iHardwareService, active);
		}
	}

	public static void turnLightOn(Camera mCamera) {
		if (mCamera == null) {
			return;
		}
		Parameters parameters = mCamera.getParameters();
		if (parameters == null) {
			return;
		}
		List<String> flashModes = parameters.getSupportedFlashModes();
		// Check if camera flash exists
		if (flashModes == null) {
			// Use the screen as a flashlight (next best thing)
			return;
		}
		String flashMode = parameters.getFlashMode();
		if (!Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
			// Turn on the flash
			if (flashModes.contains(Parameters.FLASH_MODE_TORCH)) {
				parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
				mCamera.setParameters(parameters);
			} else {
			}
		}
	}

	public static void turnLightOff(Camera mCamera) {
		if (mCamera == null) {
			return;
		}
		Parameters parameters = mCamera.getParameters();
		if (parameters == null) {
			return;
		}
		List<String> flashModes = parameters.getSupportedFlashModes();
		String flashMode = parameters.getFlashMode();
		// Check if camera flash exists
		if (flashModes == null) {
			return;
		}
		if (!Parameters.FLASH_MODE_OFF.equals(flashMode)) {
			// Turn off the flash
			if (flashModes.contains(Parameters.FLASH_MODE_OFF)) {
				parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
				mCamera.setParameters(parameters);
			} else {
				Log.e(TAG, "FLASH_MODE_OFF not supported");
			}
		}
	}
}
