package com.omnom.android.mixpanel.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.omnom.android.auth.UserData;

/**
 * Created by Ch3D on 22.12.2014.
 */
public abstract class BaseMixpanelEvent implements MixpanelEvent {

	@Nullable
	@Expose
	private UserData omnUser;

	public BaseMixpanelEvent(@Nullable UserData userData) {
		omnUser = userData;
	}

	@Override
	public UserData getUser() {
		return omnUser;
	}
}
