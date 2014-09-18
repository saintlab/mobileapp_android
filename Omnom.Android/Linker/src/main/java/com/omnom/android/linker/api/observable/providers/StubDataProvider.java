package com.omnom.android.linker.api.observable.providers;

import com.omnom.android.linker.api.observable.LinkerObeservableApi;
import com.omnom.android.linker.model.auth.UserProfile;
import com.omnom.android.linker.model.auth.AuthResponseBase;
import com.omnom.android.linker.model.auth.Error;
import com.omnom.android.linker.model.auth.LoginResponse;
import com.omnom.android.linker.model.beacon.BeaconDataResponse;
import com.omnom.android.linker.model.restaurant.Restaurant;
import com.omnom.android.linker.model.restaurant.RestaurantsFactory;
import com.omnom.android.linker.model.restaurant.RestaurantsResponse;
import com.omnom.android.linker.model.table.TableDataResponse;
import com.omnom.android.linker.utils.StringUtils;

import org.apache.http.auth.AuthenticationException;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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

	private boolean authError = false;
	private boolean checkBeaconError = false;

	@Override
	public Observable<UserProfile> getUserProfile(String authToken) {
		return new Observable<UserProfile>(new Observable.OnSubscribe<UserProfile>() {
			@Override
			public void call(Subscriber<? super UserProfile> subscriber) {
				authError = false;
				if(authError) {
					subscriber.onError(new AuthenticationException());
				} else {
					subscriber.onNext(new UserProfile());
					subscriber.onCompleted();
				}
			}
		}) {}.delaySubscription(2000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	@DebugLog
	public Observable<LoginResponse> authenticate(String username, String password) {
		return new Observable<LoginResponse>(new Observable.OnSubscribe<LoginResponse>() {
			@Override
			public void call(Subscriber<? super LoginResponse> subscriber) {
				authError = false;
				if(authError) {
					subscriber.onError(new AuthenticationException());
				} else {
					subscriber.onNext(new LoginResponse());
					subscriber.onCompleted();
				}
			}
		}) {}.delaySubscription(2000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	@DebugLog
	public Observable<AuthResponseBase> remindPassword(String username) {
		return new Observable<AuthResponseBase>(new Observable.OnSubscribe<AuthResponseBase>() {
			@Override
			public void call(Subscriber<? super AuthResponseBase> subscriber) {
				subscriber.onNext(AuthResponseBase.create(StringUtils.EMPTY_STRING, new Error(-1, "StubError!")));
				subscriber.onCompleted();
			}
		}) {};
	}

	@Override
	public Observable<AuthResponseBase> logout(String token) {
		return new Observable<AuthResponseBase>(new Observable.OnSubscribe<AuthResponseBase>() {
			@Override
			public void call(Subscriber<? super AuthResponseBase> subscriber) {
				subscriber.onNext(AuthResponseBase.create(StringUtils.EMPTY_STRING, new Error(-1, "StubError!")));
				subscriber.onCompleted();
			}
		}) {};
	}

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
