package com.omnom.android.linker.model.auth;

import com.google.gson.annotations.Expose;
import com.omnom.android.linker.model.User;

/**
 * Created by Ch3D on 28.08.2014.
 */
public class UserProfile extends AuthResponseBase {
	@Expose
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	// TODO:
	public String getImageUrl() {
		return "http://i.imgur.com/DvpvklR.png";
	}
}
