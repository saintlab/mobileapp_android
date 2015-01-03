package com.omnom.android.utils.activity.helper;

import android.content.Intent;

import com.omnom.android.utils.preferences.PreferenceProvider;

/**
 * Created by Ch3D on 17.11.2014.
 */
public interface ActivityHelper {
	void start(Intent intent, int delay);

	void start(Intent intent);

	void start(Class<?> cls, int delay);

	void start(Class<?> cls, int animIn, int aninOut);

	void start(Class<?> cls, int animIn, int aninOut, boolean finish);

	void start(Intent intent, boolean finish);

	void start(Intent intent, int animIn, int aninOut);

	void start(Intent intent, int animIn, int aninOut, boolean finish);

	void start(Class<?> cls);

	void start(Class<?> cls, boolean finish);

	PreferenceProvider getPreferences();

	void onPostCreate();

	void onResume();

	void onPause();

	void startForResult(Intent intent, int animIn, int animOut, int code);
}
