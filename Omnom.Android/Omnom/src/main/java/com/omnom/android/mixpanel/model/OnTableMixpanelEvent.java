package com.omnom.android.mixpanel.model;

import com.omnom.android.auth.UserData;

/**
 * Created by xCh3Dx on 20.12.2014.
 */
public class OnTableMixpanelEvent extends DecodeMixpanelEvent {

	public static final String EVENT_NAME = "restaurant_enter";

	public static OnTableMixpanelEvent createEventBluetooth(UserData userData, String restaurantName, String tableId) {
		return new OnTableMixpanelEvent(userData, restaurantName, tableId, METHODE_BLUETOOTH);
	}

	public static OnTableMixpanelEvent createEventQr(UserData userData, String restaurantName, String tableId) {
		return new OnTableMixpanelEvent(userData, restaurantName, tableId, METHODE_QR);
	}

	private OnTableMixpanelEvent(UserData userData, String restaurantName, String tableId, String methode) {
		super(userData, tableId, restaurantName, methode);
	}

	@Override
	public String getName() {
		return EVENT_NAME;
	}
}
