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
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.service.BluetoothLeService;
import com.omnom.android.linker.service.RBLBluetoothAttributes;
import com.omnom.android.linker.utils.AndroidUtils;
import com.omnom.android.linker.utils.AnimationBuilder;
import com.omnom.android.linker.utils.AnimationUtils;
import com.omnom.android.linker.widget.LoaderView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public class ValidationActivity extends BaseActivity {

	private static final String TAG = ValidationActivity.class.getSimpleName();

	private class ValidationAsyncTask extends AsyncTask<Void, Integer, Integer> {
		private final int ERROR_CODE_EMPTY = -1;
		private final int ERROR_CODE_NETWORK = 0;
		private final int ERROR_CODE_SERVER = 1;
		private ValidationActivity activity;

		public ValidationAsyncTask(ValidationActivity activity) {
			this.activity = activity;
		}

		@Override
		protected Integer doInBackground(Void... params) {
			publishProgress(0);
			if (!AndroidUtils.hasConnection(activity)) {
				publishProgress(100);
				return ERROR_CODE_NETWORK;
			}
			publishProgress(48);
			SystemClock.sleep(2000);
			publishProgress(100);
			return ERROR_CODE_EMPTY;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			activity.onProgress(values[0]);
		}

		@Override
		protected void onPostExecute(final Integer errorCode) {
			switch (errorCode) {
				case ERROR_CODE_NETWORK:
					activity.setError("Пожалуйста проверьте подключение к сети");
					break;

				default:
					activity.setError("OK");
					loader.scaleUp(new LoaderView.Callback() {
						@Override
						public void execute() {
							startActivity(PlacesListActivity.class, AnimationUtils.DURATION_LONG);
						}
					});
					break;
			}
		}
	}

	private static final int REQUEST_ENABLE_BT = 100;
	private static final long BLE_SCAN_PERIOD = 2000;

	private final ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
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

	@InjectView(R.id.loader)
	protected LoaderView loader;

	@InjectView(R.id.txt_error)
	protected TextView txtError;

 	private BroadcastReceiver gattConnectedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(intent.getAction())) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						final BluetoothGattService service = mBluetoothLeService.getService(RBLBluetoothAttributes
								                                                               .UUID_BLE_REDBEAR_PASSWORD_SERVICE);
						final BluetoothGattCharacteristic characteristic = service.getCharacteristic(RBLBluetoothAttributes
								                                                                        .UUID_BLE_REDBEAR_PASSWORD);
						characteristic.setValue(RBLBluetoothAttributes.RBL_PASSKEY);
						mBluetoothLeService.writeCharacteristic(characteristic);
					}
				});
			}
			if (intent.getAction() == BluetoothLeService.ACTION_GATT_CONNECTED) {
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

	private boolean mBound;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mBtEnabled = false;
	private Handler mHandler = new Handler();
	private int loaderSize;

	private List<Beacon> beacons = new ArrayList<Beacon>();

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					final BeaconParser parser = new BeaconParser();
					parser.setBeaconLayout(RBLBluetoothAttributes.REDBEAR_BEACON_LAYOUT);
					final Beacon beacon = parser.fromScanData(scanRecord, rssi, device);
					if(beacon != null && beacon.getId1() != null ) {
						Identifier id1 = beacon.getId1();
						final String beaconId = id1.toString().toLowerCase();
						if (RBLBluetoothAttributes.BEACON_ID.equals(beaconId)) {
							beacons.add(beacon);
							// mBluetoothLeService.connect(beacon.getBluetoothAddress());
						}
					}
				}
			});
		}
	};

	private BluetoothLeService mBluetoothLeService;

	@Override
	public void initUi() {
		loaderSize = getResources().getDimensionPixelSize(R.dimen.loader_size);
	}

	private void setupBluetooth() {
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
	}

	private void checkBluetoothEnabled() {
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			mBtEnabled = true;
		}
	}

	private void scanBleDevices(boolean enable) {
		if (enable) {
			beacons.clear();
			mHandler.postDelayed(new Runnable() {
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
		if (requestCode == REQUEST_ENABLE_BT) {
			mBtEnabled = resultCode == RESULT_OK ? true : false;
			if (mBtEnabled) {
				scanBleDevices(true);
			}
		}
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_validation;
	}

	public void onProgress(int progress) {
		if (progress == 0 || progress >= 100) {
			loader.showProgress(false);
		} else {
			loader.showProgress(true);
		}
		loader.updateProgress(progress);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(gattConnectedReceiver);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (mBluetoothLeService == null) {
			bindService(new Intent(this, BluetoothLeService.class), mServiceConnection, BIND_AUTO_CREATE);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mBound) {
			unbindService(mServiceConnection);
			mBound = false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		animateStart();
		setupBluetooth();
		checkBluetoothEnabled();
		IntentFilter filter = new IntentFilter(BluetoothLeService.ACTION_GATT_CONNECTED);
		filter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		registerReceiver(gattConnectedReceiver, filter);
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
