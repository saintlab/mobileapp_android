package com.omnom.android.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.os.Build;

import com.omnom.android.fragment.menu.OrderUpdateEvent;
import com.omnom.android.utils.observable.BluetoothObservable;
import com.squareup.otto.Subscribe;

import altbeacon.beacon.Beacon;
import rx.Observable;

/**
 * Created by Ch3D on 03.06.2015.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ValidateActivityBle18 extends ValidateActivityBle {

	@Subscribe
	public void onOrderUpdate(OrderUpdateEvent event) {
		updateOrderData(event);
	}

	@Override
	protected Observable<Beacon> getBeaconsObservable(final int scanDuration) {
		return BluetoothObservable.getScanResultObservableJB(parser,
		                                                     BluetoothAdapter.getDefaultAdapter(),
		                                                     scanDuration);
	}
}
