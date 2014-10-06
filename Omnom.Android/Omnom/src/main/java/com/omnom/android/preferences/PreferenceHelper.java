package com.omnom.android.preferences;

import android.content.Context;

import com.omnom.android.OmnomApplication;
import com.omnom.android.utils.preferences.PreferenceProvider;

/**
 * Created by Ch3D on 28.09.2014.
 */
public class PreferenceHelper implements PreferenceProvider {
	@Override
	public boolean setAuthToken(Context context, String value) {
		return OmnomApplication.get(context).getPreferences().setAuthToken(context, value);
	}

	@Override
	public String getAuthToken(Context context) {
		return OmnomApplication.get(context).getPreferences().getAuthToken(context);
	}
}
