package com.omnom.android.linker.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 26.08.2014.
 */
public class ResponseBase {
	@Expose
	private String error;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
