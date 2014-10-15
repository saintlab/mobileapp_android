package com.omnom.android.utils.preferences;

import android.content.Context;

/**
 * Created by Ch3D on 04.09.2014.
 */
public interface PreferenceProvider {
	public boolean setAuthToken(Context context, String value);
	public String getAuthToken(Context context);

	public String getCardData(Context context);
	public boolean setCardData(Context context, String cardId);
}
