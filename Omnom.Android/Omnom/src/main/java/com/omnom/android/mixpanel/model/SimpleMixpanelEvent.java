package com.omnom.android.mixpanel.model;

/**
 * Created by Ch3D on 22.12.2014.
 */
public class SimpleMixpanelEvent implements MixpanelEvent {
	private String mEventName;

	public SimpleMixpanelEvent(final String eventName) {
		mEventName = eventName;
	}

	@Override
	public String getName() {
		return mEventName;
	}
}
