package com.omnom.android.utils.utils;

import java.net.ConnectException;
import java.net.UnknownHostException;

/**
 * Created by mvpotter on 2/7/2015.
 */
public final class ErrorUtils {

	private ErrorUtils() {
		throw new UnsupportedOperationException("Unable to create an instance of static class");
	}

	public static boolean isConnectionError(final Throwable throwable) {
		if (throwable == null) {
			return false;
		}
		return isConnectionException(throwable) || isConnectionException(throwable.getCause());
	}

	private static boolean isConnectionException(final Throwable throwable) {
		return throwable instanceof ConnectException || throwable instanceof UnknownHostException;
	}

}
