package com.omnom.android.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.util.Log;

import com.omnom.android.R;
import com.omnom.android.fragment.menu.OrderUpdateEvent;
import com.omnom.android.restaurateur.model.decode.BeaconRecord;
import com.omnom.android.restaurateur.model.decode.RssiRecord;
import com.omnom.android.utils.utils.BluetoothUtils;
import com.squareup.otto.Subscribe;

import java.util.List;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.BeaconParser;

/**
 * Created by Ch3D on 03.01.2015.
 */
public class ValidateActivityBle21 extends ValidateActivityBle {

	private static final String TAG = ValidateActivityBle21.class.getSimpleName();

	private ScanCallback mScanCallback;

	private ScanSettings mScanSettings;

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void initBle() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		parser = new BeaconParser();
		parser.setBeaconLayout(getResources().getString(R.string.redbear_beacon_layout));

		ScanSettings.Builder builder = new ScanSettings.Builder();
		builder.setReportDelay(0);
		builder.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
		mScanSettings = builder.build();

		mScanCallback = new ScanCallback() {
			@Override
			public void onScanResult(final int callbackType, final ScanResult result) {
				final ScanRecord scanRecord = result.getScanRecord();
				if(scanRecord == null) {
					return;
				}

				final Beacon beacon = parser.fromScanData(scanRecord.getBytes(), result.getRssi(), result.getDevice());
				if(beacon == null) {
					return;
				}
				BeaconRecord record = find(mBeacons, beacon);
				if(record == null) {
					record = BeaconRecord.create(beacon);
					mBeacons.add(record);
				}
				record.addRssi(new RssiRecord(beacon.getRssi(), System.currentTimeMillis()));
			}

			@Override
			public void onBatchScanResults(final List<ScanResult> results) {
				for(ScanResult result : results) {
					final ScanRecord scanRecord = result.getScanRecord();
					if(scanRecord == null) {
						continue;
					}

					final Beacon beacon = parser.fromScanData(scanRecord.getBytes(), result.getRssi(), result.getDevice());
					if(beacon == null) {
						return;
					}
					BeaconRecord record = find(mBeacons, beacon);
					if(record == null) {
						record = BeaconRecord.create(beacon);
						mBeacons.add(record);
					}
					record.addRssi(new RssiRecord(beacon.getRssi(), System.currentTimeMillis()));
				}
			}

			@Override
			public void onScanFailed(final int errorCode) {
				Log.e(TAG, "onScanFailed : code = " + errorCode);
			}
		};
	}

	@Subscribe
	public void onOrderUpdate(OrderUpdateEvent event) {
		updateOrderData(event);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void scanBleDevices(final boolean enable, final Runnable endCallback) {
		final BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
		if(enable) {
			mBeacons.clear();
			findViewById(android.R.id.content).postDelayed(new Runnable() {
				@Override
				public void run() {
					if (BluetoothUtils.isAdapterStateOn(mBluetoothAdapter)) {
						scanner.stopScan(mScanCallback);
					}
					if(endCallback != null) {
						endCallback.run();
					}
				}
			}, getResources().getInteger(R.integer.ble_scan_duration));
			scanner.startScan(null, mScanSettings, mScanCallback);
		} else {
			scanner.stopScan(mScanCallback);
			if(endCallback != null) {
				endCallback.run();
			}
		}
	}
}
