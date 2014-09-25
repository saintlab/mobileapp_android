package com.omnom.android.auth;

/**
 * Created by Ch3D on 04.09.2014.
 */
public class AuthServiceException extends RuntimeException {
	private int mCode;
	private final AuthError mError;

	public AuthError getError() {
		return mError;
	}

	public AuthServiceException(int code, AuthError error) {
		mCode = code;
		mError = error;
	}

	public int getCode() {
		return mCode;
	}
}
