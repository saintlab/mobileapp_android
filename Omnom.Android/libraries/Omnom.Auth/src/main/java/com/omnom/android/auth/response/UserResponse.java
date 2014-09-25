package com.omnom.android.auth.response;

import com.google.gson.annotations.Expose;
import com.omnom.android.auth.UserData;

/**
 * Created by Ch3D on 25.09.2014.
 */
public class UserResponse extends AuthResponse {
	@Expose
	private UserData user;

	public UserData getUser() {
		return user;
	}

	public void setUser(UserData user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "UserResponse{" +
				"user=" + user +
				'}';
	}
}
