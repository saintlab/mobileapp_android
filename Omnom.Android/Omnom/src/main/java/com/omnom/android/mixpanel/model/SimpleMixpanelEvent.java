package com.omnom.android.mixpanel.model;

import com.google.gson.annotations.Expose;
import com.omnom.android.auth.UserData;

/**
 * Created by Ch3D on 22.12.2014.
 */
public class SimpleMixpanelEvent extends AbstractBaseMixpanelEvent {

	private final transient String mEventName;

	@Expose
	protected final String requestId;

	public SimpleMixpanelEvent(UserData userData, final String eventName, final String requestId) {
		super(userData);
		mEventName = eventName;
		this.requestId = requestId;
	}

	@Override
	public String getName() {
		return mEventName;
	}
}
