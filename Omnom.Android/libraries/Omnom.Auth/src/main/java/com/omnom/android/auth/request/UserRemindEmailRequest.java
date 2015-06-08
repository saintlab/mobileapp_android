package com.omnom.android.auth.request;

import com.google.gson.annotations.Expose;

public class UserRemindEmailRequest {
	@Expose
	private String email;

	public UserRemindEmailRequest(final String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}
}
