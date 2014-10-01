package com.omnom.util;

import com.omnom.util.preferences.Preferences;

/**
 * Created by Ch3D on 14.08.2014.
 */
public interface Extras extends Preferences {
	public static final String EXTRA_BEACON = "com.omnom.android.linker.beacon";
	public static final String EXTRA_RESTAURANT = "com.omnom.android.linker.restaurant";
	public static final String EXTRA_RESTAURANTS = "com.omnom.android.linker.restaurants";
	public static final String EXTRA_USERNAME = "com.omnom.android.linker.username";
	public static final String EXTRA_PASSWORD = "com.omnom.android.linker.password";
	public static final String EXTRA_ERROR_CODE = "com.omnom.android.linker.error.code";
	public static final String EXTRA_SELECTED_RESTAURANT = "com.omnom.android.linker.selected_restaurant";
	public static final String EXTRA_SHOW_BACK = "com.omnom.android.linker.bind.show_back";
	public static final String EXTRA_LOADER_ANIMATION = "com.omnom.android.linker.loader.animation";
	public static final String EXTRA_ANIMATE = "com.omnom.android.linker.activity.animate";
	public static final String EXTRA_PHONE = "com.omnom.android.user.phone";
	public static final String EXTRA_CONFIRM_TYPE = "com.omnom.android.user.phone.confirm_type";

	public static final String EXTRA_CHARACTERISTIC_UUID = "ble.characteristic.uuid";
	public static final String EXTRA_CHARACTERISTIC_VALUE = "ble.characteristic.value";

	public static final int EXTRA_ERROR_WRONG_PASSWORD = 1;
	public static final int EXTRA_ERROR_WRONG_USERNAME = 2;
	public static final int EXTRA_ERROR_AUTHTOKEN_EXPIRED = 4;
	public static final int EXTRA_ERROR_LOGOUT = 8;

	public static final int EXTRA_LOADER_ANIMATION_SCALE_UP = 0;
	public static final int EXTRA_LOADER_ANIMATION_SCALE_DOWN = 1;
}
