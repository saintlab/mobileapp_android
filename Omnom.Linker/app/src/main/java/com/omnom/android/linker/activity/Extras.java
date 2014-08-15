package com.omnom.android.linker.activity;

import com.omnom.android.linker.activity.base.Preferences;

/**
 * Created by Ch3D on 14.08.2014.
 */
public interface Extras extends Preferences {
	public static final String EXTRA_RESTAURANT          = "com.omnom.android.linker.restaurant";
	public static final String EXTRA_RESTAURANTS         = "com.omnom.android.linker.restaurants";
	public static final String EXTRA_USERNAME            = "com.omnom.android.linker.username";
	public static final String EXTRA_PASSWORD            = "com.omnom.android.linker.password";
	public static final String EXTRA_ERROR_CODE          = "com.omnom.android.linker.error.code";
	public static final String EXTRA_SELECTED_RESTAURANT = "com.omnom.android.linker.selected_restaurant";
	public static final String EXTRA_SHOW_BACK           = "com.omnom.android.linker.bind.show_back";

	public static final int EXTRA_ERROR_WRONG_PASSWORD = 0;
	public static final int EXTRA_ERROR_WRONG_USERNAME = 1;

}
