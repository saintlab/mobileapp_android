package com.omnom.android.mixpanel.model;

import com.omnom.android.auth.UserData;

/**
 * Created by xCh3Dx on 20.12.2014.
 */
public class OnTableMixpanelEvent extends DecodeMixpanelEvent {

	public static final String EVENT_NAME = "on_table";

	public static OnTableMixpanelEvent create(String requestId, UserData userData, String restaurantId, String tableId, String method) {
		return new OnTableMixpanelEvent(requestId, userData, restaurantId, tableId, method);
	}

	private OnTableMixpanelEvent(String requestId, UserData userData, String restaurantId, String tableId, String method) {
		super(requestId, userData, tableId, restaurantId, method);
	}

	@Override
	public String getName() {
		return EVENT_NAME;
	}
}
