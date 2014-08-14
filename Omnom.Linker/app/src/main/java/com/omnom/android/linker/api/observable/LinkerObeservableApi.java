package com.omnom.android.linker.api.observable;

import com.omnom.android.linker.model.Restaurant;
import com.omnom.android.linker.model.RestaurantsResult;

import altbeacon.beacon.Beacon;
import rx.Observable;

/**
 * Created by Ch3D on 11.08.2014.
 */
public interface LinkerObeservableApi {
	public Observable<String> authenticate(String username, String password);

	public Observable<String> remindPassword(String username);

	public Observable<Integer> checkBeacon(String restaurantId, Beacon beacon);

	public Observable<Integer> bindBeacon(String restaurantId, Beacon beacon);

	public Observable<Integer> checkQrCode(String restaurantId, String qrData);

	public Observable<Integer> bindQrCode(String restaurantId, String qrData);

	public Observable<Restaurant> getRestaurant(String restaurantId);

	public Observable<RestaurantsResult> getRestaurants();

	void setAuthToken(String token);
}
