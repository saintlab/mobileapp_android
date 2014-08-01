package com.omnom.android.linker.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.UUID;

import hugo.weaving.DebugLog;

public class BluetoothLeService extends Service {
	public class LocalBinder extends Binder {
		public BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	private boolean mConnected = false;
	private boolean mSuccess = false;

	BluetoothGattCallback callback = new BluetoothGattCallback() {
		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicWrite(gatt, characteristic, status);
		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			super.onDescriptorRead(gatt, descriptor, status);
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			super.onDescriptorWrite(gatt, descriptor, status);
		}

		@Override
		public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
			super.onReliableWriteCompleted(gatt, status);
		}

		@Override
		@DebugLog
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			String intentAction;
			if(newState == BluetoothProfile.STATE_CONNECTED) {
				mConnected = true;
			}
			if(status == BluetoothGatt.GATT_SUCCESS) {
				mSuccess = true;
			}

			if (newState == BluetoothProfile.STATE_CONNECTED) {
				intentAction = ACTION_GATT_CONNECTED;
				broadcastUpdate(intentAction);
				Log.i(TAG, "Connected to GATT server.");
				// Attempts to discover services after successful connection.
				// Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = ACTION_GATT_DISCONNECTED;
				Log.i(TAG, "Disconnected from GATT server.");
				broadcastUpdate(intentAction);
			}
		}

		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_GATT_RSSI, rssi);
			} else {
				Log.w(TAG, "onReadRemoteRssi received: " + status);
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
		}
	};


	public final static String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_GATT_RSSI = "ACTION_GATT_RSSI";
	public final static String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA = "EXTRA_DATA";

	public static UUID UUID_BLE_REDBEAR_BEACON_SERVICE = UUID.fromString(BluetoothGattAttributes.BLE_REDBEAR_BEACON_SERVICE);
	public static UUID UUID_BLE_REDBEAR_BEACON_UUID = UUID.fromString(BluetoothGattAttributes.BLE_REDBEAR_BEACON_UUID);
	public static UUID UUID_BLE_REDBEAR_BEACON_MAJOR_ID = UUID.fromString(BluetoothGattAttributes.BLE_REDBEAR_BEACON_MAJOR_ID);
	public static UUID UUID_BLE_REDBEAR_BEACON_MINOR_ID = UUID.fromString(BluetoothGattAttributes.BLE_REDBEAR_BEACON_MINOR_ID);

	private static final String TAG = BluetoothLeService.class.getSimpleName();

	private final IBinder mBinder = new LocalBinder();
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action, int rssi) {
		final Intent intent = new Intent(action);
		intent.putExtra(EXTRA_DATA, String.valueOf(rssi));
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);

		// This is special handling for the Heart Rate Measurement profile. Data
		// parsing is
		// carried out as per profile specifications:
		// http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic
		// .heart_rate_measurement.xml
//		if (UUID_BLE_SHIELD_RX.equals(characteristic.getUuid())) {
//			final byte[] rx = characteristic.getValue();
//			intent.putExtra(EXTRA_DATA, rx);
//		}

		sendBroadcast(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// After using a given device, you should make sure that
		// BluetoothGatt.close() is called
		// such that resources are cleaned up properly. In this particular
		// example, close() is
		// invoked when the UI is disconnected from the Service.
		close();
		return super.onUnbind(intent);
	}

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 *
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 *
	 * @param address The device address of the destination device.
	 * @return Return true if the connection is initiated successfully. The
	 * connection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
			Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		// Previously connected device. Try to reconnect.
		if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
			Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
			if (mBluetoothGatt.connect()) {
				return true;
			} else {
				return false;
			}
		}

		final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.
		mBluetoothGatt = device.connectGatt(this, false, callback);
		Log.d(TAG, "Trying to create a new connection.");
		mBluetoothDeviceAddress = address;

		return true;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	@DebugLog
	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
	}

	@Override
	@DebugLog
	public void onDestroy() {
		super.onDestroy();
		disconnect();
		close();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	@DebugLog
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic,
	 *int)}
	 * callback.
	 *
	 * @param characteristic The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}

		mBluetoothGatt.readCharacteristic(characteristic);
	}

	public void readRssi() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}

		mBluetoothGatt.readRemoteRssi();
	}

	public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}

		mBluetoothGatt.writeCharacteristic(characteristic);
	}

//	/**
//	 * Enables or disables notification on a give characteristic.
//	 *
//	 * @param characteristic Characteristic to act on.
//	 * @param enabled        If true, enable notification. False otherwise.
//	 */
//	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
//		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
//			Log.w(TAG, "BluetoothAdapter not initialized");
//			return;
//		}
//		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
//
//		if (UUID_BLE_SHIELD_RX.equals(characteristic.getUuid())) {
//			BluetoothGattDescriptor descriptor =
//					characteristic.getDescriptor(UUID.fromString(BluetoothGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
//			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//			mBluetoothGatt.writeDescriptor(descriptor);
//		}
//	}

	@DebugLog
	public boolean getDiscoverGattService() {
		if (mBluetoothGatt == null) {
			return false;
		}
		getServices();
		return mBluetoothGatt.discoverServices();
	}

	@DebugLog
	public BluetoothGattService getSupportedGattService() {
		if (mBluetoothGatt == null) {
			return null;
		}
		getServices();
		return mBluetoothGatt.getService(UUID_BLE_REDBEAR_BEACON_SERVICE);
	}

	@DebugLog
	private List<BluetoothGattService> getServices() {
		return mBluetoothGatt.getServices();
	}
}
