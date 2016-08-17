/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.wei.image.zxing.camera;

import android.hardware.Camera;

/**
 * Provides an abstracted means to open a {@link Camera}. The API changes over Android API versions and
 * this allows the app to use newer API methods while retaining backwards-compatible behavior.
 */
public interface OpenCameraInterface {

  Camera open();

}
