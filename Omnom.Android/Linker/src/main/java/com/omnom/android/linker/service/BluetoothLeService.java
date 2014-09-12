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

import com.omnom.android.linker.LinkerApplication;
import com.omnom.android.linker.activity.bind.GattEvent;
import com.squareup.otto.Bus;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import javax.inject.Inject;

import hugo.weaving.DebugLog;

public class BluetoothLeService extends Service {
	public final static String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_FAILED = "ACTION_GATT_FAILED";
	public final static String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
	public static final String ACTION_BEACON_WRITE_FAILED = "ACTION_BEACON_WRITE_FAILED";

	private static final String TAG = BluetoothLeService.class.getSimpleName();

	private BluetoothGattCallback callback = new BluetoothGattCallback() {
		@Override
		@DebugLog
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			if(status != BluetoothGatt.GATT_SUCCESS) {
				final String msg = "onCharacteristicWrite : unable to write characteristic = " + characteristic.getUuid()
						+ " status = " + status;
				mWriteQueue.clear();
				broadcastUpdate(ACTION_BEACON_WRITE_FAILED);
				Log.d(TAG, msg);
				throw new RuntimeException(msg);
			}
			if(!processQueue()) {
				mQueueEndCallback.run();
			}
		}

		@Override
		@DebugLog
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			String intentAction;
			if(newState == BluetoothProfile.STATE_CONNECTED) {
				if(status == BluetoothGatt.GATT_SUCCESS) {
					intentAction = ACTION_GATT_CONNECTED;
					broadcastUpdate(intentAction);
				} else {
					intentAction = ACTION_GATT_FAILED;
					broadcastUpdate(intentAction);
				}
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
	};

	public class LocalBinder extends Binder {
		public BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	private final LinkedBlockingQueue<CharacteristicHolder> mWriteQueue = new LinkedBlockingQueue<CharacteristicHolder>();
	private final IBinder mBinder = new LocalBinder();
	@Inject
	protected Bus mBus;
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;
	private Runnable mQueueEndCallback;

	@Override
	public void onCreate() {
		super.onCreate();
		LinkerApplication.get(this).inject(this);
	}

	@DebugLog
	private void broadcastUpdate(final String action) {
		mBus.post(new GattEvent(action));
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
		Log.d(TAG, "Trying to validate a new connection.");
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
	@DebugLog
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
	public void queueCharacteristic(CharacteristicHolder data) {
		if(mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mWriteQueue.add(data);
	}

	@DebugLog
	public void startWritingQueue(final Runnable queueEndCallback) {
		this.mQueueEndCallback = queueEndCallback;
		if(mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		processQueue();
	}

	@DebugLog
	private boolean processQueue() {
		if(mWriteQueue.isEmpty()) {
			return false;
		}

		final CharacteristicHolder data = mWriteQueue.poll();
		if(data != null) {
			BluetoothGattService service = mBluetoothGatt.getService(data.getServiceId());
			BluetoothGattCharacteristic characteristic = service.getCharacteristic(data.getCharId());
			characteristic.setValue(data.getData());
			mBluetoothGatt.writeCharacteristic(characteristic);
			return true;
		}
		return false;
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
