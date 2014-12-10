package com.omnom.android.preferences;

import android.content.Context;

import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.restaurateur.model.config.Config;
import com.omnom.android.utils.preferences.PreferenceProvider;

/**
 * Created by mvpotter on 12/10/2014.
 */
public interface JsonPreferenceProvider extends PreferenceProvider {

	/**
	 * Sets user profile
	 *
	 * @param context context
	 * @param userProfile user profile
	 */
	boolean setUserProfile(Context context, UserProfile userProfile);

	/**
	 * Returns user profile
	 *
	 * @param context context
	 * @return user profile
	 */
	UserProfile getUserProfile(Context context);

	/**
	 * Sets config
	 *
	 * @param context context
	 * @param config config
	 */
	boolean setConfig(Context context, Config config);

	/**
	 * Returns config
	 *
	 * @param context context
	 * @return config
	 */
	Config getConfig(Context context);

}
