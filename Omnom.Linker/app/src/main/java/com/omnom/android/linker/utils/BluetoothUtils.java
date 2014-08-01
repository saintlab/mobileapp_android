package com.omnom.android.linker.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by Ch3D on 01.08.2014.
 */
public class BluetoothUtils {

	public static boolean hasBleSupport(Context context) {
		return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
	}
}