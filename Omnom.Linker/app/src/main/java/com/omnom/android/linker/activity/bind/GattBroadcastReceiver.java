package com.omnom.android.linker.activity.bind;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.omnom.android.linker.activity.base.Extras;
import com.omnom.android.linker.model.ibeacon.BeaconDataResponse;
import com.omnom.android.linker.service.BluetoothLeService;

import java.util.UUID;

import hugo.weaving.DebugLog;

/**
 * Created by Ch3D on 12.08.2014.
 */
class GattBroadcastReceiver extends BroadcastReceiver {
	private BindActivity activity;

	public GattBroadcastReceiver(BindActivity activity) {this.activity = activity;}

	@Override
	@DebugLog
	public void onReceive(Context context, Intent intent) {
		if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(intent.getAction())) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					activity.gattAvailable = true;
					BeaconDataResponse data = activity.getBeaconData();
					activity.writeBeaconData(data.uuid, data.major, data.minor);
				}
			});
		} else if(BluetoothLeService.ACTION_GATT_FAILED.equals(intent.getAction())) {
			activity.gattConnected = false;
			activity.gattAvailable = false;
		} else if(BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(intent.getAction())) {
			activity.gattConnected = false;
			activity.gattAvailable = false;
		} else if(BluetoothLeService.ACTION_GATT_CONNECTED.equals(intent.getAction())) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					activity.gattConnected = true;
					activity.mBluetoothLeService.getDiscoverGattService();
				}
			});
		} else if(BluetoothLeService.ACTION_CHARACTERISTIC_UPDATE.equals(intent.getAction())) {
			final UUID cUuid = UUID.fromString(intent.getExtras().getString(Extras.EXTRA_CHARACTERISTIC_UUID));
			final String cValue = intent.getStringExtra(Extras.EXTRA_CHARACTERISTIC_VALUE);
			activity.updateBeaconData(cUuid, cValue);
		}
	}
}
