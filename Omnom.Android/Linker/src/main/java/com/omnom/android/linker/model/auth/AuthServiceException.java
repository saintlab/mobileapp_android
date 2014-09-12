package com.omnom.android.linker.model.auth;

/**
 * Created by Ch3D on 04.09.2014.
 */
public class AuthServiceException extends RuntimeException {
	private int mCode;
	private final Error mError;

	public Error getError() {
		return mError;
	}

	public AuthServiceException(int code, Error error) {
		mCode = code;
		mError = error;
	}

	public int getCode() {
		return mCode;
	}
}
