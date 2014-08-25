package com.omnom.android.linker.activity.bind;

import com.omnom.android.linker.service.RBLBluetoothAttributes;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.Identifier;

/**
 * Created by Ch3D on 25.08.2014.
 */
public class BeaconFilter {
	public static final int RSSI_MIN_VALUE = -68;

	public boolean check(Beacon beacon) {
		if(beacon.getId1() == null) {
			return false;
		}
		final Identifier id1 = beacon.getId1();
		final String beaconId = id1.toString().toLowerCase();
		return RBLBluetoothAttributes.BEACON_ID.equals(beaconId) && beacon.getRssi() >= RSSI_MIN_VALUE;
	}
}
