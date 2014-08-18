package com.omnom.android.linker.activity.base;

import android.content.Context;

import com.omnom.android.linker.activity.ConnectionExecption;
import com.omnom.android.linker.activity.LocationException;
import com.omnom.android.linker.utils.AndroidUtils;
import com.omnom.android.linker.utils.BluetoothUtils;

import java.util.concurrent.TimeUnit;

import altbeacon.beacon.BleNotAvailableException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Ch3D on 14.08.2014.
 */
public class ValidationObservable {
	public enum Error {
		OK, NO_CONNECTION, BLUETOOTH_DISABLED, LOCATION_DISABLED
	}

	public static final int TIMEOUT = 20;

	public static rx.Subscription validate(final Context context, Action1<Boolean> onNext) {
		return ValidationObservable.validate(context).all(new Func1<Error, Boolean>() {
			@Override
			public Boolean call(ValidationObservable.Error o) {
				return o == ValidationObservable.Error.OK;
			}
		}).onErrorReturn(new Func1<Throwable, Boolean>() {
			@Override
			public Boolean call(Throwable throwable) {
				return false;
			}
		}).delaySubscription(1000, TimeUnit.MILLISECONDS).subscribe(onNext);
	}

	public static Observable<Object> empty(final Context context) {
		return Observable.empty().timeout(2, TimeUnit.MILLISECONDS);
	}

	public static Observable<? extends ValidationObservable.Error> hasConnection(final Context context) {
		return Observable.create(new Observable.OnSubscribe<Error>() {
			@Override
			public void call(Subscriber<? super Error> subscriber) {
				if(AndroidUtils.hasConnection(context)) {
					subscriber.onNext(Error.OK);
				} else {
					subscriber.onError(new ConnectionExecption());
					return;
				}
				subscriber.onCompleted();
			}
		}).timeout(TIMEOUT, TimeUnit.MILLISECONDS).onErrorReturn(new Func1<Throwable, Error>() {
			@Override
			public Error call(Throwable throwable) {
				return Error.NO_CONNECTION;
			}
		});
	}

	public static Observable<? extends ValidationObservable.Error> isLocationEnabled(final Context context) {
		return Observable.create(new Observable.OnSubscribe<Error>() {
			@Override
			public void call(Subscriber<? super Error> subscriber) {
				if(AndroidUtils.isLocationEnabled(context)) {
					subscriber.onNext(Error.OK);
				} else {
					subscriber.onError(new LocationException());
					return;
				}
				subscriber.onCompleted();
			}
		}).timeout(TIMEOUT, TimeUnit.MILLISECONDS);
	}

	public static Observable<? extends ValidationObservable.Error> isBluetoothEnabled(final Context context) {
		return Observable.create(new Observable.OnSubscribe<Error>() {
			@Override
			public void call(Subscriber<? super Error> subscriber) {
				if(BluetoothUtils.isBluetoothEnabled(context)) {
					subscriber.onNext(Error.OK);
				} else {
					subscriber.onError(new BleNotAvailableException("Bluetooth disabled"));
					return;
				}
				subscriber.onCompleted();
			}
		}).timeout(TIMEOUT, TimeUnit.MILLISECONDS);
	}

	public static Observable<? extends ValidationObservable.Error> validate(final Context context) {
		return Observable.concat(hasConnection(context), isLocationEnabled(context),
				isBluetoothEnabled(context))
				.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).delaySubscription(400, TimeUnit.MILLISECONDS);
	}
}
