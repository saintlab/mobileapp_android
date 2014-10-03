package com.omnom.android.linker.model;

import com.google.gson.annotations.Expose;
import com.omnom.android.auth.UserData;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.auth.response.UserResponse;

/**
 * Created by Ch3D on 28.08.2014.
 */
public class UserProfile extends AuthResponse {
	@Expose
	private UserData user;

	public UserProfile(UserResponse response) {
		user = response.getUser();
	}

	public UserData getUser() {
		return user;
	}

	public void setUser(UserData user) {
		this.user = user;
	}

	// TODO:
	public String getImageUrl() {
		return null;
	}
}
