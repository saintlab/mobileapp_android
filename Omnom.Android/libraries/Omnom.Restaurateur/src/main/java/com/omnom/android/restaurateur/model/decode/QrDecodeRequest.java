package com.omnom.android.restaurateur.model.decode;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 23.12.2014.
 */
public class QrDecodeRequest extends DecodeRequest {
	@Expose
	private String qr;

	public QrDecodeRequest(final String qr) {
		this.qr = qr;
	}

	public String getQr() {
		return qr;
	}

	public void setQr(final String qr) {
		this.qr = qr;
	}
}
