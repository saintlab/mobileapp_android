package com.omnom.android.linker.model.beacon;

import com.google.gson.annotations.Expose;
import com.omnom.android.linker.model.ResponseBase;

/**
 * Created by Ch3D on 25.08.2014.
 */
public class BeaconDataResponse extends ResponseBase {
	@Expose
	private String uuid;

	public String getUuid() {
		return uuid;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	@Expose
	private int major;

	@Expose
	private int minor;

	@Override
	public String toString() {
		return uuid + ":" + major + ":" + minor;
	}
}
