package com.omnom.android.restaurateur.model.beacon;

import altbeacon.beacon.Beacon;

/**
 * Created by Ch3D on 27.08.2014.
 */
public class BeaconFactory {
	public static Beacon create(BeaconDataResponse beaconData) {
		return new Beacon.Builder()
				.setId1(beaconData.getUuid())
				.setId2(Integer.toString(beaconData.getMajor()))
				.setId3(Integer.toString(beaconData.getMinor())).build();
	}
}
