package com.omnom.android.linker.model.beacon;

import com.google.gson.annotations.Expose;

import altbeacon.beacon.Beacon;

/**
 * Created by Ch3D on 25.08.2014.
 */
public class BeaconBindRequest {
	@Expose
	public String restaurantId;

	@Expose
	public int table_num;

	@Expose
	public String uuid;

	@Expose
	public int major;

	@Expose
	public int minor;

	public BeaconBindRequest(String restaurantId, int table_num, Beacon beacon) {
		this(restaurantId, table_num, beacon.getIdValue(0), Integer.valueOf(beacon.getIdValue(1)), Integer.valueOf(beacon.getIdValue(2)));
	}

	public BeaconBindRequest(String restaurantId, int table_num, BeaconDataResponse beaconData) {
		this(restaurantId, table_num, beaconData.getUuid(), beaconData.getMajor(), beaconData.getMinor());
	}

	public BeaconBindRequest(String restaurantId, int table_num, String uuid, int major, int minor) {
		this.restaurantId = restaurantId;
		this.table_num = table_num;
		this.uuid = uuid;
		this.major = major;
		this.minor = minor;
	}
}
