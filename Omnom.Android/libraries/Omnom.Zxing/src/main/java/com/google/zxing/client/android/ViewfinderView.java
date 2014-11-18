/*
 * Copyright (C) 2008 ZXing authors
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

package com.google.zxing.client.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;
import com.omnom.android.zxing.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

	public static final int CADRE_SIZE         = 100;
	public static final int CADRE_SIZE_VISIBLE = 16;

	private static final int[] SCANNER_ALPHA         = {0, 64, 128, 192, 255, 192, 128, 64};
	private static final long  ANIMATION_DELAY       = 80L;
	private static final int   CURRENT_POINT_OPACITY = 0xA0;
	private static final int   MAX_RESULT_POINTS     = 20;
	private static final int   POINT_SIZE            = 6;
	private final Paint cadrePaint;
	private       int   cadreColor;

	private final Paint             paint;
	private final int               maskColor;
	private final int               resultColor;
	private final int               laserColor;
	private final int               resultPointColor;
	private       CameraManager     cameraManager;
	private       Bitmap            resultBitmap;
	private       int               scannerAlpha;
	private       List<ResultPoint> possibleResultPoints;
	private       List<ResultPoint> lastPossibleResultPoints;

	// This constructor is used when the class is built from an XML resource.
	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Initialize these once for performance rather than calling them every time in onDraw().
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);
		laserColor = resources.getColor(android.R.color.transparent/*R.color.viewfinder_laser*/);
		resultPointColor = resources.getColor(android.R.color.transparent/*R.color.possible_result_points*/);

		cadreColor = context.getResources().getColor(R.color.cadre_border);
		cadrePaint = new Paint();
		cadrePaint.setColor(cadreColor);

		scannerAlpha = 0;
		possibleResultPoints = new ArrayList<ResultPoint>(5);
		lastPossibleResultPoints = null;
	}

	public void setCameraManager(CameraManager cameraManager) {
		this.cameraManager = cameraManager;
	}

	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		if(cameraManager == null) {
			return; // not ready yet, early draw before done configuring
		}
		Rect frame = cameraManager.getFramingRect();
		Rect previewFrame = cameraManager.getFramingRectInPreview();
		if(frame == null || previewFrame == null) {
			return;
		}

		canvas.clipRect(frame.left + CADRE_SIZE_VISIBLE, frame.top + CADRE_SIZE_VISIBLE, frame.left + CADRE_SIZE, frame.top + CADRE_SIZE,
		                Region.Op.DIFFERENCE);
		canvas.clipRect(frame.right - CADRE_SIZE, frame.top + CADRE_SIZE_VISIBLE, frame.right - CADRE_SIZE_VISIBLE, frame.top + CADRE_SIZE,
		                Region.Op.DIFFERENCE);
		canvas.clipRect(frame.right - CADRE_SIZE, frame.bottom - CADRE_SIZE, frame.right - CADRE_SIZE_VISIBLE,
		                frame.bottom - CADRE_SIZE_VISIBLE, Region.Op.DIFFERENCE);
		canvas.clipRect(frame.left + CADRE_SIZE_VISIBLE, frame.bottom - CADRE_SIZE, frame.left + CADRE_SIZE,
		                frame.bottom - CADRE_SIZE_VISIBLE, Region.Op.DIFFERENCE);

		canvas.drawRect(frame.left, frame.top, frame.left + CADRE_SIZE, frame.top + CADRE_SIZE, cadrePaint);
		canvas.drawRect(frame.right - CADRE_SIZE, frame.top, frame.right, frame.top + CADRE_SIZE, cadrePaint);
		canvas.drawRect(frame.right - CADRE_SIZE, frame.bottom - CADRE_SIZE, frame.right, frame.bottom, cadrePaint);
		canvas.drawRect(frame.left, frame.bottom - CADRE_SIZE, frame.left + CADRE_SIZE, frame.bottom, cadrePaint);


		canvas.restore();
	}

	public void drawViewfinder() {
		Bitmap resultBitmap = this.resultBitmap;
		this.resultBitmap = null;
		if(resultBitmap != null) {
			resultBitmap.recycle();
		}
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live scanning display.
	 *
	 * @param barcode An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		List<ResultPoint> points = possibleResultPoints;
		synchronized(points) {
			points.add(point);
			int size = points.size();
			if(size > MAX_RESULT_POINTS) {
				// trim it
				points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
			}
		}
	}

}
