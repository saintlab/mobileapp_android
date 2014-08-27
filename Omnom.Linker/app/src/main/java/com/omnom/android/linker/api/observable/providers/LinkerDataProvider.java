package com.omnom.android.linker.api.observable.providers;

import android.text.TextUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omnom.android.linker.BuildConfig;
import com.omnom.android.linker.api.LinkerDataService;
import com.omnom.android.linker.api.Protocol;
import com.omnom.android.linker.api.observable.LinkerObeservableApi;
import com.omnom.android.linker.model.beacon.BeaconBindRequest;
import com.omnom.android.linker.model.beacon.BeaconBuildRequest;
import com.omnom.android.linker.model.beacon.BeaconDataResponse;
import com.omnom.android.linker.model.beacon.BeaconFindRequest;
import com.omnom.android.linker.model.qrcode.QRCodeBindRequest;
import com.omnom.android.linker.model.restaurant.Restaurant;
import com.omnom.android.linker.model.restaurant.RestaurantsResponse;
import com.omnom.android.linker.model.table.TableDataResponse;

import org.apache.http.auth.AuthenticationException;

import java.util.concurrent.TimeUnit;

import altbeacon.beacon.Beacon;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Ch3D on 11.08.2014.
 */
public class LinkerDataProvider implements LinkerObeservableApi, RequestInterceptor {
	private final LinkerDataService mDataService;
	private final RestAdapter mRestAdapter;
	private String mAuthToken;

	public LinkerDataProvider(final String endPoint) {
		final RestAdapter.LogLevel logLevel = BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;
		final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
		final GsonConverter converter = new GsonConverter(gson);
		mRestAdapter = new RestAdapter.Builder().setRequestInterceptor(this).setEndpoint(endPoint).setLogLevel(logLevel)
		                                        .setConverter(converter).build();
		mDataService = mRestAdapter.create(LinkerDataService.class);
		// TODO: Remove
		setAuthToken("stub_auth_token");
	}

	@Override
	public Observable<String> authenticate(String username, String password) {
		// TODO:
		// return mDataService.authenticate(username, password).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
		return new Observable<String>(new Observable.OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> subscriber) {
				boolean authError = false;
				if(authError) {
					subscriber.onError(new AuthenticationException());
				} else {
					subscriber.onNext("OK");
					subscriber.onCompleted();
				}
			}
		}) {}.delaySubscription(2000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<String> remindPassword(String username) {
		// TODO:
		// return mDataService.remindPassword(username).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
		return new Observable<String>(new Observable.OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> subscriber) {
				boolean authError = false;
				if(authError) {
					subscriber.onError(new AuthenticationException());
				} else {
					subscriber.onNext("OK");
					subscriber.onCompleted();
				}
			}
		}) {}.delaySubscription(2000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<TableDataResponse> findBeacon(Beacon beacon) {
		return mDataService.findBeacon(new BeaconFindRequest(beacon)).subscribeOn(
				Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<BeaconDataResponse> bindBeacon(String restaurantId, int tableNumber, Beacon beacon) {
		final BeaconBindRequest request = new BeaconBindRequest(restaurantId, tableNumber, beacon);
		return mDataService.bindBeacon(request).subscribeOn(
				Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<BeaconDataResponse> bindBeacon(String restaurantId, int tableNumber, BeaconDataResponse beaconData) {
		final BeaconBindRequest request = new BeaconBindRequest(restaurantId, tableNumber, beaconData);
		return mDataService.bindBeacon(request).subscribeOn(
				Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<TableDataResponse> checkQrCode(String qrData) {
		return mDataService.checkQrCode(qrData).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<TableDataResponse> bindQrCode(String restaurantId, int tableNumber, String qrData) {
		return mDataService.bindQrCode(new QRCodeBindRequest(restaurantId, tableNumber, qrData)).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Restaurant> getRestaurant(String restaurantId) {
		return mDataService.getRestaurant(restaurantId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<RestaurantsResponse> getRestaurants() {
		return mDataService.getRestaurants().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public void setAuthToken(final String token) {
		mAuthToken = token;
	}

	@Override
	public Observable<BeaconDataResponse> buildBeacon(String restaurantId, int tableNumber, String uuid) {
		return mDataService.buildBeacon(new BeaconBuildRequest(uuid, String.valueOf(tableNumber), restaurantId))
		                   .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public void intercept(RequestFacade request) {
		if(!TextUtils.isEmpty(mAuthToken)) {
			request.addHeader(Protocol.HEADER_AUTH_TOKEN, mAuthToken);
		}
	}
}
