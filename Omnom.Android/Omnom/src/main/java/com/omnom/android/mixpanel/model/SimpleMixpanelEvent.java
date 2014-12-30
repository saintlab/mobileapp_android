package com.omnom.android.mixpanel.model;

import com.omnom.android.auth.UserData;

/**
 * Created by Ch3D on 22.12.2014.
 */
public class SimpleMixpanelEvent extends AbstractBaseMixpanelEvent {
	private String mEventName;

	public SimpleMixpanelEvent(UserData userData, final String eventName) {
		super(userData);
		mEventName = eventName;
	}

	@Override
	public String getName() {
		return mEventName;
	}
}
