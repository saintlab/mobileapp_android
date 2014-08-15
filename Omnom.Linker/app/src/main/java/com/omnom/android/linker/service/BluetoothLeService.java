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
import java.util.concurrent.LinkedBlockingQueue;

import hugo.weaving.DebugLog;

public class BluetoothLeService extends Service {
	public final static  String                ACTION_GATT_CONNECTED           = "ACTION_GATT_CONNECTED";
	public final static  String                ACTION_GATT_DISCONNECTED        = "ACTION_GATT_DISCONNECTED";
	public final static  String                ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
	public final static  String                ACTION_DATA_AVAILABLE           = "ACTION_DATA_AVAILABLE";
	private              BluetoothGattCallback callback                        = new BluetoothGattCallback() {
		@Override
		@DebugLog
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicWrite(gatt, characteristic, status);
			if(!mWriteQueue.isEmpty()) {
				checkWritingQueue();
			} else {
				disconnect();
				close();
			}
		}

		@Override
		@DebugLog
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			String intentAction;
			if(newState == BluetoothProfile.STATE_CONNECTED) {
				intentAction = ACTION_GATT_CONNECTED;
				broadcastUpdate(intentAction);
			} else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = ACTION_GATT_DISCONNECTED;
				broadcastUpdate(intentAction);
			}
		}

		@Override
		@DebugLog
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if(status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
			}
		}

		@Override
		@DebugLog
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			if(status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			}
		}
	};
	public final static  String                ACTION_PASSWORD_WRITTEN         = "ACTION_PASSWORD_WRITTEN";
	public final static  String                EXTRA_DATA                      = "EXTRA_DATA";
	private static final String                TAG                             = BluetoothLeService.class.getSimpleName();

	public class LocalBinder extends Binder {
		public BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	private final LinkedBlockingQueue<BluetoothGattCharacteristic> mWriteQueue = new LinkedBlockingQueue<BluetoothGattCharacteristic>();
	private final IBinder                                          mBinder     = new LocalBinder();
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String           mBluetoothDeviceAddress;
	private BluetoothGatt    mBluetoothGatt;

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
		sendBroadcast(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		close();
		return super.onUnbind(intent);
	}

	public boolean initialize() {
		if(mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if(mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if(mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	@DebugLog
	public boolean connect(final String address) {
		if(mBluetoothAdapter == null || address == null) {
			Log.d(TAG, "BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		// Previously connected device. Try to reconnect.
		if(mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
			Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
			if(mBluetoothGatt.connect()) {
				return true;
			} else {
				return false;
			}
		}

		final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		if(device == null) {
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
	public void disconnect() {
		if(mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		disconnect();
		close();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if(mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	public boolean readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if(mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return false;
		}
		return mBluetoothGatt.readCharacteristic(characteristic);
	}

	@DebugLog
	public void queueCharacteristic(BluetoothGattCharacteristic characteristic) {
		if(mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mWriteQueue.add(characteristic);
	}

	@DebugLog
	public void checkWritingQueue() {
		if(mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}

		if(mWriteQueue.isEmpty()) {
			return;
		}

		BluetoothGattCharacteristic characteristic = mWriteQueue.poll();
		if(characteristic != null) {
			mBluetoothGatt.writeCharacteristic(characteristic);
		}
	}

	public boolean getDiscoverGattService() {
		if(mBluetoothGatt == null) {
			return false;
		}
		return mBluetoothGatt.discoverServices();
	}

	public BluetoothGattService getService(UUID uuid) {
		return mBluetoothGatt.getService(uuid);
	}

	public void readDescriptors(UUID uuid, UUID cuuid) {
		BluetoothGattService service = mBluetoothGatt.getService(uuid);
		BluetoothGattCharacteristic characteristic = service.getCharacteristic(cuuid);
		List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
		for(BluetoothGattDescriptor d : descriptors) {
			mBluetoothGatt.readDescriptor(d);
		}
	}

	public void readCharacteristic(UUID uuid, UUID cuuid) {
		BluetoothGattService service = mBluetoothGatt.getService(uuid);
		BluetoothGattCharacteristic characteristic = service.getCharacteristic(cuuid);
		mBluetoothGatt.readCharacteristic(characteristic);
	}
}
