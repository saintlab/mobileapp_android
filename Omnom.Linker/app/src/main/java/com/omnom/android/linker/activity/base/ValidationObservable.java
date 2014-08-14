package com.omnom.android.linker.activity.base;

import android.content.Context;

import com.omnom.android.linker.activity.ConnectionExecption;
import com.omnom.android.linker.activity.LocationException;
import com.omnom.android.linker.utils.AndroidUtils;
import com.omnom.android.linker.utils.BluetoothUtils;

import altbeacon.beacon.BleNotAvailableException;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Ch3D on 14.08.2014.
 */
public class ValidationObservable {
	public enum Error {
		OK, NO_CONNECTION, BLUETOOTH_DISABLED, LOCATION_DISABLED
	}

	public static Observable<? extends ValidationObservable.Error> create(final Context context) {
		return Observable.create(new Observable.OnSubscribe<ValidationObservable.Error>() {
			@Override
			public void call(Subscriber<? super ValidationObservable.Error> subscriber) {
				boolean b = AndroidUtils.hasConnection(context);
				if(b) {
					subscriber.onNext(Error.OK);
				} else {
					subscriber.onError(new ConnectionExecption());
					return;
				}
				b = BluetoothUtils.isBluetoothEnabled(context);
				if(b) {
					subscriber.onNext(Error.OK);
				} else {
					subscriber.onError(new BleNotAvailableException("BLE disabled"));
					return;
				}
				b = AndroidUtils.isLocationEnabled(context);
				if(b) {
					subscriber.onNext(Error.OK);
				} else {
					subscriber.onError(new LocationException());
					return;
				}
				subscriber.onCompleted();
			}
		});
	}
}
