package com.omnom.android.debug;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.omnom.android.R;
import com.omnom.android.activity.SplashActivity;
import com.omnom.android.utils.utils.AndroidUtils;

import java.util.ArrayList;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.BeaconParser;

import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;

public class BackgroundBleService extends Service implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient
		.OnConnectionFailedListener {

	public static final int MILLISECONDS_PER_SECOND = 1000;

	public static final int DETECTION_INTERVAL_SECONDS = 20;

	public static final int DETECTION_INTERVAL_MILLISECONDS = MILLISECONDS_PER_SECOND * DETECTION_INTERVAL_SECONDS;

	private static final String TAG = BackgroundBleService.class.getSimpleName();

	private GooglePlayServicesClient.OnConnectionFailedListener mLocationFailedCallback =
			new GooglePlayServicesClient.OnConnectionFailedListener() {
				@Override
				public void onConnectionFailed(final ConnectionResult connectionResult) {
					final String errorString = GooglePlayServicesUtil.getErrorString(connectionResult.getErrorCode());
					Log.e(TAG, errorString);
				}
			};

	private static final int SCAN_DURATION = 5000;

	private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 0;

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

	private boolean mIsNearTestLocation;

	/*
	 * Store the PendingIntent used to send activity recognition events
	 * back to the app
	 */
	private PendingIntent mActivityRecognitionPendingIntent;

	// Store the current activity recognition client
	private ActivityRecognitionClient mActivityRecognitionClient;

	private BluetoothAdapter mBluetoothAdapter;

	private Handler mHandler;

	private BeaconParser mParser;

	private ArrayList<Beacon> mBeacons;

	private boolean mBleStateEnabled;

	private Runnable endCallback;

	private boolean mInProgress;

	private LocationClient mLocationClient;

	private boolean mLocationConnected;

	private Location mTestLocation;

	LocationListener mLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(final Location location) {
			if(location.distanceTo(mTestLocation) <= 1000) {
				mIsNearTestLocation |= true;
			}
			Log.e(TAG, "cuurent location = " + location.toString());
		}
	};

	private GooglePlayServicesClient.ConnectionCallbacks mLocationConnectionCallback = new GooglePlayServicesClient.ConnectionCallbacks() {
		@Override
		public void onConnected(final Bundle bundle) {
			mLocationConnected = true;
			LocationRequest mLocationRequest = LocationRequest.create();
			mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
			mLocationRequest.setInterval(5000);
			mLocationRequest.setFastestInterval(5000);
			mLocationRequest.setSmallestDisplacement(100);
			mLocationClient.requestLocationUpdates(mLocationRequest, mLocationListener);
		}

		@Override
		public void onDisconnected() {
			mLocationConnected = false;
		}
	};

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

		mTestLocation = new Location(TAG);
		mTestLocation.setLatitude(54.842716);
		mTestLocation.setLongitude(83.102330);

		if(!AndroidUtils.isJellyBean()) {
			Log.e(TAG, "Skipping scan : device doesn't support BLE");
			stopSelf();
			return;
		}

		final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		final Intent intent = new Intent(this, BackgroundBleService.class);
		final PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent, 0);
		final long triggerMillis = SystemClock.elapsedRealtime() + (AlarmManager.INTERVAL_FIFTEEN_MINUTES / 15);
		alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerMillis, alarmIntent);

		if(servicesConnected()) {
			mActivityRecognitionClient = new ActivityRecognitionClient(this, this, this);
			mLocationClient = new LocationClient(this, mLocationConnectionCallback, mLocationFailedCallback);

			Intent ai = new Intent(this, ActivityRecognitionIntentService.class);
			mActivityRecognitionPendingIntent = PendingIntent.getService(this, 0, ai, PendingIntent.FLAG_UPDATE_CURRENT);

			if(!mInProgress) {
				mInProgress = true;
				mActivityRecognitionClient.connect();
				mLocationClient.connect();
			}
		}
		scanBeacons();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void scanBeacons() {
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
				mLocationClient.removeLocationUpdates(mLocationListener);
				mLocationClient.disconnect();
				mActivityRecognitionClient.removeActivityUpdates(mActivityRecognitionPendingIntent);
				mActivityRecognitionClient.disconnect();

				check();

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

	private void check() {
		if(mIsNearTestLocation && mBeacons.size() > 0) {
			final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			Notification.Builder builder = new Notification.Builder(this);
			final Notification test = builder
					.setContentText("You are near beacon and test location")
					.setContentInfo("Test")
					.setSmallIcon(R.drawable.ic_app)
					.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, SplashActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
					.build();
			nm.notify(1, test);
		}
	}

	private boolean servicesConnected() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(ConnectionResult.SUCCESS == resultCode) {
			Log.d(TAG, "Google Play services is available.");
			return true;
		} else {
			Log.e(TAG, GooglePlayServicesUtil.getErrorString(resultCode));
			return false;
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

	@Override
	public void onConnected(final Bundle bundle) {
		mActivityRecognitionClient.requestActivityUpdates(0, mActivityRecognitionPendingIntent);
		mInProgress = false;
	}

	@Override
	public void onDisconnected() {
		// Turn off the request flag
		mInProgress = false;
		// Delete the client
		mActivityRecognitionClient = null;
	}

	@Override
	public void onConnectionFailed(final ConnectionResult connectionResult) {
		// Turn off the request flag
		mInProgress = false;
		// Get the error code
		int errorCode = connectionResult.getErrorCode();
		// Get the error dialog from Google Play services
		String error = GooglePlayServicesUtil.getErrorString(errorCode);
		Log.e(TAG, error);
	}
}
