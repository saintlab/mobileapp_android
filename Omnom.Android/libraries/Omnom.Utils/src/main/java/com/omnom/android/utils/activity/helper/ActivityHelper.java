package com.omnom.android.utils.activity.helper;

import com.omnom.android.utils.preferences.PreferenceProvider;

/**
 * Created by Ch3D on 17.11.2014.
 */
public interface ActivityHelper {

	PreferenceProvider getPreferences();

	void onPostCreate();

	void onStart();

	void onApplicationLaunch();

	void onResume();

	void onPause();

	void onStop();

	void onDestroy();

}
