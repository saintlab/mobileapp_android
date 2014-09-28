package com.omnom.android.preferences;

import android.content.Context;

import com.omnom.util.preferences.PreferenceProvider;
import com.omnom.util.utils.StringUtils;

/**
 * Created by Ch3D on 28.09.2014.
 */
public class PreferenceHelper implements PreferenceProvider {
	@Override
	public boolean setAuthToken(Context context, String value) {
		return false;
	}

	@Override
	public String getAuthToken(Context context) {
		return StringUtils.EMPTY_STRING;
	}
}
