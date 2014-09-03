package com.omnom.android.linker.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 28.08.2014.
 */
public class UserProfile {
	@Expose
	private User user;
	@Expose
	private String status;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	// TODO:
	public String getImageUrl() {
		return "http://i.imgur.com/DvpvklR.png";
	}
}
