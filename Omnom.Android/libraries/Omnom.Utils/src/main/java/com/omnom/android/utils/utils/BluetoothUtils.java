package com.omnom.android.utils.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import hugo.weaving.DebugLog;

/**
 * Created by Ch3D on 01.08.2014.
 */
public class BluetoothUtils {

	public static boolean hasBleSupport(Context context) {
		return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
	}

	@DebugLog
	public static boolean isBluetoothEnabled(Context context) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
			final BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
			return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
		} else {
			return BluetoothAdapter.getDefaultAdapter() != null;
		}
	}

	public static boolean isAdapterStateOn(final BluetoothAdapter bluetoothAdapter) {
		return bluetoothAdapter != null && bluetoothAdapter.getState() != BluetoothAdapter.STATE_ON;
	}

}