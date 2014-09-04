package com.omnom.android.linker.preferences;

import android.content.Context;

import com.omnom.android.linker.utils.StringUtils;

/**
 * Created by Ch3D on 04.09.2014.
 */
public class PreferenceHelper implements PreferenceProvider {
	private static final String USER_PREFERENCES = "com.omnom.android.linker.user";
	private static final String AUTH_TOKEN = "com.omnom.android.linker.user.auth_token";

	@Override
	public boolean setAuthToken(Context context, final String value) {
		return context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)
		              .edit()
		              .putString(AUTH_TOKEN, value)
		              .commit();
	}

	@Override
	public String getAuthToken(Context context) {
		return context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)
		              .getString(AUTH_TOKEN, StringUtils.EMPTY_STRING);
	}
}
