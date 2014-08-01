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
import com.omnom.android.linker.service.BluetoothGattAttributes;
import com.omnom.android.linker.service.BluetoothLeService;
import com.omnom.android.linker.utils.AndroidUtils;
import com.omnom.android.linker.utils.AnimationBuilder;
import com.omnom.android.linker.widget.LoaderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.InjectView;
import hugo.weaving.DebugLog;

public class ValidationActivity extends BaseActivity {

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
//					loader.scaleUp(new LoaderView.Callback() {
//						@Override
//						public void execute() {
//							startActivity(PlacesListActivity.class, AnimationUtils.DURATION_LONG);
//						}
//					});
					break;
			}
		}
	}

	private static final String TAG = ValidationActivity.class.getSimpleName();
	private static final String LIST_UUID = "UUID";
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
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};
	@InjectView(R.id.loader)
	protected LoaderView loader;
	@InjectView(R.id.txt_error)
	protected TextView txtError;
	private String mDeviceAddress = null;
	private String LIST_NAME = "NAME";
	private boolean mConnected;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mBtEnabled = false;
	private boolean mScanning = false;
	private Handler mHandler = new Handler();
	private int loaderSize;

	BroadcastReceiver gattConnectedReceiver  = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction() == BluetoothLeService.ACTION_GATT_CONNECTED) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mBluetoothLeService.getDiscoverGattService();
					}
				});
			}
		}
	};

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		@DebugLog
		public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
			if (device.getAddress().equals("20:CD:39:A4:A1:70")) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mDeviceAddress = device.getAddress();
						mBluetoothLeService.connect(device.getAddress());
					}
				});
			}
		}
	};

	private BluetoothLeService mBluetoothLeService;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics;

	@Override
	public void initUi() {
		startBleService();
		registerReceiver(gattConnectedReceiver, new IntentFilter(BluetoothLeService.ACTION_GATT_CONNECTED));
		loaderSize = getResources().getDimensionPixelSize(R.dimen.loader_size);
	}

	private void displayGattServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null) {
			return;
		}
		String uuid = null;
		String unknownServiceString = getResources().
				getString(R.string.unknown_service);
		String unknownCharaString = getResources().
				getString(R.string.unknown_characteristic);
		ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
		ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
		mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			uuid = gattService.getUuid().toString();
			currentServiceData.put(LIST_NAME, BluetoothGattAttributes.
					lookup(uuid, unknownServiceString));
			currentServiceData.put(LIST_UUID, uuid);
			gattServiceData.add(currentServiceData);

			ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
			List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
			ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();
			// Loops through available Characteristics.
			for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				charas.add(gattCharacteristic);
				HashMap<String, String> currentCharaData = new HashMap<String, String>();
				uuid = gattCharacteristic.getUuid().toString();
				currentCharaData.put(LIST_NAME, BluetoothGattAttributes.lookup(uuid, unknownCharaString));
				currentCharaData.put(LIST_UUID, uuid);
				gattCharacteristicGroupData.add(currentCharaData);
			}
			mGattCharacteristics.add(charas);
			gattCharacteristicData.add(gattCharacteristicGroupData);
		}
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

	private void startBleService() {
		bindService(new Intent(this, BluetoothLeService.class), mServiceConnection, BIND_AUTO_CREATE);
	}

	@DebugLog
	private void scanBleDevices(boolean enable) {
		if (enable) {
			startBleService();
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
			}, BLE_SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			mBtEnabled = resultCode == RESULT_OK ? true : false;
			if (mBtEnabled) {
				// scanBleDevices(true);
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
	@DebugLog
	protected void onStop() {
		super.onStop();
		unregisterReceiver(gattConnectedReceiver);
		unbindService(mServiceConnection);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// animateStart();
		setupBluetooth();
		checkBluetoothEnabled();
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
