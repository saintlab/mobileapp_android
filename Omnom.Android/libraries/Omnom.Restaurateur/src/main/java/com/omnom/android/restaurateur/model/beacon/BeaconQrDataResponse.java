package com.omnom.android.restaurateur.model.beacon;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 17.12.2014.
 */
public class BeaconQrDataResponse extends BeaconDataResponse {

	@Expose
	private String qr;

	public BeaconQrDataResponse(final String uuid, final int major, final int minor, final String qrData) {
		super(uuid, major, minor);
		this.qr = qrData;
	}

	public String getQr() {
		return qr;
	}

	public void setQr(final String qr) {
		this.qr = qr;
	}
}
