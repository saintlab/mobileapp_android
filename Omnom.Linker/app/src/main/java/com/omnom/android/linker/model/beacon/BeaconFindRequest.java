package com.omnom.android.linker.model.beacon;

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

	public BeaconFindRequest(Beacon beacon) {
		this(beacon.getIdValue(0).toUpperCase(), Integer.valueOf(beacon.getIdValue(1)), Integer.valueOf(beacon.getIdValue(2)));
	}

	public BeaconFindRequest(String uuid, int major, int minor) {
		this.uuid = uuid;
		this.major = major;
		this.minor = minor;
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
}
