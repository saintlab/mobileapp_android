package com.omnom.android.linker.model.auth;

/**
 * Created by Ch3D on 04.09.2014.
 */
public class AuthServiceException extends RuntimeException {
	private final String mStatus;
	private final Error mError;

	public String getStatus() {
		return mStatus;
	}

	public Error getError() {
		return mError;
	}

	public AuthServiceException(String status, Error error) {
		mStatus = status;
		mError = error;
	}
}
