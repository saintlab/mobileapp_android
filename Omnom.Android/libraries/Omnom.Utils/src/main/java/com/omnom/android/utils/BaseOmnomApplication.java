package com.omnom.android.utils;

import android.app.Application;

import com.omnom.android.utils.activity.OmnomActivity;
import com.omnom.android.utils.preferences.PreferenceProvider;

/**
 * Created by Ch3D on 28.09.2014.
 */
public abstract class BaseOmnomApplication extends Application {
	public static BaseOmnomApplication get(OmnomActivity activity) {
		return (BaseOmnomApplication) activity.getActivity().getApplication();
	}

	public abstract void inject(Object o);

	public abstract PreferenceProvider getPreferences();
}
