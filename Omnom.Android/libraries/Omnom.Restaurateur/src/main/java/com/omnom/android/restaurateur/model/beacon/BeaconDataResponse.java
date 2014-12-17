package com.omnom.android.restaurateur.model.beacon;

import com.google.gson.annotations.Expose;
import com.omnom.android.restaurateur.model.ResponseBase;
import com.omnom.android.utils.utils.StringUtils;

/**
 * Created by Ch3D on 25.08.2014.
 */
public class BeaconDataResponse extends ResponseBase {

	public static BeaconDataResponse NULL = new BeaconDataResponse(StringUtils.EMPTY_STRING, -1, -1);

	@Expose
	private String uuid;

	@Expose
	private int major;

	@Expose
	private int minor;

	public BeaconDataResponse(String uuid, int major, int minor) {
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

	@Override
	public String toString() {
		return uuid + ":" + major + ":" + minor;
	}

	public void setUuid(String s) {
		uuid = s;
	}
}
