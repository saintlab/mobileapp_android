package com.omnom.android.debug;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.omnom.android.R;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.BluetoothUtils;

import java.util.ArrayList;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.BeaconParser;
import hugo.weaving.DebugLog;

import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;
import static android.os.Build.VERSION_CODES.KITKAT;

public class BackgroundBleService extends Service {

	private static final int SECOND = 1000;

	private static final int SCAN_DURATION = 5 * SECOND;

	private static final int MINUTE = 60 * SECOND;

	private static final long ALARM_INTERVAL = MINUTE;

	private static final String TAG = BackgroundBleService.class.getSimpleName();

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
			Log.d(TAG, "Beacon found:\n" + beacon.toDebugString());
			mBeacons.add(beacon);
		}
	}

	public Callback mLeScanCallback;

	private BluetoothAdapter mBluetoothAdapter;

	private Handler mHandler;

	private BeaconParser mParser;

	private ArrayList<Beacon> mBeacons;

	private boolean mBleState;

	private Runnable endCallback;

	private int mStartId;

	public BackgroundBleService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	@DebugLog
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		Log.d(TAG, "Starting service with : " + intent);
		this.mStartId = startId;
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if(!AndroidUtils.isJellyBeanMR2() || !BluetoothUtils.hasBleSupport(this)) {
			Log.d(TAG, "Skipping scan : device doesn't support BLE");
			stopSelf(mStartId);
			return;
		}
		scanBeacons();
	}

	@TargetApi(JELLY_BEAN_MR2)
	@DebugLog
	private void scanBeacons() {
		mHandler = new Handler();
		mParser = new BeaconParser();
		mParser.setBeaconLayout(getResources().getString(R.string.redbear_beacon_layout));
		mBeacons = new ArrayList<Beacon>();
		BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		mBleState = mBluetoothAdapter.isEnabled();
		mLeScanCallback = new Callback(mParser, mBeacons);
		endCallback = new Runnable() {
			@Override
			public void run() {
				if(mBeacons != null) {
					if(mBeacons.size() == 0) {
						Log.d(TAG, "BLE Scan finished : no beacons found");
					} else {
						Log.d(TAG, "BLE Scan finished : Found " + mBeacons.size() + " advertisings");
						for(Beacon b : mBeacons) {
							Log.d(TAG, "BLE Scan : BeaconData = [" + b.toDebugString() + "]");
						}
					}
				} else {
					Log.d(TAG, "BLE Scan finished : no beacons found");
				}
				if(!mBleState) {
					mBluetoothAdapter.disable();
				}
				stopSelf(mStartId);
				scheduleNextAlarm();
			}
		};

		if(!mBleState) {
			mBluetoothAdapter.enable();
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					scanBleDevices(true, endCallback);
				}
			}, SCAN_DURATION);
		} else {
			scanBleDevices(true, endCallback);
		}
	}

	@DebugLog
	private void scheduleNextAlarm() {
		if(Build.VERSION.SDK_INT == KITKAT) {
			scheduleNextAlarmKK();
		} else if(Build.VERSION.SDK_INT == JELLY_BEAN_MR2) {
			scheduleAlarmJB();
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@DebugLog
	private void scheduleAlarmJB() {
		((AlarmManager) getSystemService(ALARM_SERVICE)).set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
		                                                     getTriggetAt(),
		                                                     getPendingIntent());
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@DebugLog
	private void scheduleNextAlarmKK() {
		((AlarmManager) getSystemService(ALARM_SERVICE)).setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
		                                                          getTriggetAt(),
		                                                          getPendingIntent());
	}

	@TargetApi(JELLY_BEAN_MR2)
	@DebugLog
	private void scanBleDevices(final boolean enable, final Runnable endCallback) {
		if(enable) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					stopScan(endCallback);
				}
			}, SCAN_DURATION);
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			stopScan(endCallback);
		}
	}

	private long getTriggetAt() {
		return SystemClock.elapsedRealtime() + (ALARM_INTERVAL);
	}

	private PendingIntent getPendingIntent() {
		return PendingIntent.getService(this, 0, new Intent(this, BackgroundBleService.class), 0);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@DebugLog
	private void stopScan(final Runnable endCallback) {
		mBluetoothAdapter.stopLeScan(mLeScanCallback);
		runCallback(endCallback);
	}

	@DebugLog
	private void runCallback(final Runnable endCallback) {
		if(endCallback != null) {
			endCallback.run();
		}
	}
}
