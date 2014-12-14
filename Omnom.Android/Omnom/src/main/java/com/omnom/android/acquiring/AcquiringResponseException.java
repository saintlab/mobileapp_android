package com.omnom.android.acquiring;

import com.omnom.android.acquiring.mailru.response.AcquiringResponseError;

/**
 * Created by mvpotter on 12/10/2014.
 */
public class AcquiringResponseException extends RuntimeException {

	private final AcquiringResponseError mError;

	public AcquiringResponseException(AcquiringResponseError error) {
		mError = error;
	}

	public AcquiringResponseError getError() {
		return mError;
	}

}
