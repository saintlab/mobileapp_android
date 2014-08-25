package com.omnom.android.linker.api.observable;

import com.omnom.android.linker.model.ibeacon.BeaconDataResponse;
import com.omnom.android.linker.model.Restaurant;
import com.omnom.android.linker.model.RestaurantsResult;

import altbeacon.beacon.Beacon;
import rx.Observable;

/**
 * Created by Ch3D on 11.08.2014.
 */
public interface LinkerObeservableApi {
	public void setAuthToken(String token);

	public Observable<BeaconDataResponse> buildBeacon(String restaurantId, int tableNumber, String uuid);

	public Observable<BeaconDataResponse> bindBeacon(String restaurantId, int tableNumber, Beacon beacon);

	public Observable<String> authenticate(String username, String password);

	public Observable<String> remindPassword(String username);

	public Observable<Integer> checkBeacon(String restaurantId, Beacon beacon);


	public Observable<Integer> checkQrCode(String restaurantId, String qrData);

	public Observable<Integer> bindQrCode(String restaurantId, int tableNumber, String qrData);

	public Observable<Restaurant> getRestaurant(String restaurantId);

	public Observable<RestaurantsResult> getRestaurants();

	public Observable<Integer> commitBeacon(String restaurantId, Beacon beacon);
}
