/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.zxing.client.android.PreferencesActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * A class which deals with reading, parsing, and setting the camera parameters which are used to
 * configure the camera hardware.
 */
final class CameraConfigurationManager {

	private static final String TAG = "CameraConfiguration";

	private final Context context;

	private Point screenResolution;

	private Point cameraResolution;

	CameraConfigurationManager(Context context) {
		this.context = context;
	}

	/**
	 * Reads, one time, values from the camera that are needed by the app.
	 */
	void initFromCameraParameters(Camera camera) {
		Camera.Parameters parameters = camera.getParameters();
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();

		Point theScreenResolution = new Point();
		display.getSize(theScreenResolution);
		screenResolution = theScreenResolution;
		Log.i(TAG, "Screen resolution: " + screenResolution);
		cameraResolution = findBestPreviewSizeValue(parameters, screenResolution);
		Log.i(TAG, "Camera resolution: " + cameraResolution);
	}

	void setDesiredCameraParameters(Camera camera, boolean safeMode) {
		Camera.Parameters parameters = camera.getParameters();

		if(parameters == null) {
			Log.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.");
			return;
		}

		Log.i(TAG, "Initial camera parameters: " + parameters.flatten());

		if(safeMode) {
			Log.w(TAG, "In camera config safe mode -- most settings will not be honored");
		}

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		initializeTorch(parameters, prefs, safeMode);
		CameraConfigurationUtils.setFocus(parameters, prefs.getBoolean(PreferencesActivity.KEY_AUTO_FOCUS, true),
		                                  prefs.getBoolean(PreferencesActivity.KEY_DISABLE_CONTINUOUS_FOCUS, true), safeMode);

		if(!safeMode) {
			if(prefs.getBoolean(PreferencesActivity.KEY_INVERT_SCAN, false)) {
				CameraConfigurationUtils.setInvertColor(parameters);
			}

			if(!prefs.getBoolean(PreferencesActivity.KEY_DISABLE_BARCODE_SCENE_MODE, true)) {
				CameraConfigurationUtils.setBarcodeSceneMode(parameters);
			}

			if(!prefs.getBoolean(PreferencesActivity.KEY_DISABLE_METERING, true)) {
				CameraConfigurationUtils.setVideoStabilization(parameters);
				CameraConfigurationUtils.setFocusArea(parameters);
				CameraConfigurationUtils.setMetering(parameters);
			}

		}

		parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
		CameraConfigurationUtils.setBestPreviewFPS(parameters);
		parameters.set("iso", "auto");
		parameters.setExposureCompensation(0);

		Log.i(TAG, "Final camera parameters: " + parameters.flatten());

		camera.setParameters(parameters);

		Camera.Parameters afterParameters = camera.getParameters();
		Camera.Size afterSize = afterParameters.getPreviewSize();
		if(afterSize != null && (cameraResolution.x != afterSize.width || cameraResolution.y != afterSize.height)) {
			Log.w(TAG, "Camera said it supported preview size " + cameraResolution.x + 'x' + cameraResolution.y +
					", but after setting it, preview size is " + afterSize.width + 'x' + afterSize.height);
			cameraResolution.x = afterSize.width;
			cameraResolution.y = afterSize.height;
		}
	}

	Point getCameraResolution() {
		return cameraResolution;
	}

	Point getScreenResolution() {
		return screenResolution;
	}

	boolean getTorchState(Camera camera) {
		if(camera != null) {
			Camera.Parameters parameters = camera.getParameters();
			if(parameters != null) {
				String flashMode = parameters.getFlashMode();
				return flashMode != null && (Camera.Parameters.FLASH_MODE_ON.equals(flashMode) || Camera.Parameters.FLASH_MODE_TORCH
						.equals(flashMode));
			}
		}
		return false;
	}

	void setTorch(Camera camera, boolean newSetting) {
		Camera.Parameters parameters = camera.getParameters();
		doSetTorch(parameters, newSetting, false);
		camera.setParameters(parameters);
	}

	private void initializeTorch(Camera.Parameters parameters, SharedPreferences prefs, boolean safeMode) {
		boolean currentSetting = FrontLightMode.readPref(prefs) == FrontLightMode.ON;
		doSetTorch(parameters, currentSetting, safeMode);
	}

