package com.omnom.android.restaurateur.model;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 26.08.2014.
 */
public class ResponseBase {

	/**
	 * Value from X-Request-ID header.
	 */
	@Expose
	private String requestId;

	/**
	 * Restaurateur error
	 */
	@Expose
	private String error;

	/**
	 * Omnom errors
	 */
	@Expose
	private OmnomErrors errors;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public OmnomErrors getErrors() {
		return errors;
	}

	public void setErrors(OmnomErrors errors) {
		this.errors = errors;
	}

	public boolean hasErrors() {
		return !TextUtils.isEmpty(error) || errors != null;
	}

	public boolean hasAuthError() {
		return errors != null && errors.getAuthentication() != null;
	}

	public boolean hasCommonError() {
		return errors != null && errors.getCommon() != null;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
