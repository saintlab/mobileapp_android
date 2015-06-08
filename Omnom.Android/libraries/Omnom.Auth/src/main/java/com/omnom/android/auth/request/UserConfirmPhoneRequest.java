package com.omnom.android.auth.request;

import com.google.gson.annotations.Expose;

public class UserConfirmPhoneRequest {
	@Expose
	private String phone;

	@Expose
	private String code;

	public UserConfirmPhoneRequest(final String phone, final String code) {
		this.phone = phone;
		this.code = code;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(final String phone) {
		this.phone = phone;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}
}
