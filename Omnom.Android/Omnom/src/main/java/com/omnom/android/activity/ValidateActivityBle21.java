package com.omnom.android.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.support.annotation.NonNull;

import com.omnom.android.fragment.menu.OrderUpdateEvent;
import com.omnom.android.utils.observable.BluetoothObservable;
import com.squareup.otto.Subscribe;

import altbeacon.beacon.Beacon;
import rx.Observable;

/**
 * Created by Ch3D on 03.01.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ValidateActivityBle21 extends ValidateActivityBle {

	private ScanSettings getScanSettings() {
		ScanSettings.Builder builder = new ScanSettings.Builder();
		builder.setReportDelay(0);
		builder.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
		return builder.build();
	}

	@Subscribe
	public void onOrderUpdate(OrderUpdateEvent event) {
		updateOrderData(event);
	}

	@NonNull
	@Override
	protected Observable<Beacon> getBeaconsObservable(final int scanDuration) {
		return BluetoothObservable.getScanResultObservable(parser,
		                                                   BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner(),
		                                                   getScanSettings(),
		                                                   scanDuration);
	}
}