	private void doSetTorch(Camera.Parameters parameters, boolean newSetting, boolean safeMode) {
		CameraConfigurationUtils.setTorch(parameters, newSetting);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if(!safeMode && !prefs.getBoolean(PreferencesActivity.KEY_DISABLE_EXPOSURE, true)) {
			CameraConfigurationUtils.setBestExposure(parameters, newSetting);
		}
	}

	/**
	 * Moved from {@link com.google.zxing.client.android.camera.CameraConfigurationUtils} to
	 * handle portrait mode appropriately.
	 *
	 * @param parameters camera parameters
	 * @param screenResolution screen resolution
	 * @return best preview size
	 */
	private Point findBestPreviewSizeValue(Camera.Parameters parameters, Point screenResolution) {
		final int MIN_PREVIEW_PIXELS = 480 * 320; // normal screen
		final double MAX_ASPECT_DISTORTION = 0.15;

		List<Camera.Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
		if (rawSupportedSizes == null) {
			Log.w(TAG, "Device returned no supported preview sizes; using default");
			Camera.Size defaultSize = parameters.getPreviewSize();
			if (defaultSize == null) {
				throw new IllegalStateException("Parameters contained no preview size!");
			}
			return new Point(defaultSize.width, defaultSize.height);
		}

		// Sort by size, descending
		List<Camera.Size> supportedPreviewSizes = new ArrayList<>(rawSupportedSizes);
		Collections.sort(supportedPreviewSizes, new Comparator<Camera.Size>() {
			@Override
			public int compare(final Camera.Size lhs, final Camera.Size rhs) {
				return lhs.width > rhs.width ? -1 : 1;
			}
		});

		if (Log.isLoggable(TAG, Log.INFO)) {
			StringBuilder previewSizesString = new StringBuilder();
			for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
				previewSizesString.append(supportedPreviewSize.width).append('x')
						.append(supportedPreviewSize.height).append(' ');
			}
			Log.i(TAG, "Supported preview sizes: " + previewSizesString);
		}

		double screenAspectRatio = aspectRatio(screenResolution);

		// Remove sizes that are unsuitable
		Iterator<Camera.Size> it = supportedPreviewSizes.iterator();
		while (it.hasNext()) {
			Camera.Size supportedPreviewSize = it.next();
			int realWidth = supportedPreviewSize.width;
			int realHeight = supportedPreviewSize.height;
			if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {
				it.remove();
				continue;
			}

			boolean isCandidatePortrait = realWidth < realHeight;
			int maybeFlippedWidth = isCandidatePortrait ? realHeight : realWidth;
			int maybeFlippedHeight = isCandidatePortrait ? realWidth : realHeight;
			double aspectRatio = aspectRatio(maybeFlippedWidth, maybeFlippedHeight);
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

		// If no exact match, use largest preview size. This was not a great idea on older devices because
		// of the additional computation needed. We're likely to get here on newer Android 4+ devices, where
		// the CPU is much more powerful.
		if (!supportedPreviewSizes.isEmpty()) {
			Camera.Size largestPreview = supportedPreviewSizes.get(0);
			Point largestSize = new Point(largestPreview.width, largestPreview.height);
			Log.i(TAG, "Using largest suitable preview size: " + largestSize);
			return largestSize;
		}

		// If there is nothing at all suitable, return current preview size
		Camera.Size defaultPreview = parameters.getPreviewSize();
		if (defaultPreview == null) {
			throw new IllegalStateException("Parameters contained no preview size!");
		}
		Point defaultSize = new Point(defaultPreview.width, defaultPreview.height);
		Log.i(TAG, "No suitable preview sizes, using default: " + defaultSize);
		return defaultSize;
	}

	private double aspectRatio(final Point previewSize) {
		return aspectRatio(previewSize.x, previewSize.y);
	}

	private double aspectRatio(final int x, final int y) {
		boolean isCandidatePortrait = x < y;
		int maybeFlippedWidth = isCandidatePortrait ? y : x;
		int maybeFlippedHeight = isCandidatePortrait ? x : y;
		return (double) maybeFlippedWidth / (double) maybeFlippedHeight;
	}

}
