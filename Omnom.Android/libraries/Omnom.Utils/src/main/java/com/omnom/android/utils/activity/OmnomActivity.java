package com.omnom.android.utils.activity;

import android.app.Activity;
import android.content.Intent;

import com.omnom.android.utils.preferences.PreferenceProvider;

/**
 * Created by Ch3D on 31.07.2014.
 */
public interface OmnomActivity {
	public static final int REQUEST_CODE_ENABLE_BLUETOOTH = 100;
	public static final int REQUEST_CODE_SCAN_QR = 101;

	void start(Intent intent, int animIn, int animOut, boolean finish);

	void startForResult(Intent intent, int animIn, int animOut, int code);

	void start(Class<?> cls);

	void start(Class<?> cls, boolean finish);

	void start(Intent intent, boolean finish);

	public Activity getActivity();

	public int getLayoutResource();

	public void initUi();

	public PreferenceProvider getPreferences();

}
