package com.omnom.android.linker.providers;

import com.omnom.android.linker.api.LinkerApi;
import com.omnom.android.linker.model.Place;
import com.omnom.android.linker.model.PlaceFactory;

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
	public Place getRestaurantData(String restaurantId) {
		return PlaceFactory.createFake(restaurantId);
	}

	@Override
	public List<Place> getRestaurants() {
		return Arrays.asList(PlaceFactory.createFake("fake 1"), PlaceFactory.createFake("fake 2"), PlaceFactory.createFake("fake 3"),
		                     PlaceFactory.createFake("fake 4"), PlaceFactory.createFake("fake 5"));
	}
}
