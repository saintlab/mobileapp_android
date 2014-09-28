package com.omnom.util;

import android.app.Application;

import com.omnom.util.preferences.PreferenceProvider;
import com.omnom.util.activity.OmnomActivity;

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
