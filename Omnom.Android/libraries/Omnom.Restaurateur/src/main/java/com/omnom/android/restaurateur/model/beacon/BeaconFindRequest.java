package com.omnom.android.restaurateur.model.beacon;

import com.google.gson.annotations.Expose;

import altbeacon.beacon.Beacon;

/**
 * Created by Ch3D on 26.08.2014.
 */
public class BeaconFindRequest {
	@Expose
	private String uuid;
	@Expose
	private int major;
	@Expose
	private int minor;
	@Expose
	private int battery;

	public BeaconFindRequest(Beacon beacon) {
		// FIXME: uppercase vs lowercase
		this(beacon.getIdValue(0).toUpperCase(), Integer.valueOf(beacon.getIdValue(1)),
				Integer.valueOf(beacon.getIdValue(2)), Integer.valueOf(beacon.getIdValue(3)));
	}

	public BeaconFindRequest(String uuid, int major, int minor, int battery) {
		this.uuid = uuid;
		this.major = major;
		this.minor = minor;
		this.battery = battery;
	}

	public String getUuid() {
		return uuid;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getBattery() {
		return battery;
	}
}
