package com.omnom.android.linker.activity.base;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by Ch3D on 31.07.2014.
 */
public interface OmnomActivity {
	void startActivity(Class<?> cls);

	void startActivity(Class<?> cls, boolean finish);

	void startActivity(Intent intent, boolean finish);

	public Activity getActivity();
	public int getLayoutResource();
	public void initUi();
}
