package com.omnom.android.linker.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 03.09.2014.
 */
public class LoginResponse {
	@Expose
	private String token;

	@Expose
	private String status;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
