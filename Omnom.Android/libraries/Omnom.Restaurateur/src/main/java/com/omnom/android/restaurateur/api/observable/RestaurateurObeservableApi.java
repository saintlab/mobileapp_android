package com.omnom.android.restaurateur.api.observable;

import com.omnom.android.restaurateur.model.beacon.BeaconDataResponse;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantsResponse;
import com.omnom.android.restaurateur.model.table.TableDataResponse;

import altbeacon.beacon.Beacon;
import rx.Observable;

/**
 * Created by Ch3D on 11.08.2014.
 */
public interface RestaurateurObeservableApi {
	public Observable<Restaurant> getRestaurant(String restaurantId);

	public Observable<RestaurantsResponse> getRestaurants();

	public Observable<BeaconDataResponse> buildBeacon(String restaurantId, int tableNumber, String uuid);

	public Observable<BeaconDataResponse> bindBeacon(String restaurantId, int tableNumber, Beacon beacon, Beacon oldBeacon);

	public Observable<BeaconDataResponse> bindBeacon(String restaurantId, int tableNumber, BeaconDataResponse beaconData, Beacon oldBeacon);

	public Observable<TableDataResponse> findBeacon(Beacon beacon);

	public Observable<Restaurant> setRssiThreshold(String restaurantId, int rssi);

	public Observable<TableDataResponse> checkQrCode(String qrData);

	public Observable<TableDataResponse> bindQrCode(String restaurantId, int tableNumber, String qrData);
}