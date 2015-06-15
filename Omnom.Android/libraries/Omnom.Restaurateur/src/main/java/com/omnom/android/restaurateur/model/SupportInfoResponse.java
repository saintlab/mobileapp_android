package com.omnom.android.restaurateur.model;

import com.google.gson.annotations.Expose;

/**
 * Created by mvpotter on 2/25/2015.
 */
public class SupportInfoResponse extends ResponseBase {

	public static final SupportInfoResponse NULL = new SupportInfoResponse();

	@Expose
	private String phone;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}
