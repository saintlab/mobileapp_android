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
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.SplashActivity;
import com.omnom.android.beacon.BeaconFilter;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.ResponseBase;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.BluetoothUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.BeaconParser;
import hugo.weaving.DebugLog;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;
import static android.os.Build.VERSION_CODES.KITKAT;

public class BackgroundBleService extends Service {

	private static final int SECOND = 1000;

	private static final int SCAN_DURATION = 5 * SECOND;

	private static final int MINUTE = 60 * SECOND;

	private static final long ALARM_INTERVAL = MINUTE;

	private static final long BEACON_CACHE_INTERVAL = 5 * MINUTE;

	private static final String TAG = BackgroundBleService.class.getSimpleName();

	private static final String BLE_SERVICE_PREFERENCES = TAG;

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
			mBeacons.add(beacon);
		}
	}

	public Callback mLeScanCallback;

	@Inject
	protected RestaurateurObeservableApi api;

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
		this.mStartId = startId;
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		OmnomApplication.get(this).inject(this);
		if(!AndroidUtils.isJellyBeanMR2() || !BluetoothUtils.hasBleSupport(this)) {
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
				if(!mBleState) {
					mBluetoothAdapter.disable();
				}

				clearCachedBeacons();
				notifyIfNeeded(mBeacons);

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

	/**
	 * Clear cached beacons which timestamp
	 */
	private void clearCachedBeacons() {
		final SharedPreferences sharedPreferences = getSharedPreferences(BLE_SERVICE_PREFERENCES, MODE_PRIVATE);
		final SharedPreferences.Editor editor = sharedPreferences.edit();
		for(Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
			final Long timestamp = (Long) entry.getValue();
			if(timestamp + BEACON_CACHE_INTERVAL < SystemClock.elapsedRealtime()) {
				editor.remove(entry.getKey());
			}
		}
		editor.apply();
	}

	@DebugLog
	private void notifyIfNeeded(final ArrayList<Beacon> beacons) {
		ArrayList<Beacon> omnBeacons = new ArrayList<Beacon>();
		final BeaconFilter filter = new BeaconFilter(OmnomApplication.get(this));
		for(final Beacon b : beacons) {
			if(filter.check(b)) {
				omnBeacons.add(b);
			}
		}
		if(omnBeacons.size() > 0) {
			final List<Beacon> filteredBeacons = filter.filterBeacons(omnBeacons);
			if(filteredBeacons.size() == 1) {
				final Beacon beacon = filteredBeacons.get(0);
				final TableDataResponse[] table = new TableDataResponse[1];
				if(!isHandled(beacon)) {
					api.findBeacon(beacon)
					   .flatMap(new Func1<TableDataResponse, Observable<Restaurant>>() {
						   @Override
						   public Observable<Restaurant> call(final TableDataResponse tableDataResponse) {
							   if(!tableDataResponse.hasErrors()) {
								   table[0] = tableDataResponse;
								   return api.getRestaurant(tableDataResponse.getRestaurantId());
							   }
							   return Observable.empty();
						   }
					   })
					   .flatMap(new Func1<Restaurant, Observable<ResponseBase>>() {
						   @Override
						   public Observable<ResponseBase> call(final Restaurant restaurant) {
							   if(restaurant != null) {
								   cacheBeacon(beacon);
								   showNotification(beacon, restaurant);
								   return api.newGuest(restaurant.getId(), table[0].getId());
							   }
							   return Observable.empty();
						   }
					   })
					   .subscribe(new Action1<ResponseBase>() {
						   @Override
						   public void call(final ResponseBase o) {
							   // Do nothing
						   }
					   }, new Action1<Throwable>() {
						   @Override
						   public void call(final Throwable throwable) {
							   Log.e(TAG, "notifyIfNeeded", throwable);
						   }
					   });
				}
			}
		}
	}

	private void cacheBeacon(final Beacon beacon) {
		final SharedPreferences sharedPreferences = getSharedPreferences(BLE_SERVICE_PREFERENCES, MODE_PRIVATE);
		sharedPreferences.edit().putLong(beacon.getBluetoothAddress(), SystemClock.elapsedRealtime()).apply();
	}

	/**
	 * @return <code>true</code> if beacon is already handled.
	 * This means that notification for this beacon already shown.
	 */
	@DebugLog
	private boolean isHandled(final Beacon beacon) {
		final SharedPreferences sharedPreferences = getSharedPreferences(BLE_SERVICE_PREFERENCES, MODE_PRIVATE);
		final boolean contains = sharedPreferences.contains(beacon.getBluetoothAddress());
		if(contains) {
			final long timestamp = sharedPreferences.getLong(beacon.getBluetoothAddress(), 0);
			return timestamp < SystemClock.elapsedRealtime() + BEACON_CACHE_INTERVAL;
		}
		return false;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@DebugLog
	private void showNotification(final Beacon beacon, final Restaurant restaurant) {
		final Notification notification = new Notification.Builder(this)
				.setSmallIcon(R.drawable.ic_app)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(getString(R.string.welcome_to_, restaurant.getTitle()))
				.setContentIntent(getNotificationIntent())
				.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_ALL)
				.setOnlyAlertOnce(true)
				.setPriority(Notification.PRIORITY_HIGH).build();

		final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(beacon.getId3().toInt(), notification);
	}

	private PendingIntent getNotificationIntent() {
		final Intent intent = new Intent(this, SplashActivity.class);
		intent.setAction(Intent.ACTION_MAIN)
		      .addCategory(Intent.CATEGORY_LAUNCHER)
		      .setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		return PendingIntent.getActivity(this, 0, intent, 0);
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
