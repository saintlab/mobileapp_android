package com.omnom.android.utils.preferences;

import android.content.Context;

/**
 * Created by Ch3D on 04.09.2014.
 */
public interface PreferenceProvider {

	void setCardId(Context activity, String externalCardId);

	String getCardId(Context context);

	public boolean setAuthToken(Context context, String value);

	public String getAuthToken(Context context);

	public String getCardData(Context context);

	public boolean setCardData(Context context, String cardId);

	/**
	 * Sets user profile
	 *
	 * @param context context
	 * @param userProfile user profile json
	 */
	boolean setUserProfileJson(Context context, String userProfile);

	/**
	 * Returns user profile
	 *
	 * @param context context
	 * @return user profile json
	 */
	String getUserProfileJson(Context context);

	/**
	 * Sets config
	 *
	 * @param context context
	 * @param config config json
	 */
	boolean setConfigJson(Context context, String config);

	/**
	 * Returns config
	 *
	 * @param context context
	 * @return config json
	 */
	String getConfigJson(Context context);

}
