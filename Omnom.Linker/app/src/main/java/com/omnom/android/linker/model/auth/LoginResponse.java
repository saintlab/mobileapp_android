package com.omnom.android.linker.model.auth;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 03.09.2014.
 */
public class LoginResponse extends AuthResponseBase {
	@Expose
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
