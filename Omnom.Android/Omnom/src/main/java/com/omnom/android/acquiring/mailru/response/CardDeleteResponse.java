package com.omnom.android.acquiring.mailru.response;

import com.google.gson.annotations.Expose;

/**
 * Created by mvpotter on 12/10/2014.
 */
public class CardDeleteResponse {

	public static final String STATUS_SUCCESS = "OK";

	@Expose
	private String status;

	@Expose
	private AcquiringResponseError error;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isSuccess() {
		return STATUS_SUCCESS.equals(status);
	}

	public AcquiringResponseError getError() {
		return error;
	}

	public void setError(AcquiringResponseError error) {
		this.error = error;
	}

}
