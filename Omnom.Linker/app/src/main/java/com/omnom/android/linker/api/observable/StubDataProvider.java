package com.omnom.android.linker.api.observable;

import com.omnom.android.linker.activity.BeaconAlreadyBoundException;
import com.omnom.android.linker.api.ServerResponse;
import com.omnom.android.linker.model.Restaurant;
import com.omnom.android.linker.model.RestaurantsFactory;
import com.omnom.android.linker.model.RestaurantsResult;

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

	private boolean authError        = false;
	private boolean checkBeaconError = false;

	@Override
	@DebugLog
	public Observable<String> authenticate(String username, String password) {
		return new Observable<String>(new Observable.OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> subscriber) {
				authError = false;
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
	@DebugLog
	public Observable<String> remindPassword(String username) {
		return new Observable<String>(new Observable.OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> subscriber) {
				subscriber.onNext("OK");
				subscriber.onCompleted();
			}
		}) {};
	}

	@Override
	@DebugLog
	public Observable<Integer> checkBeacon(String restaurantId, Beacon beacon) {
		return new Observable<Integer>(new Observable.OnSubscribe<Integer>() {
			@Override
			public void call(Subscriber<? super Integer> subscriber) {
				if(checkBeaconError) {
					subscriber.onError(new BeaconAlreadyBoundException());
				} else {
					subscriber.onNext(0);
					subscriber.onCompleted();
				}
			}
		}) {};
	}

	@Override
	@DebugLog
	public Observable<Integer> bindBeacon(String restaurantId, Beacon beacon) {
		return new Observable<Integer>(new Observable.OnSubscribe<Integer>() {
			@Override
			public void call(Subscriber<? super Integer> subscriber) {
				subscriber.onNext(0);
				subscriber.onCompleted();
			}
		}) {};
	}

	@Override
	@DebugLog
	public Observable<Integer> checkQrCode(String restaurantId, String qrData) {
		return new Observable<Integer>(new Observable.OnSubscribe<Integer>() {
			@Override
			public void call(Subscriber<? super Integer> subscriber) {
				subscriber.onNext(0);
				subscriber.onCompleted();
			}
		}) {};
	}

	@Override
	@DebugLog
	public Observable<Integer> bindQrCode(String restaurantId, String qrData) {
		return new Observable<Integer>(new Observable.OnSubscribe<Integer>() {
			@Override
			public void call(Subscriber<? super Integer> subscriber) {
				subscriber.onNext(0);
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
	public Observable<RestaurantsResult> getRestaurants() {
		return new Observable<RestaurantsResult>(new Observable.OnSubscribe<RestaurantsResult>() {
			@Override
			public void call(Subscriber<? super RestaurantsResult> subscriber) {
				final boolean fast = false;
				RestaurantsResult result = new RestaurantsResult();
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
	@DebugLog
	public Observable<Integer> commitBeacon(String restaurantId, Beacon beacon) {
		return new Observable<Integer>(new Observable.OnSubscribe<Integer>() {
			@Override
			public void call(Subscriber<? super Integer> subscriber) {
				subscriber.onNext(0);
				subscriber.onCompleted();
			}
		}) {}.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	@DebugLog
	public void setAuthToken(String token) {
		// Do nothing
	}

	@Override
	public Observable<ServerResponse> build(String restaurantId, int tableNumber, String uuid) {
		return new Observable<ServerResponse>(new Observable.OnSubscribe<ServerResponse>() {
			@Override
			public void call(Subscriber<? super ServerResponse> subscriber) {
				subscriber.onNext(new ServerResponse());
				subscriber.onCompleted();
			}
		}) {}.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}
}
