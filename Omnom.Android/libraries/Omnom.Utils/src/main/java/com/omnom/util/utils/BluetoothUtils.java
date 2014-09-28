package com.omnom.util.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;

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
		final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		final BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
		return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
	}
}