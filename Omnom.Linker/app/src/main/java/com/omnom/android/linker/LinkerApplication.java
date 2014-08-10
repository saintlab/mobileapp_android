package com.omnom.android.linker;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Ch3D on 10.08.2014.
 */
public class LinkerApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		CalligraphyConfig.initDefault("fonts/Futura-OSF-Omnom-Regular.otf", R.attr.fontPath);
	}
}
