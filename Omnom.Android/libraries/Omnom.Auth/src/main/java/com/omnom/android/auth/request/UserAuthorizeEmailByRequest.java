package com.omnom.android.auth.request;

import com.google.gson.annotations.Expose;

public class UserAuthorizeEmailByRequest {
	@Expose
	private String email;

	@Expose
	private String code;

	public UserAuthorizeEmailByRequest(final String email, final String code) {
		this.email = email;
		this.code = code;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}
}
