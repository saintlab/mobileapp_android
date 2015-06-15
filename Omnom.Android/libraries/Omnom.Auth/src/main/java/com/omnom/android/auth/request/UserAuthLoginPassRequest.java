package com.omnom.android.auth.request;

import com.google.gson.annotations.Expose;

public class UserAuthLoginPassRequest {
	@Expose
	private String login;

	@Expose
	private String password;

	public UserAuthLoginPassRequest(final String login, final String password) {
		this.login = login;
		this.password = password;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(final String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

}
