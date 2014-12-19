package com.omnom.android.restaurateur.model.beacon;

import com.google.gson.annotations.Expose;

import altbeacon.beacon.Beacon;

/**
 * Created by Ch3D on 17.12.2014.
 */
public class BeaconQrBindRequest extends BeaconBindRequest {

	@Expose
	public String qr;

	public BeaconQrBindRequest(final String restaurantId, final int table_num, String qrData,
	                           final Beacon beacon, final Beacon old_beacon) {
		super(restaurantId, table_num, beacon, old_beacon);
		this.qr = qrData;
	}

	public BeaconQrBindRequest(final String restaurantId, final int table_num, String qrData,
	                           final BeaconDataResponse beaconData, final Beacon old_beacon) {
		super(restaurantId, table_num, beaconData, old_beacon);
		this.qr = qrData;
	}
}
