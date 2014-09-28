package com.omnom.android.linker.api.observable.providers;

import com.omnom.android.linker.api.observable.LinkerObeservableApi;
import com.omnom.android.linker.model.beacon.BeaconDataResponse;
import com.omnom.android.linker.model.restaurant.Restaurant;
import com.omnom.android.linker.model.restaurant.RestaurantsFactory;
import com.omnom.android.linker.model.restaurant.RestaurantsResponse;
import com.omnom.android.linker.model.table.TableDataResponse;

import java.util.Arrays;

import altbeacon.beacon.Beacon;
import hugo.weaving.DebugLog;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Ch3D on 11.08.2014.
 */
public class StubDataProvider implements LinkerObeservableApi {
	@Override
	public Observable<TableDataResponse> findBeacon(Beacon beacon) {
		return new Observable<TableDataResponse>(new Observable.OnSubscribe<TableDataResponse>() {
			@Override
			public void call(Subscriber<? super TableDataResponse> subscriber) {
				subscriber.onNext(TableDataResponse.NULL);
				subscriber.onCompleted();
			}
		}) {};
	}

	@Override
	public Observable<Restaurant> setRssiThreshold(String restaurantId, int rssi) {
		return new Observable<Restaurant>(new Observable.OnSubscribe<Restaurant>() {
			@Override
			public void call(Subscriber<? super Restaurant> subscriber) {
				subscriber.onNext(RestaurantsFactory.createFake("test"));
				subscriber.onCompleted();
			}
		}) {};
	}

	@Override
	@DebugLog
	public Observable<BeaconDataResponse> bindBeacon(String restaurantId, int tableNumber, Beacon beacon, Beacon oldBeacon) {
		return new Observable<BeaconDataResponse>(new Observable.OnSubscribe<BeaconDataResponse>() {
			@Override
			public void call(Subscriber<? super BeaconDataResponse> subscriber) {
				subscriber.onNext(BeaconDataResponse.NULL);
				subscriber.onCompleted();
			}
		}) {};
	}

	@Override
	public Observable<BeaconDataResponse> bindBeacon(String restaurantId, int tableNumber,
	                                                 BeaconDataResponse beaconData, Beacon oldBeacon) {
		return bindBeacon(restaurantId, tableNumber, (Beacon) null, null);
	}

	@Override
	@DebugLog
	public Observable<TableDataResponse> checkQrCode(String qrData) {
		return new Observable<TableDataResponse>(new Observable.OnSubscribe<TableDataResponse>() {
			@Override
			public void call(Subscriber<? super TableDataResponse> subscriber) {
				subscriber.onNext(TableDataResponse.NULL);
				subscriber.onCompleted();
			}
		}) {};
	}

	@Override
	@DebugLog
	public Observable<TableDataResponse> bindQrCode(String restaurantId, int tableNumber, String qrData) {
		return new Observable<TableDataResponse>(new Observable.OnSubscribe<TableDataResponse>() {
			@Override
			public void call(Subscriber<? super TableDataResponse> subscriber) {
				subscriber.onNext(TableDataResponse.NULL);
				subscriber.onCompleted();
			}
		}) {};
	}

	@Override
	@DebugLog
	public Observable<Restaurant> getRestaurant(String restaurantId) {
		return new Observable<Restaurant>(new Observable.OnSubscribe<Restaurant>() {
			@Override
			public void call(Subscriber<? super Restaurant> subscriber) {
				subscriber.onNext(RestaurantsFactory.createFake("test"));
				subscriber.onCompleted();
			}
		}) {};
	}

	@Override
	@DebugLog
	public Observable<RestaurantsResponse> getRestaurants() {
		return new Observable<RestaurantsResponse>(new Observable.OnSubscribe<RestaurantsResponse>() {
			@Override
			public void call(Subscriber<? super RestaurantsResponse> subscriber) {
				final boolean fast = false;
				RestaurantsResponse result = new RestaurantsResponse();
				if(fast) {
					result.setItems(Arrays.asList(RestaurantsFactory.createFake("fake 1")));
				} else {
					result.setItems(Arrays.asList(RestaurantsFactory.createFake("fake 1"), RestaurantsFactory.createFake("fake 2"),
					                              RestaurantsFactory.createFake("fake 3"), RestaurantsFactory.createFake("fake 4"),
					                              RestaurantsFactory.createFake("fake 5")));
				}
				result.setLimit(0);
				result.setOffset(0);
				result.setTotal(0);
				subscriber.onNext(result);
				subscriber.onCompleted();
			}
		}) {};
	}

	@Override
	public Observable<BeaconDataResponse> buildBeacon(String restaurantId, int tableNumber, String uuid) {
		return new Observable<BeaconDataResponse>(new Observable.OnSubscribe<BeaconDataResponse>() {
			@Override
			public void call(Subscriber<? super BeaconDataResponse> subscriber) {
				subscriber.onNext(BeaconDataResponse.NULL);
				subscriber.onCompleted();
			}
		}) {}.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}
}