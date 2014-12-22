package com.omnom.android.linker.preferences;

import android.content.Context;

import com.omnom.android.utils.preferences.PreferenceProvider;
import com.omnom.android.utils.utils.StringUtils;

/**
 * Created by Ch3D on 04.09.2014.
 */
public class PreferenceHelper implements PreferenceProvider {
	private static final String USER_PREFERENCES = "com.omnom.android.linker.user";

	private static final String AUTH_TOKEN = "com.omnom.android.linker.user.auth_token";

	@Override
	public void setCardId(final Context activity, final String externalCardId) {
		// do nothing
	}

	@Override
	public String getCardId(final Context context) {
		// do nothing
		return StringUtils.EMPTY_STRING;
	}

	@Override
	public boolean setAuthToken(Context context, final String value) {
		return context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE).edit().putString(AUTH_TOKEN, value).commit();
	}

	@Override
	public String getAuthToken(Context context) {
		final String string =
				context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE).getString(AUTH_TOKEN, StringUtils.EMPTY_STRING);
		return string;
	}

	@Override
	public String getCardData(Context context) {
		return null;
	}

	@Override
	public boolean setCardData(Context context, String cardId) {
		return false;
	}

	@Override
	public boolean setUserProfileJson(Context context, String userProfile) {
		return false;
	}

	@Override
	public String getUserProfileJson(Context context) {
		// do nothing
		return StringUtils.EMPTY_STRING;
	}

	@Override
	public boolean setConfigJson(Context context, String config) {
		return false;
	}

	@Override
	public String getConfigJson(Context context) {
		// do nothing
		return StringUtils.EMPTY_STRING;
	}

	@Override
	public boolean contains(Context context, String key) {
		return context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE).contains(key);
	}
}
