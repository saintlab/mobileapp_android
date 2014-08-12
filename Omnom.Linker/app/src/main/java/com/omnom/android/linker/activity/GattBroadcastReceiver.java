package com.omnom.android.linker.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.omnom.android.linker.service.BluetoothLeService;
import com.omnom.android.linker.service.RBLBluetoothAttributes;

/**
* Created by Ch3D on 12.08.2014.
*/
class GattBroadcastReceiver extends BroadcastReceiver {
	private ValidationActivity activity;

	public GattBroadcastReceiver(ValidationActivity activity) {this.activity = activity;}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(intent.getAction())) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					final BluetoothGattService service = activity.mBluetoothLeService.getService(
							RBLBluetoothAttributes.UUID_BLE_REDBEAR_PASSWORD_SERVICE);
					final BluetoothGattCharacteristic characteristic = service.getCharacteristic(
							RBLBluetoothAttributes.UUID_BLE_REDBEAR_PASSWORD);
					characteristic.setValue(RBLBluetoothAttributes.RBL_PASSKEY);
					activity.mBluetoothLeService.writeCharacteristic(characteristic);
				}
			});
		}
		if(intent.getAction() == BluetoothLeService.ACTION_GATT_CONNECTED) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activity.mBluetoothLeService.getDiscoverGattService();
						}
					});
				}
			});
		}
	}
}
