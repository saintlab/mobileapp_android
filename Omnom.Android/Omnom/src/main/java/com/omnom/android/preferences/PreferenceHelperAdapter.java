package com.omnom.android.preferences;

import android.content.Context;

import com.google.gson.Gson;
import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.restaurateur.model.config.Config;

/**
 * Created by mvpotter on 12/10/2014.
 */
public class PreferenceHelperAdapter extends PreferenceHelper implements JsonPreferenceProvider {

	private final Gson gson;

	public PreferenceHelperAdapter() {
		gson = new Gson();
	}

	@Override
	public boolean setUserProfile(Context context, UserProfile userProfile) {
		return setUserProfileJson(context, gson.toJson(userProfile));
	}

	@Override
	public UserProfile getUserProfile(Context context) {
		final String userProfileJson = getUserProfileJson(context);
		if (userProfileJson == null) {
			return null;
		}
		return gson.fromJson(userProfileJson, UserProfile.class);
	}

	@Override
	public boolean setConfig(Context context, Config config) {
		return setConfigJson(context, gson.toJson(config));
	}

	@Override
	public Config getConfig(Context context) {
		final String configJson = getConfigJson(context);
		if (configJson == null) {
			return null;
		}
		return gson.fromJson(configJson, Config.class);
	}
}
