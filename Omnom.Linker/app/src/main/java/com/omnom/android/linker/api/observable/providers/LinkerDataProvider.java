package com.omnom.android.linker.api.observable.providers;

import android.text.TextUtils;

import com.omnom.android.linker.BuildConfig;
import com.omnom.android.linker.api.ApiProtocol;
import com.omnom.android.linker.api.ServerResponse;
import com.omnom.android.linker.api.LinkerDataService;
import com.omnom.android.linker.api.observable.LinkerObeservableApi;
import com.omnom.android.linker.model.Restaurant;
import com.omnom.android.linker.model.RestaurantsResult;

import altbeacon.beacon.Beacon;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Ch3D on 11.08.2014.
 */
public class LinkerDataProvider implements LinkerObeservableApi, RequestInterceptor {

	public static final String ENDPOINT          = "http://admin-interface.laaaab.com";
	public static final String HEADER_AUTH_TOKEN = "auth_token";

	private final LinkerDataService mDataService;
	private final RestAdapter       mRestAdapter;
	private       String            mAuthToken;

	public LinkerDataProvider() {
		final RestAdapter.LogLevel logLevel = BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;
		mRestAdapter = new RestAdapter.Builder().setRequestInterceptor(this).setEndpoint(ENDPOINT).setLogLevel(logLevel).build();
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
	public Observable<Integer> checkBeacon(String restaurantId, Beacon beacon) {
		return mDataService.checkBeacon(restaurantId, beacon.getIdValue(0), beacon.getIdValue(1), beacon.getIdValue(2)).subscribeOn(
				Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Integer> bindBeacon(String restaurantId, int tableNumber, Beacon beacon) {
		return mDataService.bindBeacon(restaurantId, tableNumber, beacon.getIdValue(0), beacon.getIdValue(1),
		                               beacon.getIdValue(2)).subscribeOn(
				Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Integer> checkQrCode(String restaurantId, String qrData) {
		return mDataService.checkQrCode(restaurantId, qrData).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Integer> bindQrCode(String restaurantId, int tableNumber, String qrData) {
		return mDataService.bindQrCode(restaurantId, tableNumber, qrData).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Restaurant> getRestaurant(String restaurantId) {
		return mDataService.getRestaurant(restaurantId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<RestaurantsResult> getRestaurants() {
		return mDataService.getRestaurants().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Integer> commitBeacon(String restaurantId, Beacon beacon) {
		return mDataService.commitBeacon(restaurantId, beacon.getIdValue(0), beacon.getIdValue(1), beacon.getIdValue(2)).subscribeOn(
				Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public void setAuthToken(final String token) {
		mAuthToken = token;
	}

	@Override
	public Observable<ServerResponse> build(String restaurantId, int tableNumber, String uuid) {
		return mDataService.build(restaurantId, tableNumber, uuid);
	}

	@Override
	public void intercept(RequestFacade request) {
		if(!TextUtils.isEmpty(mAuthToken)) {
			request.addHeader(ApiProtocol.HEADER_AUTH_TOKEN, mAuthToken);
		}
	}
}
