package com.omnom.android.linker.beacon;

import android.content.Context;
import android.util.Log;

import com.omnom.android.linker.LinkerApplication;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;

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

	private Context mContext;

	public BeaconRssiProviderSimple(final Context context) {
		mContext = context;
		LinkerApplication.get(context).inject(this);
	}

	@Override
	public void updateRssiThreshold(Restaurant restaurant) {
		api.getRestaurant(restaurant.getId()).flatMap(new Func1<Restaurant, Observable<Restaurant>>() {
			@Override
			public Observable<Restaurant> call(Restaurant restaurant) {
				if(restaurant.getRssiThreshold() != DEFAULT_RSSI_THRESHOLD) {
					return api.setRssiThreshold(restaurant.getId(), DEFAULT_RSSI_THRESHOLD);
				} else {
					return Observable.empty();
				}
			}
		}).subscribe(new Action1<Restaurant>() {
			@Override
			public void call(final Restaurant updatedRestaurant) {
				if(updatedRestaurant == null || updatedRestaurant.getRssiThreshold() != DEFAULT_RSSI_THRESHOLD) {
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
