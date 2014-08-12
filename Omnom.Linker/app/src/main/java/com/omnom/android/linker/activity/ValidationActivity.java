package com.omnom.android.linker.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.service.BluetoothLeService;
import com.omnom.android.linker.service.RBLBluetoothAttributes;
import com.omnom.android.linker.utils.AndroidUtils;
import com.omnom.android.linker.utils.AnimationBuilder;
import com.omnom.android.linker.utils.AnimationUtils;
import com.omnom.android.linker.utils.ViewUtils;
import com.omnom.android.linker.widget.loader.LoaderView;

import java.util.ArrayList;
import java.util.List;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.BeaconParser;
import altbeacon.beacon.Identifier;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;

public class ValidationActivity extends BaseActivity {

	private static final String TAG               = ValidationActivity.class.getSimpleName();
	private static final int    REQUEST_ENABLE_BT = 100;
	private static final long   BLE_SCAN_PERIOD   = 2000;

	private static final int MSG_LE_STOP = 0x01;

	private class ValidationAsyncTask extends AsyncTask<Void, Integer, Integer> {
		private final int ERROR_CODE_EMPTY   = -1;
		private final int ERROR_CODE_NETWORK = 0;
		private final int ERROR_CODE_SERVER  = 1;

		private final CountDownTimer countDownTimer;

		private ValidationActivity activity;

		private volatile boolean mFinished = false;

		public ValidationAsyncTask(ValidationActivity activity) {
			this.activity = activity;
			final int progressMax = activity.getResources().getInteger(R.integer.loader_progress_max);
			final int timeMax = activity.getResources().getInteger(R.integer.loader_time_max);
			final int tick = activity.getResources().getInteger(R.integer.loader_tick_interval);
			final int ticksCount = timeMax / tick;
			final int magic = progressMax / ticksCount;

			countDownTimer = new CountDownTimer(timeMax, tick) {
				int j = 0;

				@Override
				public void onTick(long millisUntilFinished) {
					j += magic * 2;
					publishProgress(j);
				}

				@Override
				public void onFinish() {
					publishProgress(progressMax);
					mFinished = true;
				}
			};
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			publishProgress(0);
			countDownTimer.start();
			loader.animateColor(Color.BLACK);
		}

		@Override
		protected Integer doInBackground(Void... params) {
			if(!AndroidUtils.hasConnection(activity)) {
				return ERROR_CODE_NETWORK;
			}
			while(!mFinished) {
				SystemClock.sleep(activity.getResources().getInteger(R.integer.loader_tick_interval));
			}
			return ERROR_CODE_EMPTY;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			activity.onProgress(values[0]);
		}

		@Override
		protected void onPostExecute(final Integer errorCode) {
			switch(errorCode) {
				case ERROR_CODE_NETWORK:
					activity.setError("Пожалуйста проверьте подключение к сети");
					break;

				default:
					activity.setError("OK");
					loader.scaleUp(new LoaderView.Callback() {
						@Override
						public void execute() {
							startActivity(RestaurantsListActivity.class, AnimationUtils.DURATION_LONG);
						}
					});
					break;
			}
		}
	}

	@InjectView(R.id.loader)
	protected LoaderView loader;
	@InjectView(R.id.txt_error)
	protected TextView   txtError;
	@InjectView(R.id.btn_settings)
	protected Button     btnSettings;
	@InjectViews({R.id.txt_error, R.id.panel_bottom})
	protected List<View> errorViews;

