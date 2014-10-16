package com.omnom.android.restaurateur.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 10.10.2014.
 */
public class WaiterCallResponse {

	public static final String STATUS_SUCCESS = "success";

	@Expose
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isSuccess() {
		return STATUS_SUCCESS.equals(status);
	}
}
