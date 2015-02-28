package com.omnom.android.notifier.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 28.02.2015.
 */
public class RegisterRequest {

	@Expose
	private String pushToken;

	public RegisterRequest(final String pushToken) {
		this.pushToken = pushToken;
	}

	public String getPushToken() {
		return pushToken;
	}

	public void setPushToken(final String pushToken) {
		this.pushToken = pushToken;
	}
}
