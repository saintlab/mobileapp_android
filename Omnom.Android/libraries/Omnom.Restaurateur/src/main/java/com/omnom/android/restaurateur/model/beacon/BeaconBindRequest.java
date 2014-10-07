package com.omnom.android.restaurateur.model.beacon;

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

	@Expose
	private String oldUuid;

	@Expose
	private int oldMajor;

	@Expose
	private int oldMinor;

	public BeaconBindRequest(String restaurantId, int table_num, Beacon beacon, Beacon old_beacon) {
		this(restaurantId, table_num, beacon.getIdValue(0), Integer.valueOf(beacon.getIdValue(1)), Integer.valueOf(beacon.getIdValue(2)),
		     old_beacon.getIdValue(0), Integer.valueOf(old_beacon.getIdValue(1)), Integer.valueOf(old_beacon.getIdValue(2)));
	}

	public BeaconBindRequest(String restaurantId, int table_num, BeaconDataResponse beaconData, Beacon old_beacon) {
		this(restaurantId, table_num, beaconData.getUuid(), beaconData.getMajor(), beaconData.getMinor(),
		     old_beacon.getIdValue(0), Integer.valueOf(old_beacon.getIdValue(1)), Integer.valueOf(old_beacon.getIdValue(2)));
	}

	private BeaconBindRequest(String restaurantId, int table_num, String uuid, int major, int minor, String old_uuid, int old_major,
	                          int old_minor) {
		this.restaurantId = restaurantId;
		this.table_num = table_num;
		this.uuid = uuid;
		this.major = major;
		this.minor = minor;
		this.oldUuid = old_uuid;
		this.oldMajor = old_major;
		this.oldMinor = old_minor;
	}
}
