package com.omnom.android.mixpanel.model;

import android.support.annotation.Nullable;

import com.omnom.android.auth.UserData;

/**
 * Created by Ch3D on 22.12.2014.
 */
public class AppLaunchMixpanelEvent extends AbstractBaseMixpanelEvent {

	public static final String EVENT_TITLE = "application_launch";

	public AppLaunchMixpanelEvent(@Nullable final UserData userData) {
		super(userData);
	}

	@Override
	public String getName() {
		return EVENT_TITLE;
	}
}
