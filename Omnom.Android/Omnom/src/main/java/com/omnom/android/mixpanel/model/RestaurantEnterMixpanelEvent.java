package com.omnom.android.mixpanel.model;

/**
 * Created by xCh3Dx on 20.12.2014.
 */
public class RestaurantEnterMixpanelEvent extends DecodeMixpanelEvent {

	public static final String EVENT_NAME = "restaurant_enter";

	public static RestaurantEnterMixpanelEvent createEventBluetooth(String restaurantName, String tableId) {
		return new RestaurantEnterMixpanelEvent(restaurantName, tableId, METHODE_BLUETOOTH);
	}

	public static RestaurantEnterMixpanelEvent createEventQr(String restaurantName, String tableId) {
		return new RestaurantEnterMixpanelEvent(restaurantName, tableId, METHODE_QR);
	}

	private RestaurantEnterMixpanelEvent(String restaurantName, String tableId, String methode) {
		super(tableId, restaurantName, methode);
	}

	@Override
	public String getName() {
		return EVENT_NAME;
	}
}
