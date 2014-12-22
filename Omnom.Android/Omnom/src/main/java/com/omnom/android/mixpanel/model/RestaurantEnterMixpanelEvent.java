package com.omnom.android.mixpanel.model;

import com.google.gson.annotations.Expose;
import com.omnom.android.auth.UserData;
import com.omnom.android.restaurateur.model.table.TableDataResponse;

import altbeacon.beacon.Beacon;

/**
 * Created by xCh3Dx on 20.12.2014.
 */
public class RestaurantEnterMixpanelEvent extends DecodeMixpanelEvent {

	public static final String EVENT_NAME = "restaurant_enter";

	public static RestaurantEnterMixpanelEvent createEventBluetooth(UserData userData, TableDataResponse table, Beacon beacon) {
		return new RestaurantEnterMixpanelEvent(userData, table.getRestaurantId(), table.getId(), METHODE_BLUETOOTH,
		                                        beacon.getEncodeString());
	}

	public static RestaurantEnterMixpanelEvent createEventQr(UserData userData, TableDataResponse table, String qrData) {
		return new RestaurantEnterMixpanelEvent(userData, table.getRestaurantId(), table.getId(), METHODE_QR, qrData);
	}

	@Expose
	private String beacon;

	private RestaurantEnterMixpanelEvent(UserData userData, String restaurantName, String tableId, String methode, final String id) {
		super(userData, tableId, restaurantName, methode);
		this.beacon = id;
	}

	@Override
	public String getName() {
		return EVENT_NAME;
	}

	public String getBeacon() {
		return beacon;
	}
}
