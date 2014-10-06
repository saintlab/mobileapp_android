package com.omnom.android.restaurateur.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 26.08.2014.
 */
public class ResponseBase {
	@Expose
	private String error;

	@Expose
	private OmnomErrors errors;

	public OmnomErrors getErrors() {
		return errors;
	}

	public void setErrors(OmnomErrors errors) {
		this.errors = errors;
	}

	public boolean hasAuthError() {
		return errors != null;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
