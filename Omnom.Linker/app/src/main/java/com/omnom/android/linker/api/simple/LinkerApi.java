package com.omnom.android.linker.api.simple;

import com.omnom.android.linker.model.restaurant.Restaurant;

import java.util.List;

/**
 * Created by Ch3D on 11.08.2014.
 */
public interface LinkerApi {
	public String authenticate(String username, String password);

	public String remindPassword(String username);

	public int checkBeacon(String restaurantId, String beaconUuid, String majorId, String minorId);

	public int bindBeacon(String restaurantId, String beaconUuid, String majorId, String minorId);

	public int checkQrCode(String restaurantId, String qrData);

	public int bindQrCode(String restaurantId, String qrData);

	public Restaurant getRestaurant(String restaurantId);

	public List<Restaurant> getRestaurants();
}
