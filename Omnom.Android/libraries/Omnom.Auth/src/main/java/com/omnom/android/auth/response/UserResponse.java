package com.omnom.android.auth.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.omnom.android.auth.UserData;

/**
 * Created by Ch3D on 25.09.2014.
 */
public class UserResponse extends AuthResponse {

	public static final UserResponse NULL = new UserResponse(UserData.NULL, STATUS_SUCCESS);

	@Expose
	private UserData user;

	@Expose
	@SerializedName("time")
	private long serverTime;

	private transient long responseTime;

	public UserResponse(final UserData user, final String status) {
		this.user = user;
		setStatus(status);
	}

	public UserData getUser() {
		return user;
	}

	public void setUser(UserData user) {
		this.user = user;
	}

	public long getServerTime() {
		return serverTime;
	}

	public void setServerTime(final Long serverTime) {
		this.serverTime = serverTime;
	}

	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(final Long responseTime) {
		this.responseTime = responseTime;
	}

	@Override
	public String toString() {
		return "UserResponse{" +
				"user=" + user +
				", serverTime=" + serverTime +
				'}';
	}
}
