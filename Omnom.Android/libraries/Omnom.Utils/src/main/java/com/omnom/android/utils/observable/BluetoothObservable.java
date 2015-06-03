package com.omnom.android.utils.observable;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import java.util.List;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.BeaconParser;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

/**
 * Created by Ch3D on 03.06.2015.
 */
public class BluetoothObservable {

	private static class ScanCallbackWrapper {
		private BleScanCallback mCallback;

		public BleScanCallback getCallback() {
			return mCallback;
		}

		public void setCallback(final BleScanCallback callback) {
			mCallback = callback;
		}
	}

	private static class ScanCallbackWrapperJB {
		private BleScanCallbackJB mCallback;

		public BleScanCallbackJB getCallback() {
			return mCallback;
		}

		public void setCallback(final BleScanCallbackJB callback) {
			mCallback = callback;
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private static class BleScanCallbackJB implements BluetoothAdapter.LeScanCallback {
		private final BeaconParser mParser;

		private final Subscriber<? super Beacon> mSubscriber;

		public BleScanCallbackJB(final BeaconParser parser, final Subscriber<? super Beacon> subscriber) {
			mParser = parser;
			mSubscriber = subscriber;
		}

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
			final Beacon beacon = mParser.fromScanData(scanRecord, rssi, device);
			if(beacon != null) {
				mSubscriber.onNext(beacon);
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private static class BleScanCallback extends ScanCallback {
		private final BeaconParser mParser;

		private final Subscriber<? super Beacon> mSubscriber;

		private BleScanCallback(BeaconParser parser, final Subscriber<? super Beacon> subscriber) {
			mParser = parser;
			mSubscriber = subscriber;
		}

		@Override
		public void onScanResult(final int callbackType, final ScanResult scanResult) {
			handleScanResult(scanResult);
		}

		private void handleScanResult(final ScanResult scanResult) {
			final Beacon beacon = getBeacon(scanResult);
			if(beacon != null) {
				mSubscriber.onNext(beacon);
			}
		}

		@Nullable
		private Beacon getBeacon(final ScanResult scanResult) {
			final ScanRecord scanRecord = scanResult.getScanRecord();
			if(scanRecord == null) {
				return null;
			}
			final Beacon beacon = mParser.fromScanData(scanRecord.getBytes(), scanResult.getRssi(), scanResult.getDevice());
			if(beacon == null) {
				return null;
			}
			return beacon;
		}

		@Override
		public void onBatchScanResults(final List<ScanResult> results) {
			for(int i = 0; i < results.size(); i++) {
				handleScanResult(results.get(i));
			}
		}
	}

	/**
	 * This Handler implements the only functionality - to stop BLE scanning
	 */
	private static class SubscriberHandler extends Handler {
		public static final int MSG_SCAN_STOP = 1;

		public SubscriberHandler(final Subscriber<? extends Object> subscriber) {
			super(new Callback() {
				@Override
				public boolean handleMessage(final Message msg) {
					switch(msg.what) {
						case MSG_SCAN_STOP:
							subscriber.onCompleted();
					}
					return false;
				}
			});
		}

		public void sendStopDelayed(final int delay) {
			sendMessageDelayed(obtainMessage(MSG_SCAN_STOP), delay);
		}
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public static Observable<Beacon> getScanResultObservable(final BeaconParser parser,
	                                                         final BluetoothLeScanner scanner,
	                                                         final ScanSettings scanSettings,
	                                                         final int duration) {
		final ScanCallbackWrapper callbackWrapper = new ScanCallbackWrapper();
		return Observable.create(new Observable.OnSubscribe<Beacon>() {
			@Override
			public void call(final Subscriber<? super Beacon> subscriber) {
				if(scanner == null) {
					subscriber.onCompleted();
					return;
				}
				final BleScanCallback callback = new BleScanCallback(parser, subscriber);
				final SubscriberHandler handler = new SubscriberHandler(subscriber);
				callbackWrapper.setCallback(callback);
				scanner.startScan(null, scanSettings, callback);
				handler.sendStopDelayed(duration);
			}
		}).doOnUnsubscribe(new Action0() {
			@Override
			public void call() {
				if(scanner != null && callbackWrapper != null) {
					scanner.stopScan(callbackWrapper.getCallback());
				}
			}
		});
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static Observable<Beacon> getScanResultObservableJB(final BeaconParser parser,
	                                                           final BluetoothAdapter adapter,
	                                                           final int duration) {
		final ScanCallbackWrapperJB callbackWrapper = new ScanCallbackWrapperJB();
		return Observable.create(new Observable.OnSubscribe<Beacon>() {
			@Override
			public void call(final Subscriber<? super Beacon> subscriber) {
				if(adapter == null) {
					subscriber.onCompleted();
					return;
				}
				final BleScanCallbackJB callback = new BleScanCallbackJB(parser, subscriber);
				final SubscriberHandler handler = new SubscriberHandler(subscriber);
				callbackWrapper.setCallback(callback);
				adapter.startLeScan(callback);
				handler.sendStopDelayed(duration);
			}
		}).doOnUnsubscribe(new Action0() {
			@Override
			public void call() {
				if(adapter != null || callbackWrapper != null) {
					adapter.stopLeScan(callbackWrapper.getCallback());
				}
			}
		});
	}
}
