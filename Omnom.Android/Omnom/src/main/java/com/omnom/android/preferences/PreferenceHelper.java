package com.omnom.android.preferences;

import android.content.Context;

import com.omnom.android.utils.preferences.PreferenceProvider;
import com.omnom.android.utils.utils.StringUtils;

/**
 * Created by Ch3D on 28.09.2014.
 */
public class PreferenceHelper implements PreferenceProvider {
	private static final String USER_PREFERENCES = "com.omnom.android.linker.user";
	private static final String CARD_ID = "com.omnom.android.card_id";
	private static final String AUTH_TOKEN = "com.omnom.android.linker.user.auth_token";
	private static final String USER_ID = "com.omnom.android.linker.user.id";

	@Override
	public boolean setAuthToken(Context context, String value) {
		return context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)
		              .edit()
		              .putString(AUTH_TOKEN, value)
		              .commit();
	}

	@Override
	public String getAuthToken(Context context) {
		final String string = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)
		                             .getString(AUTH_TOKEN, StringUtils.EMPTY_STRING);
		return string;
	}

	@Override
	public String getCardData(Context context) {
		final String string = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)
		                             .getString(CARD_ID, StringUtils.EMPTY_STRING);
		return string;

	}

	@Override
	public boolean setCardData(Context context, String cardId) {
		return context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)
		              .edit()
		              .putString(CARD_ID, cardId)
		              .commit();
	}
}
