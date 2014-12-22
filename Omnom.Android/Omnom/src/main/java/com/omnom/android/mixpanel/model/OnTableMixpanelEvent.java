package com.omnom.android.mixpanel.model;

/**
 * Created by xCh3Dx on 20.12.2014.
 */
public class OnTableMixpanelEvent extends DecodeMixpanelEvent {

	public static final String EVENT_NAME = "restaurant_enter";

	public static OnTableMixpanelEvent createEventBluetooth(String restaurantName, String tableId) {
		return new OnTableMixpanelEvent(restaurantName, tableId, METHODE_BLUETOOTH);
	}

	public static OnTableMixpanelEvent createEventQr(String restaurantName, String tableId) {
		return new OnTableMixpanelEvent(restaurantName, tableId, METHODE_QR);
	}

	private OnTableMixpanelEvent(String restaurantName, String tableId, String methode) {
		super(tableId, restaurantName, methode);
	}

	@Override
	public String getName() {
		return EVENT_NAME;
	}
}
