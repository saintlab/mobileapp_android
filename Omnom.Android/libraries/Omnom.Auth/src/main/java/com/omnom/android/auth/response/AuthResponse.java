package com.omnom.android.auth.response;

import com.google.gson.annotations.Expose;
import com.omnom.android.auth.AuthError;

/**
 * Created by Ch3D on 25.09.2014.
 */
public class AuthResponse {
	public static final String STATUS_ERROR = "error";

	public static final String STATUS_SUCCESS = "success";

	public static AuthResponse create(String status, AuthError error) {
		AuthResponse response = new AuthResponse();
		response.setStatus(status);
		response.setError(error);
		return response;
	}

	@Expose
	private String requestId;

	@Expose
	private String status;

	@Expose
	private String token;

	@Expose
	private AuthError error;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public AuthError getError() {
		return error;
	}

	public void setError(AuthError error) {
		this.error = error;
	}

	public boolean hasError() {
		return STATUS_ERROR.equals(status) || error != null;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
