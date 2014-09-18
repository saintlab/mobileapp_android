package com.omnom.android.linker.model.restaurant;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 18.09.2014.
 */
public class RssiThresholdRequest {
	@Expose
	private int rssiThreshold;

	public RssiThresholdRequest(int rssiThreshold) {
		this.rssiThreshold = rssiThreshold;
	}

	public int getRssiThreshold() {
		return rssiThreshold;
	}

	public void setRssiThreshold(int rssiThreshold) {
		this.rssiThreshold = rssiThreshold;
	}
}
