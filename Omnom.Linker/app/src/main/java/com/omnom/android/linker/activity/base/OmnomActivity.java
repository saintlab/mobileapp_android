package com.omnom.android.linker.activity.base;

import android.app.Activity;

/**
 * Created by Ch3D on 31.07.2014.
 */
public interface OmnomActivity {
	public Activity getActivity();
	public int getLayoutResource();
	public void initUi();
}
