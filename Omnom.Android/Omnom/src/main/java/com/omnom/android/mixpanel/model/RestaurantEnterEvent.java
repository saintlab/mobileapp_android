package com.omnom.android.mixpanel.model;

import com.google.gson.annotations.Expose;

/**
 * Created by xCh3Dx on 20.12.2014.
 */
public class RestaurantEnterEvent implements Event {

	public static final String EVENT_NAME = "restaurant_enter";

	public static RestaurantEnterEvent createEventBluetooth(String restaurantName, String tableId) {
		return new RestaurantEnterEvent(restaurantName, tableId, "bluetooth");
	}

	public static RestaurantEnterEvent createEventQr(String restaurantName, String tableId) {
		return new RestaurantEnterEvent(restaurantName, tableId, "qr");
	}

	@Expose
	private final String restaurantName;

	@Expose
	private final String tableId;

	@Expose
	private final String methode;

	private RestaurantEnterEvent(String restaurantName, String tableId, String bluetooth) {
		this.restaurantName = restaurantName;
		this.tableId = tableId;
		this.methode = bluetooth;
	}

	@Override
	public String getName() {
		return EVENT_NAME;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public String getTableId() {
		return tableId;
	}

	public String getMethode() {
		return methode;
	}
}
