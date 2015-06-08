package com.omnom.android.auth.request;

import com.google.gson.annotations.Expose;

public class UserPhoneConfirmResendRequest {
	@Expose
	private String phone;

	public UserPhoneConfirmResendRequest(final String phone) {
		this.phone = phone;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(final String phone) {
		this.phone = phone;
	}
}
