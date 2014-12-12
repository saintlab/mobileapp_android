package com.omnom.android.utils.observable;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.BluetoothUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Ch3D on 14.08.2014.
 */
public class ValidationObservable {
	public enum Error {
		OK, NO_CONNECTION, BLUETOOTH_DISABLED, LOCATION_DISABLED
	}

	public static final int TIMEOUT = 100;

	public static Observable<ValidationObservable.Error> hasConnection(final Context context) {
		return Observable.create(new Observable.OnSubscribe<Error>() {
			@Override
			public void call(Subscriber<? super Error> subscriber) {
				if(AndroidUtils.hasConnection(context)) {
					subscriber.onNext(Error.OK);
					subscriber.onCompleted();
				} else {
					subscriber.onNext(Error.NO_CONNECTION);
				}
			}
		});
	}

	public static Observable<ValidationObservable.Error> isLocationEnabled(final Context context) {
		return Observable.create(new Observable.OnSubscribe<Error>() {
			@Override
			public void call(Subscriber<? super Error> subscriber) {
				if(AndroidUtils.isLocationEnabled(context)) {
					subscriber.onNext(Error.OK);
					subscriber.onCompleted();
				} else {
					subscriber.onNext(Error.LOCATION_DISABLED);
				}
			}
		});
	}

	public static Observable<? extends ValidationObservable.Error> isBluetoothEnabled(final Context context) {
		return Observable.create(new Observable.OnSubscribe<Error>() {
			@Override
			public void call(Subscriber<? super Error> subscriber) {
				if(BluetoothUtils.isBluetoothEnabled(context)) {
					subscriber.onNext(Error.OK);
					subscriber.onCompleted();
				} else {
					subscriber.onNext(Error.BLUETOOTH_DISABLED);
				}
			}
		});
	}

	public static Observable<? extends ValidationObservable.Error> validate(final Context context) {
		return Observable.concat(hasConnection(context), isLocationEnabled(context), isBluetoothEnabled(context))
		                 .timeout(TIMEOUT, TimeUnit.MILLISECONDS)
		                 .delaySubscription(400, TimeUnit.MILLISECONDS)
		                 .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
		                 .takeFirst(new Func1<Error, Boolean>() {
			                 @Override
			                 public Boolean call(Error error) {
				                 return error != Error.OK;
			                 }
		                 });
	}

	public static Observable<? extends ValidationObservable.Error> validateSmart(final Context context, final boolean isDemo) {
		final Observable<Error> observableChain;
		if(isDemo) {
			observableChain = hasConnection(context);
		} else {
			if(hasBluetooth()) {
				observableChain = Observable.concat(hasConnection(context), isLocationEnabled(context), isBluetoothEnabled(context));
			} else {
				observableChain = Observable.concat(hasConnection(context), isLocationEnabled(context));
			}
		}
		return observableChain
				.timeout(TIMEOUT, TimeUnit.MILLISECONDS)
				.delaySubscription(400, TimeUnit.MILLISECONDS)
				.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.takeFirst(new Func1<Error, Boolean>() {
					@Override
					public Boolean call(Error error) {
						return error != Error.OK;
					}
				});
	}

	public static Observable<? extends ValidationObservable.Error> validateSmart(final Context context) {
		return validateSmart(context, false);
	}

	private static boolean hasBluetooth() {
		return BluetoothAdapter.getDefaultAdapter() != null;
	}
}
