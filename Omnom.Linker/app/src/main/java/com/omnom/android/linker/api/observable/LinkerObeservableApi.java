package com.omnom.android.linker.api.observable;

import com.omnom.android.linker.model.UserProfile;
import com.omnom.android.linker.model.beacon.BeaconDataResponse;
import com.omnom.android.linker.model.restaurant.Restaurant;
import com.omnom.android.linker.model.restaurant.RestaurantsResponse;
import com.omnom.android.linker.model.table.TableDataResponse;

import altbeacon.beacon.Beacon;
import rx.Observable;

/**
 * Created by Ch3D on 11.08.2014.
 */
public interface LinkerObeservableApi {

	public void setAuthToken(String token);

	public Observable<UserProfile> getUserProfile(String authToken);

	public Observable<String> authenticate(String username, String password);

	public Observable<String> remindPassword(String username);

	public Observable<Restaurant> getRestaurant(String restaurantId);

	public Observable<RestaurantsResponse> getRestaurants();

	public Observable<BeaconDataResponse> buildBeacon(String restaurantId, int tableNumber, String uuid);

	public Observable<BeaconDataResponse> bindBeacon(String restaurantId, int tableNumber, Beacon beacon);

	public Observable<BeaconDataResponse> bindBeacon(String restaurantId, int tableNumber, BeaconDataResponse beaconData);

	public Observable<TableDataResponse> findBeacon(Beacon beacon);

	public Observable<TableDataResponse> checkQrCode(String qrData);

	public Observable<TableDataResponse> bindQrCode(String restaurantId, int tableNumber, String qrData);
}
