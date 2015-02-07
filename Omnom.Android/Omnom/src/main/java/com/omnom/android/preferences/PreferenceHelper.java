package com.omnom.android.preferences;

import android.content.Context;
import android.os.SystemClock;

import com.omnom.android.utils.preferences.PreferenceProvider;
import com.omnom.android.utils.utils.StringUtils;

import altbeacon.beacon.Beacon;

/**
 * Created by Ch3D on 28.09.2014.
 */
public class PreferenceHelper implements PreferenceProvider {
	public static final String USER_PREFERENCES = "com.omnom.android.prefs.user";

	public static final String BEACONS_PREFERENCES = "com.omnom.android.prefs.beacons";

	private static final String AUTH_TOKEN = "com.omnom.android.linker.user.auth_token";

	private static final String USER_ID = "com.omnom.android.linker.user.id";

	private static final String CARD_ID = "com.omnom.android.card_id";

	private static final String CARD_DATA = "com.omnom.android.card_data";

	private static final String USER_PROFILE = "com.omnom.android.user_profile";

	private static final String CONFIG = "com.omnom.android.config";

	@Override
	public void setCardId(final Context context, final String externalCardId) {
		context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE).edit().putString(CARD_ID, externalCardId).apply();
	}

	public void saveNotificationDetails(final Context context, final String restaurantId) {
		long timestamp = SystemClock.elapsedRealtime();
		context.getSharedPreferences(BEACONS_PREFERENCES, Context.MODE_PRIVATE).edit().putLong(restaurantId, timestamp)
		       .apply();
	}

	public long getNotificationTimestamp(final Context context, final String restaurantId) {
		return context.getSharedPreferences(BEACONS_PREFERENCES, Context.MODE_PRIVATE).getLong(restaurantId, -1);
	}

	public boolean hasNotificationData(final Context context, final String restaurantId) {
		return context.getSharedPreferences(BEACONS_PREFERENCES, Context.MODE_PRIVATE).contains(restaurantId);
	}

	public void saveRestaurantBeacon(final Context context, final Beacon beacon) {
		long timestamp = SystemClock.elapsedRealtime();
		context.getSharedPreferences(BEACONS_PREFERENCES, Context.MODE_PRIVATE).edit().putLong(beacon.getRestaurantData(), timestamp).apply();
	}

	public void saveBeacon(final Context context, final Beacon beacon) {
		long timestamp = SystemClock.elapsedRealtime();
		context.getSharedPreferences(BEACONS_PREFERENCES, Context.MODE_PRIVATE).edit().putLong(beacon.getBluetoothAddress(), timestamp)
				.putLong(beacon.getBluetoothAddress(), timestamp).apply();
	}

	public boolean hasBeacon(final Context context, final Beacon beacon) {
		return context.getSharedPreferences(BEACONS_PREFERENCES, Context.MODE_PRIVATE).contains(beacon.getBluetoothAddress());
	}

	public long getBeaconTimestamp(final Context context, final Beacon beacon) {
		return context.getSharedPreferences(BEACONS_PREFERENCES, Context.MODE_PRIVATE).getLong(beacon.getBluetoothAddress(), -1);
	}

	@Override
	public String getCardId(final Context context) {
		final String string =
				context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE).getString(CARD_ID, StringUtils.EMPTY_STRING);
		return string;
	}

	@Override
	public boolean setAuthToken(Context context, String value) {
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
		final String string =
				context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE).getString(CARD_DATA, StringUtils.EMPTY_STRING);
		return string;

	}

	@Override
	public boolean setCardData(Context context, String cardId) {
		return context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE).edit().putString(CARD_DATA, cardId).commit();
	}

	@Override
	public boolean setUserProfileJson(Context context, String userProfile) {
		return context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE).edit().putString(USER_PROFILE, userProfile).commit();
	}

	@Override
	public String getUserProfileJson(Context context) {
		return context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE).getString(USER_PROFILE, StringUtils.EMPTY_STRING);
	}

	@Override
	public boolean setConfigJson(Context context, String config) {
		return context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE).edit().putString(CONFIG, config).commit();
	}

	@Override
	public String getConfigJson(Context context) {
		return context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE).getString(CONFIG, StringUtils.EMPTY_STRING);
	}

	@Override
	public boolean contains(Context context, String key) {
		return context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE).contains(key)
				|| context.getSharedPreferences(BEACONS_PREFERENCES, Context.MODE_PRIVATE).contains(key);
	}
}
