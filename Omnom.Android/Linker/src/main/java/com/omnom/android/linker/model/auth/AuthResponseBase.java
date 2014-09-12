package com.omnom.android.linker.model.auth;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 04.09.2014.
 */
public class AuthResponseBase {
	public static final String STATUS_ERROR = "error";
	public static final String STATUS_SUCCESS = "success";

	public static AuthResponseBase create(String status, Error error) {
		AuthResponseBase response = new AuthResponseBase();
		response.setStatus(status);
		response.setError(error);
		return response;
	}

	@Expose
	private String status;

	@Expose
	private Error error;

	public Error getError() {
		return error;
	}

	public boolean isError() {
		return status.equals(STATUS_ERROR);
	}

	public void setError(Error error) {
		this.error = error;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
