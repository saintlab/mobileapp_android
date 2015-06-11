package com.omnom.android.auth.request;

import com.google.gson.annotations.Expose;

public class UserRecoverPhoneRequest {
	@Expose
	private String phone;

	public UserRecoverPhoneRequest(final String phone) {
		this.phone = phone;
	}

	public String getPhone() {
		return phone;
	}
}
