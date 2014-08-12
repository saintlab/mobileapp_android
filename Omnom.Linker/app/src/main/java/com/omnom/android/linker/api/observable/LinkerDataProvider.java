package com.omnom.android.linker.api.observable;

import com.omnom.android.linker.BuildConfig;
import com.omnom.android.linker.model.Restaurant;
import com.omnom.android.linker.model.RestaurantsResult;

import retrofit.RestAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Ch3D on 11.08.2014.
 */
public class LinkerDataProvider implements LinkerObeservableApi {

	public static final String ENDPOINT          = "http://admin-interface.laaaab.com";
	public static final String HEADER_AUTH_TOKEN = "auth_token";

	private final LinkerDataService mDataService;
	private final RestAdapter       mRestAdapter;

	public LinkerDataProvider() {
		final RestAdapter.LogLevel logLevel = BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;
		mRestAdapter = new RestAdapter.Builder().setEndpoint(ENDPOINT).setLogLevel(logLevel).build();
		mDataService = mRestAdapter.create(LinkerDataService.class);
	}

	@Override
	public Observable<String> authenticate(String username, String password) {
		return mDataService.authenticate(username, password).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<String> remindPassword(String username) {
		return mDataService.remindPassword(username).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Integer> checkBeacon(String restaurantId, String beaconUuid, String majorId, String minorId) {
		return mDataService.checkBeacon(restaurantId, beaconUuid, majorId, minorId).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Integer> bindBeacon(String restaurantId, String beaconUuid, String majorId, String minorId) {
		return mDataService.bindBeacon(restaurantId, beaconUuid, majorId, minorId).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Integer> checkQrCode(String restaurantId, String qrData) {
		return mDataService.checkQrCode(restaurantId, qrData).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Integer> bindQrCode(String restaurantId, String qrData) {
		return mDataService.bindQrCode(restaurantId, qrData).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Restaurant> getRestaurant(String restaurantId) {
		return mDataService.getRestaurant(restaurantId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<RestaurantsResult> getRestaurants() {
		return mDataService.getRestaurants().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}
}
