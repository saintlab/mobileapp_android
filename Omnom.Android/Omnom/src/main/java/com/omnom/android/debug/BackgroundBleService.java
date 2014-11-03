package com.omnom.android.debug;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.omnom.android.R;
import com.omnom.android.utils.utils.AndroidUtils;

import java.util.ArrayList;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.BeaconParser;

import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;

public class BackgroundBleService extends Service {

	private static final String TAG = BackgroundBleService.class.getSimpleName();

	private static final int SCAN_DURATION = 5000;

	@SuppressLint("NewApi")
	private class Callback implements BluetoothAdapter.LeScanCallback {
		private final BeaconParser mParser;

		private final ArrayList<Beacon> mBeacons;

		private Callback(BeaconParser parser, ArrayList<Beacon> beacons) {
			mParser = parser;
			mBeacons = beacons;
		}

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
			final Beacon beacon = mParser.fromScanData(scanRecord, rssi, device);
			Log.e(TAG, "Beacon found:\n" + beacon.toDebugString());
			mBeacons.add(beacon);
		}
	}

	public Callback mLeScanCallback;

	private BluetoothAdapter mBluetoothAdapter;

	private Handler mHandler;

	private BeaconParser mParser;

	private ArrayList<Beacon> mBeacons;

	private boolean mBleStateEnabled;

	private Runnable endCallback;

	public BackgroundBleService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		Log.e(TAG, "Starting service with : " + intent);
		processIntent(intent);
		return START_FLAG_REDELIVERY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "Destroying service");
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		super.onCreate();
		if(!AndroidUtils.isJellyBean()) {
			Log.e(TAG, "Skipping scan : device doesn't support BLE");
			stopSelf();
			return;
		}

		Log.e(TAG, "Initializing data");
		mHandler = new Handler();
		mParser = new BeaconParser();
		mParser.setBeaconLayout(getResources().getString(R.string.redbear_beacon_layout));
		mBeacons = new ArrayList<Beacon>();
		BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		mBleStateEnabled = mBluetoothAdapter.isEnabled();
		mLeScanCallback = new Callback(mParser, mBeacons);
		endCallback = new Runnable() {
			@Override
			public void run() {
				if(mBeacons != null) {
					if(mBeacons.size() == 0) {
						Log.e(TAG, "BLE Scan finished : no beacons found");
					} else {
						Log.e(TAG, "BLE Scan finished : Found " + mBeacons.size() + " advertisings");
						for(Beacon b : mBeacons) {
							Log.e(TAG, "BLE Scan : BeaconData = [" + b.toDebugString() + "]");
						}
					}
				} else {
					Log.e(TAG, "BLE Scan finished : no beacons found");
				}
				if(!mBleStateEnabled) {
					mBluetoothAdapter.disable();
				}
				stopSelf();
			}
		};

		if(!mBleStateEnabled) {
			mBluetoothAdapter.enable();
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					scanBleDevices(true, endCallback);
				}
			}, SCAN_DURATION * 2);
		} else {
			scanBleDevices(true, endCallback);
		}
	}

	@TargetApi(JELLY_BEAN_MR2)
	private void scanBleDevices(final boolean enable, final Runnable endCallback) {
		if(enable) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					if(endCallback != null) {
						endCallback.run();
					}
				}
			}, SCAN_DURATION);
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			if(endCallback != null) {
				endCallback.run();
			}
		}
	}

	private void processIntent(final Intent intent) {
		// Do nothing
	}

}
