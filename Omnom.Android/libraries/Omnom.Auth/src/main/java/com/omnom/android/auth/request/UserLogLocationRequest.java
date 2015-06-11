package com.omnom.android.auth.request;

import com.google.gson.annotations.Expose;

public class UserLogLocationRequest {
	@Expose
	private final String token;

	@Expose
	private double longitude;

	@Expose
	private double latitude;

	public UserLogLocationRequest(final double longitude, final double latitude, final String token) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(final double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(final double latitude) {
		this.latitude = latitude;
	}
}
