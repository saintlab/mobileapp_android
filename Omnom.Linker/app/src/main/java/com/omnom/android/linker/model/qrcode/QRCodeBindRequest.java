package com.omnom.android.linker.model.qrcode;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 26.08.2014.
 */
public class QRCodeBindRequest {

	public QRCodeBindRequest(String restaurantId, int table_num, String qrData) {
		this.restaurantId = restaurantId;
		this.table_num = table_num;
		this.qr = qrData;
	}

	@Expose
	public String restaurantId;

	@Expose
	public int table_num;

	@Expose
	public String qr;
}
