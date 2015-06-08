package com.omnom.android.auth.request;

import com.google.gson.annotations.Expose;

public class UserUpdateRequest {
	@Expose
	private String token;

	@Expose
	private String name;

	@Expose
	private String email;

	@Expose
	private String birthDate;

	@Expose
	private String avatar;

	public UserUpdateRequest(final String token, final String name, final String email, final String birth, final String avaUrl) {
		this.token = token;
		this.name = name;
		this.email = email;
		this.birthDate = birth;
		this.avatar = avaUrl;
	}
}
