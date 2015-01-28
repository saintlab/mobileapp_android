package com.saintlab.android.linker.activity.bind;

/**
 * Created by Ch3D on 28.08.2014.
 */
public class GattEvent {
	private String mAction;

	public GattEvent(final String action){
		mAction = action;
	}

	public String getAction() {
		return mAction;
	}
}
