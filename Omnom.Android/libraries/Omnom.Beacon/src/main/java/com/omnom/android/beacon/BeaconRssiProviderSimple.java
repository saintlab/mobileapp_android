package com.omnom.android.beacon;

import android.util.Log;

import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.utils.BaseOmnomApplication;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Ch3D on 18.09.2014.
 */
public class BeaconRssiProviderSimple implements BeaconRssiProvider {
	public static final int DEFAULT_RSSI_THRESHOLD = -70;
	private static final String TAG = BeaconRssiProviderSimple.class.getSimpleName();

	@Inject
	protected RestaurateurObeservableApi api;

	public BeaconRssiProviderSimple(final BaseOmnomApplication app) {
		app.inject(this);
	}

	@Override
	public void updateRssiThreshold(Restaurant restaurant) {
		api.getRestaurant(restaurant.id()).flatMap(new Func1<Restaurant, Observable<Restaurant>>() {
			@Override
			public Observable<Restaurant> call(Restaurant restaurant) {
				if(restaurant.rssiThreshold() != DEFAULT_RSSI_THRESHOLD) {
					return api.setRssiThreshold(restaurant.id(), DEFAULT_RSSI_THRESHOLD);
				} else {
					return Observable.empty();
				}
			}
		}).subscribe(new Action1<Restaurant>() {
			@Override
			public void call(final Restaurant updatedRestaurant) {
				if(updatedRestaurant == null || updatedRestaurant.rssiThreshold() != DEFAULT_RSSI_THRESHOLD) {
					Log.d(TAG, "Unable to change rssi threshold : " + updatedRestaurant);
				}
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				Log.e(TAG, "checkRssiThreshold : unable to change rssi threshold", throwable);
			}
		});
	}
}
