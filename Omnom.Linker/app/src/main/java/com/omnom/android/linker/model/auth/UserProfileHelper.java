package com.omnom.android.linker.model.auth;

import org.apache.http.HttpStatus;

/**
 * Created by Ch3D on 10.09.2014.
 */
public class UserProfileHelper {
	public static int getErrorStatus(UserProfile userProfile) {
		if(userProfile != null && userProfile.getError() != null) {
			return userProfile.getError().getCode();
		}
		return 0;
	}

	public static boolean hasAuthError(UserProfile userProfile) {
		int errorStatus = getErrorStatus(userProfile);
		return userProfile.isError() && (errorStatus == HttpStatus.SC_FORBIDDEN || errorStatus == HttpStatus.SC_UNAUTHORIZED);
	}
}
