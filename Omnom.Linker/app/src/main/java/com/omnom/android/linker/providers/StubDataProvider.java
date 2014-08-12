package com.omnom.android.linker.providers;

import com.omnom.android.linker.api.LinkerApi;
import com.omnom.android.linker.model.Restaurant;
import com.omnom.android.linker.model.RestaurantsFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Ch3D on 11.08.2014.
 */
public class StubDataProvider implements LinkerApi {

	@Override
	public String authenticate(String username, String password) {
		return "auth_token_stub";
	}

	@Override
	public String remindPassword(String username) {
		return "OK";
	}

	@Override
	public int checkBeacon(String restaurantId, String beaconUuid, String majorId, String minorId) {
		return 0;
	}

	@Override
	public int bindBeacon(String restaurantId, String beaconUuid, String majorId, String minorId) {
		return 0;
	}

	@Override
	public int checkQrCode(String restaurantId, String qrData) {
		return 0;
	}

	@Override
	public int bindQrCode(String restaurantId, String qrData) {
		return 0;
	}

	@Override
	public Restaurant getRestaurantData(String restaurantId) {
		return RestaurantsFactory.createFake(restaurantId);
	}

	@Override
	public List<Restaurant> getRestaurants() {
		return Arrays.asList(RestaurantsFactory.createFake("fake 1"), RestaurantsFactory.createFake("fake 2"), RestaurantsFactory.createFake("fake 3"),
		                     RestaurantsFactory.createFake("fake 4"), RestaurantsFactory.createFake("fake 5"));
	}
}
