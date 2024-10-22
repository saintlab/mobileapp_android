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

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.clipboard.ClipboardInterface;
import com.google.zxing.client.android.history.HistoryItem;
import com.google.zxing.client.android.history.HistoryManager;
import com.google.zxing.client.android.result.ResultButtonListener;
import com.google.zxing.client.android.result.ResultHandler;
import com.omnom.android.utils.activity.BaseFragmentActivity;
import com.omnom.android.zxing.R;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

/**
 * This activity opens the camera and does the actual scanning on a background thread. It draws a
 * viewfinder to help the user place the barcode correctly, shows feedback as the image processing
 * is happening, and then overlays the results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public class CaptureActivity extends BaseFragmentActivity implements SurfaceHolder.Callback {

	public static final int HISTORY_REQUEST_CODE = 0x0000bacc;

	public static final String EXTRA_SCANNED_URI = "zxing.scan.result";

	private static final String TAG = CaptureActivity.class.getSimpleName();

	private static final long DEFAULT_INTENT_RESULT_DURATION_MS = 1500L;

	private static final long BULK_MODE_SCAN_DELAY_MS = 1000L;

	private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES = EnumSet.of(ResultMetadataType.ISSUE_NUMBER,
	                                                                                            ResultMetadataType.SUGGESTED_PRICE,
	                                                                                            ResultMetadataType
			                                                                                            .ERROR_CORRECTION_LEVEL,
	                                                                                            ResultMetadataType
			                                                                                            .POSSIBLE_COUNTRY);

	private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b, float scaleFactor) {
		if(a != null && b != null) {
			canvas.drawLine(scaleFactor * a.getX(), scaleFactor * a.getY(), scaleFactor * b.getX(), scaleFactor * b.getY(), paint);
		}
	}

	private CameraManager cameraManager;

	private CaptureActivityHandler handler;

	private Result savedResultToShow;

	private ViewfinderView viewfinderView;

	private View resultView;

	private Result lastResult;

	private boolean hasSurface;

	private boolean copyToClipboard;

	private IntentSource source;

	private String sourceUrl;

	private ScanFromWebPageManager scanFromWebPageManager;

	private Collection<BarcodeFormat> decodeFormats;

	private Map<DecodeHintType, ?> decodeHints;

	private String characterSet;

	private HistoryManager historyManager;

	private InactivityTimer inactivityTimer;

	private AmbientLightManager ambientLightManager;

    private Rect framingRect;

	private CameraManager.TorchListener torchListener;

	ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		hasSurface = false;
		historyManager = new HistoryManager(this);
		historyManager.trimHistory();
		inactivityTimer = new InactivityTimer(this);
		ambientLightManager = new AmbientLightManager(this);

		initUI();

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	}

	protected void initUI() {

	}

	@Override
	public int getLayoutResource() {
		return R.layout.capture;
	}

	@Override
	protected void onResume() {
		super.onResume();

		// CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
		// want to open the camera driver and measure the screen size if we're going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the wrong size and partially
		// off screen.
		cameraManager = new CameraManager(getApplication());
        cameraManager.setFraming(framingRect);
        cameraManager.setTorchListener(torchListener);

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		viewfinderView.setCameraManager(cameraManager);

		// resultView = findViewById(R.id.result_view);
		// statusView = (TextView) findViewById(R.id.status_view);

		handler = null;
		lastResult = null;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if(prefs.getBoolean(PreferencesActivity.KEY_DISABLE_AUTO_ORIENTATION, true)) {
			// TODO: switched to portrait mode
			//setRequestedOrientation(getCurrentOrientation());
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		}

		resetStatusView();

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if(hasSurface) {
			// The activity was paused but not stopped, so the surface still exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the camera.
			surfaceHolder.addCallback(this);
		}

		surfaceView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					final CaptureActivity activity = CaptureActivity.this;
					if (activity.cameraManager != null) {
						activity.cameraManager.autoFocus();
					}
				}
				return true;
			}
		});

		ambientLightManager.start(cameraManager);

		inactivityTimer.onResume();

		Intent intent = getIntent();

		copyToClipboard = prefs.getBoolean(PreferencesActivity.KEY_COPY_TO_CLIPBOARD, true) && (intent == null || intent.getBooleanExtra(
				Intents.Scan.SAVE_HISTORY, true));

		source = IntentSource.NONE;
		decodeFormats = null;
		characterSet = null;

		if(intent != null) {

			String action = intent.getAction();
			String dataString = intent.getDataString();

			if(Intents.Scan.ACTION.equals(action)) {

				// Scan the formats the intent requested, and return the result to the calling activity.
				source = IntentSource.NATIVE_APP_INTENT;
				decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
				decodeHints = DecodeHintManager.parseDecodeHints(intent);

				if(intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
					int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
					int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
					if(width > 0 && height > 0) {
						cameraManager.setManualFramingRect(width, height);
					}
				}

				if(intent.hasExtra(Intents.Scan.CAMERA_ID)) {
					int cameraId = intent.getIntExtra(Intents.Scan.CAMERA_ID, -1);
					if(cameraId >= 0) {
						cameraManager.setManualCameraId(cameraId);
					}
				}

				//String customPromptMessage = intent.getStringExtra(Intents.Scan.PROMPT_MESSAGE);
				//if(customPromptMessage != null) {
				//	statusView.setText(customPromptMessage);
				//}

			} else if(dataString != null &&
					dataString.contains("http://www.google") &&
					dataString.contains("/m/products/scan")) {

				// Scan only products and send the result to mobile Product Search.
				source = IntentSource.PRODUCT_SEARCH_LINK;
				sourceUrl = dataString;
				decodeFormats = DecodeFormatManager.PRODUCT_FORMATS;

			}

			characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);

		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}

	private int getCurrentOrientation() {
		int rotation = getWindowManager().getDefaultDisplay().getRotation();
		switch(rotation) {
			case Surface.ROTATION_0:
			case Surface.ROTATION_90:
				return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			default:
				return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
		}
	}

	@Override
	protected void onPause() {
		if(handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		ambientLightManager.stop();
		cameraManager.closeDriver();
		if(!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if(source == IntentSource.NATIVE_APP_INTENT) {
					setResult(RESULT_CANCELED);
					finish();
					return true;
				}
				if((source == IntentSource.NONE || source == IntentSource.ZXING_LINK) && lastResult != null) {
					restartPreviewAfterDelay(0L);
					return true;
				}
				break;
			case KeyEvent.KEYCODE_FOCUS:
			case KeyEvent.KEYCODE_CAMERA:
				// Handle these events so they don't launch the Camera app
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.capture, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		//    switch (item.getItemId()) {
		//      case R.id.menu_share:
		//        intent.setClassName(this, ShareActivity.class.getName());
		//        startActivity(intent);
		//        break;
		//
		//      case R.id.menu_history:
		//        intent.setClassName(this, HistoryActivity.class.getName());
		//        startActivityForResult(intent, HISTORY_REQUEST_CODE);
		//        break;
		//
		//      case R.id.menu_settings:
		//        intent.setClassName(this, PreferencesActivity.class.getName());
		//        startActivity(intent);
		//        break;
		//
		//      case R.id.menu_help:
		//        intent.setClassName(this, HelpActivity.class.getName());
		//        startActivity(intent);
		//        break;
		//
		//      default:
		//        return super.onOptionsItemSelected(item);
		//    }
		return true;
	}

	@Override
	public void initUi() {

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(resultCode == RESULT_OK) {
			if(requestCode == HISTORY_REQUEST_CODE) {
				int itemNumber = intent.getIntExtra(Intents.History.ITEM_NUMBER, -1);
				if(itemNumber >= 0) {
					HistoryItem historyItem = historyManager.buildHistoryItem(itemNumber);
					decodeOrStoreSavedBitmap(null, historyItem.getResult());
				}
			}
		}
	}

	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		// Bitmap isn't used yet -- will be used soon
		if(handler == null) {
			savedResultToShow = result;
		} else {
			if(result != null) {
				savedResultToShow = result;
			}
			if(savedResultToShow != null) {
				Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if(holder == null) {
			Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if(!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (this.cameraManager != null) {
			this.cameraManager.autoFocus();
		}
	}

	/**
	 * A valid barcode has been found, so give an indication of success and show the results.
	 *
	 * @param rawResult   The contents of the barcode.
	 * @param scaleFactor amount by which thumbnail was scaled
	 * @param barcode     A greyscale bitmap of the camera data which was decoded.
	 */
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		inactivityTimer.onActivity();
		lastResult = rawResult;
		setResult(RESULT_OK, new Intent().putExtra(EXTRA_SCANNED_URI, rawResult.getText()));
		finish();
		//		ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);
		//		boolean fromLiveScan = barcode != null;
		//		if (fromLiveScan) {
		//			historyManager.addHistoryItem(rawResult, resultHandler);
		//			// Then not from history, so beep/vibrate and we have an image to draw on
		//			drawResultPoints(barcode, scaleFactor, rawResult);
		//		}
		//
		//		switch (source) {
		//			case NATIVE_APP_INTENT:
		//			case PRODUCT_SEARCH_LINK:
		//				handleDecodeExternally(rawResult, resultHandler, barcode);
		//				break;
		//
		//			case ZXING_LINK:
		//				if (scanFromWebPageManager == null || !scanFromWebPageManager.isScanFromWebPage()) {
		//					handleDecodeInternally(rawResult, resultHandler, barcode);
		//				} else {
		//					handleDecodeExternally(rawResult, resultHandler, barcode);
		//				}
		//				break;
		//
		//			case NONE:
		//				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		//				if (fromLiveScan && prefs.getBoolean(PreferencesActivity.KEY_BULK_MODE, false)) {
		//					Toast.makeText(getApplicationContext(),
		//					               getResources().getString(R.string.msg_bulk_mode_scanned) + " (" + rawResult.getText() + ')',
		//					               Toast.LENGTH_SHORT).show();
		//					// Wait a moment or else it will scan the same barcode continuously about 3 times
		//					restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
		//				} else {
		//					handleDecodeInternally(rawResult, resultHandler, barcode);
		//				}
		//				break;
		//		}
	}

	/**
	 * Superimpose a line for 1D or dots for 2D to highlight the key features of the barcode.
	 *
	 * @param barcode     A bitmap of the captured image.
	 * @param scaleFactor amount by which thumbnail was scaled
	 * @param rawResult   The decoded results which contains the points to draw.
	 */
	private void drawResultPoints(Bitmap barcode, float scaleFactor, Result rawResult) {
		ResultPoint[] points = rawResult.getResultPoints();
		if(points != null && points.length > 0) {
			Canvas canvas = new Canvas(barcode);
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.result_points));
			if(points.length == 2) {
				paint.setStrokeWidth(4.0f);
				drawLine(canvas, paint, points[0], points[1], scaleFactor);
			} else if(points.length == 4 && (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A
					|| rawResult.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
				// Hacky special case -- draw two lines, for the barcode and metadata
				drawLine(canvas, paint, points[0], points[1], scaleFactor);
				drawLine(canvas, paint, points[2], points[3], scaleFactor);
			} else {
				paint.setStrokeWidth(10.0f);
				for(ResultPoint point : points) {
					if(point != null) {
						canvas.drawPoint(scaleFactor * point.getX(), scaleFactor * point.getY(), paint);
					}
				}
			}
		}
	}

	// Put up our own UI for how to handle the decoded contents.
	private void handleDecodeInternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {

		CharSequence displayContents = resultHandler.getDisplayContents();

		if(copyToClipboard && !resultHandler.areContentsSecure()) {
			ClipboardInterface.setText(displayContents, this);
		}

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if(resultHandler.getDefaultButtonID() != null && prefs.getBoolean(PreferencesActivity.KEY_AUTO_OPEN_WEB, false)) {
			resultHandler.handleButtonPress(resultHandler.getDefaultButtonID());
			return;
		}

		viewfinderView.setVisibility(View.GONE);

		ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
		if(barcode == null) {
			barcodeImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.launcher_icon));
		} else {
			barcodeImageView.setImageBitmap(barcode);
		}

		int buttonCount = resultHandler.getButtonCount();
		ViewGroup buttonView = (ViewGroup) findViewById(R.id.result_button_view);
		buttonView.requestFocus();
		for(int x = 0; x < ResultHandler.MAX_BUTTON_COUNT; x++) {
			TextView button = (TextView) buttonView.getChildAt(x);
			if(x < buttonCount) {
				button.setVisibility(View.VISIBLE);
				button.setText(resultHandler.getButtonText(x));
				button.setOnClickListener(new ResultButtonListener(resultHandler, x));
			} else {
				button.setVisibility(View.GONE);
			}
		}

	}

	// Briefly show the contents of the barcode, then handle the result outside Barcode Scanner.
	private void handleDecodeExternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {

		if(barcode != null) {
			viewfinderView.drawResultBitmap(barcode);
		}

		long resultDurationMS;
		if(getIntent() == null) {
			resultDurationMS = DEFAULT_INTENT_RESULT_DURATION_MS;
		} else {
			resultDurationMS = getIntent().getLongExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS, DEFAULT_INTENT_RESULT_DURATION_MS);
		}

		if(resultDurationMS > 0) {
			String rawResultString = String.valueOf(rawResult);
			if(rawResultString.length() > 32) {
				rawResultString = rawResultString.substring(0, 32) + " ...";
			}
			// statusView.setText(getString(resultHandler.getDisplayTitle()) + " : " + rawResultString);
		}

		if(copyToClipboard && !resultHandler.areContentsSecure()) {
			CharSequence text = resultHandler.getDisplayContents();
			ClipboardInterface.setText(text, this);
		}

		if(source == IntentSource.NATIVE_APP_INTENT) {

			// Hand back whatever action they requested - this can be changed to Intents.Scan.ACTION when
			// the deprecated intent is retired.
			Intent intent = new Intent(getIntent().getAction());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
			intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
			byte[] rawBytes = rawResult.getRawBytes();
			if(rawBytes != null && rawBytes.length > 0) {
				intent.putExtra(Intents.Scan.RESULT_BYTES, rawBytes);
			}
			Map<ResultMetadataType, ?> metadata = rawResult.getResultMetadata();
			if(metadata != null) {
				if(metadata.containsKey(ResultMetadataType.UPC_EAN_EXTENSION)) {
					intent.putExtra(Intents.Scan.RESULT_UPC_EAN_EXTENSION, metadata.get(ResultMetadataType.UPC_EAN_EXTENSION).toString());
				}
				Number orientation = (Number) metadata.get(ResultMetadataType.ORIENTATION);
				if(orientation != null) {
					intent.putExtra(Intents.Scan.RESULT_ORIENTATION, orientation.intValue());
				}
				String ecLevel = (String) metadata.get(ResultMetadataType.ERROR_CORRECTION_LEVEL);
				if(ecLevel != null) {
					intent.putExtra(Intents.Scan.RESULT_ERROR_CORRECTION_LEVEL, ecLevel);
				}
				@SuppressWarnings("unchecked") Iterable<byte[]> byteSegments = (Iterable<byte[]>) metadata.get(
						ResultMetadataType.BYTE_SEGMENTS);
				if(byteSegments != null) {
					int i = 0;
					for(byte[] byteSegment : byteSegments) {
						intent.putExtra(Intents.Scan.RESULT_BYTE_SEGMENTS_PREFIX + i, byteSegment);
						i++;
					}
				}
			}
			sendReplyMessage(R.id.return_scan_result, intent, resultDurationMS);

		} else if(source == IntentSource.PRODUCT_SEARCH_LINK) {

			// Reformulate the URL which triggered us into a query, so that the request goes to the same
			// TLD as the scan URL.
			int end = sourceUrl.lastIndexOf("/scan");
			String replyURL = sourceUrl.substring(0, end) + "?q=" + resultHandler.getDisplayContents() + "&source=zxing";
			sendReplyMessage(R.id.launch_product_query, replyURL, resultDurationMS);

		} else if(source == IntentSource.ZXING_LINK) {

			if(scanFromWebPageManager != null && scanFromWebPageManager.isScanFromWebPage()) {
				String replyURL = scanFromWebPageManager.buildReplyURL(rawResult, resultHandler);
				sendReplyMessage(R.id.launch_product_query, replyURL, resultDurationMS);
			}

		}
	}

	private void sendReplyMessage(int id, Object arg, long delayMS) {
		if(handler != null) {
			Message message = Message.obtain(handler, id, arg);
			if(delayMS > 0L) {
				handler.sendMessageDelayed(message, delayMS);
			} else {
				handler.sendMessage(message);
			}
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		if(surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if(cameraManager.isOpen()) {
			Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a RuntimeException.
			if(handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		} catch(IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch(RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if(handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
		resetStatusView();
	}

	private void resetStatusView() {
		// resultView.setVisibility(View.GONE);
		//statusView.setText(R.string.msg_default_status);
		//statusView.setVisibility(View.VISIBLE);
		// viewfinderView.setVisibility(View.VISIBLE);
		lastResult = null;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

    protected void setFramingRect(final Rect rect) {
		this.framingRect = rect;
		if (cameraManager != null) {
			cameraManager.setFraming(rect);
		}
	}

	protected boolean getTorchState() {
		return cameraManager != null && cameraManager.getTorchState();
	}

	protected void setTorchListener(final CameraManager.TorchListener torchListener) {
		this.torchListener = torchListener;
		if (cameraManager != null) {
			cameraManager.setTorchListener(torchListener);
		}
	}

	protected void setTorch(final boolean turnOn, final boolean isManual) {
		if (cameraManager != null) {
			cameraManager.setTorch(turnOn, isManual);
		}
	}

}
