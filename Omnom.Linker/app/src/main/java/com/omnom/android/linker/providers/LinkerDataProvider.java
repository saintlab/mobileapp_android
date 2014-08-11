package com.omnom.android.linker.providers;

import com.omnom.android.linker.api.LinkerApi;
import com.omnom.android.linker.model.Place;

import java.util.List;

/**
 * Created by Ch3D on 11.08.2014.
 */
public class LinkerDataProvider implements LinkerApi {
	@Override
	public String authenticate(String username, String password) {
		return null;
	}

	@Override
	public String remindPassword(String username) {
		return null;
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
		return null;
	}

	@Override
	public List<Place> getRestaurants() {
		return null;
	}
}
