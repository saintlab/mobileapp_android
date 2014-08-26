package com.omnom.android.linker.model.ibeacon;

import com.google.gson.annotations.Expose;
import com.omnom.android.linker.model.ResponseBase;

/**
 * Created by Ch3D on 25.08.2014.
 */
public class BeaconDataResponse extends ResponseBase {
	@Expose
	public String uuid;

	@Expose
	public int major;

	@Expose
	public int minor;

	@Override
	public String toString() {
		return uuid + ":" + major + ":" + minor;
	}
}