	private BroadcastReceiver gattConnectedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(intent.getAction())) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						final BluetoothGattService service = mBluetoothLeService.getService(
								RBLBluetoothAttributes.UUID_BLE_REDBEAR_PASSWORD_SERVICE);
						final BluetoothGattCharacteristic characteristic = service.getCharacteristic(
								RBLBluetoothAttributes.UUID_BLE_REDBEAR_PASSWORD);
						characteristic.setValue(RBLBluetoothAttributes.RBL_PASSKEY);
						mBluetoothLeService.writeCharacteristic(characteristic);
					}
				});
			}
			if(intent.getAction() == BluetoothLeService.ACTION_GATT_CONNECTED) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								mBluetoothLeService.getDiscoverGattService();
							}
						});
					}
				});
			}
		}
	};
	private boolean          mBound;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mBtEnabled = false;
	private int loaderSize;
	private List<Beacon>                    beacons         = new ArrayList<Beacon>();
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					final BeaconParser parser = new BeaconParser();
					parser.setBeaconLayout(RBLBluetoothAttributes.REDBEAR_BEACON_LAYOUT);
					final Beacon beacon = parser.fromScanData(scanRecord, rssi, device);
					if(beacon != null && beacon.getId1() != null) {
						Identifier id1 = beacon.getId1();
						final String beaconId = id1.toString().toLowerCase();
						if(RBLBluetoothAttributes.BEACON_ID.equals(beaconId)) {
							beacons.add(beacon);
							// mBluetoothLeService.connect(beacon.getBluetoothAddress());
						}
					}
				}
			});
		}
	};
	private Handler                         mHandler        = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == MSG_LE_STOP) {
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
			}
		}
	};

	private BluetoothLeService mBluetoothLeService;
	private final ServiceConnection mServiceConnection      = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if(!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			scanBleDevices(true);
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
			mBound = false;
		}
	};
	private       boolean           mGattReceiverRegistered = false;

	@Override
	public void initUi() {
		loaderSize = getResources().getDimensionPixelSize(R.dimen.loader_size);
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
	}

	private boolean checkBluetoothEnabled() {
		if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			mBtEnabled = false;
		} else {
			mBtEnabled = true;
		}
		return mBtEnabled;
	}

	private void scanBleDevices(boolean enable) {
		if(enable) {
			beacons.clear();
			findViewById(android.R.id.content).postDelayed(new Runnable() {
				@Override
				public void run() {
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
			}, BLE_SCAN_PERIOD);
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_ENABLE_BT) {
			mBtEnabled = resultCode == RESULT_OK ? true : false;
			if(mBtEnabled) {
				scanBleDevices(true);
			}
		}
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_validation;
	}

	public void onProgress(int progress) {
		if(progress == 0 || progress >= getResources().getInteger(R.integer.loader_progress_max)) {
			loader.showProgress(false);
		} else {
			loader.showProgress(true);
		}
		loader.updateProgress(progress);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mGattReceiverRegistered) {
			unregisterReceiver(gattConnectedReceiver);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// TODO:
		//		if(mBluetoothLeService == null) {
		//			bindService(new Intent(this, BluetoothLeService.class), mServiceConnection, BIND_AUTO_CREATE);
		//		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		// TODO:
		//		if(mBound) {
		//			unbindService(mServiceConnection);
		//			mBound = false;
		//		}
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();

		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);

		loader.showProgress(false);
		loader.scaleDown(null);

		if(!AndroidUtils.isLocationEnabled(this)) {
			showLocationError();
			return;
		}
		if(!checkBluetoothEnabled()) {
			showErrorBluetoothDisabled();
			return;
		}

		loader.setLogo(R.drawable.ic_fork_n_knife);
		animateStart();
		IntentFilter filter = new IntentFilter(BluetoothLeService.ACTION_GATT_CONNECTED);
		filter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		registerReceiver(gattConnectedReceiver, filter);
		mGattReceiverRegistered = true;
	}

	private void showErrorBluetoothDisabled() {
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, true);
		loader.setLogo(R.drawable.ic_bluetooth_white);
		txtError.setText(getString(R.string.error_bluetooth_disabled));
		btnSettings.setText(getString(R.string.open_settings));
		btnSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loader.scaleDown(null);
				ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
				startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
			}
		});
	}

	private void showLocationError() {
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, true);
		loader.setLogo(R.drawable.ic_geolocation_white);
		txtError.setText(getString(R.string.error_location_disabled));
		btnSettings.setText(R.string.open_settings);
		btnSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loader.scaleDown(null);
				ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
				AndroidUtils.startLocationSettings(v.getContext());
			}
		});
	}

	private void animateStart() {
		loader.showProgress(true);
		loader.scaleDown(null, new AnimationBuilder.Action() {
			@Override
			public void invoke() {
				new ValidationAsyncTask(ValidationActivity.this).execute();
			}
		});
	}

	private void setError(String s) {
		txtError.setText(s);
	}
}
