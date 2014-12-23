package com.omnom.android.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.omnom.android.R;
import com.omnom.android.mixpanel.model.OnTableMixpanelEvent;
import com.omnom.android.restaurateur.model.decode.BeaconDecodeRequest;
import com.omnom.android.restaurateur.model.decode.BeaconDecodeResponse;
import com.omnom.android.restaurateur.model.decode.BeaconRecord;
import com.omnom.android.restaurateur.model.decode.RssiRecord;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.observable.ValidationObservable;

import java.util.Iterator;
import java.util.LinkedList;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.BeaconParser;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import hugo.weaving.DebugLog;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

public class ValidateActivityBle extends ValidateActivity {

	private static final String TAG = ValidateActivityBle.class.getSimpleName();

	private BluetoothAdapter.LeScanCallback mLeScanCallback;

	private BluetoothAdapter mBluetoothAdapter;

	private BeaconParser parser;

	private LinkedList<BeaconRecord> mBeacons = new LinkedList<BeaconRecord>();

	private Subscription mValidateSubscribtion;

	private Subscription mFindBeaconSubscription;

	private Crouton mCrouton;

	@Override
	public void initUi() {
		super.initUi();
		initBle();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void initBle() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		parser = new BeaconParser();
		parser.setBeaconLayout(getResources().getString(R.string.redbear_beacon_layout));
		mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
			@Override
			@DebugLog
			public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						final Beacon beacon = parser.fromScanData(scanRecord, rssi, device);
						BeaconRecord record = find(mBeacons, beacon);
						if(record == null) {
							record = BeaconRecord.create(beacon);
							mBeacons.add(record);
						}
						record.addRssi(new RssiRecord(rssi, System.currentTimeMillis()));
					}
				});
			}
		};
	}

	private BeaconRecord find(final LinkedList<BeaconRecord> beacons, final Beacon beacon) {
		final Iterator<BeaconRecord> iterator = beacons.iterator();
		while(iterator.hasNext()) {
			final BeaconRecord next = iterator.next();
			if(next.getUuid().equals(beacon.getIdValue(0))
					&& next.getMajor().equals(beacon.getIdValue(1))
					&& next.getMinor().equals(beacon.getIdValue(2))) {
				return next;
			}
		}
		return null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OmnomObservable.unsubscribe(mFindBeaconSubscription);
		OmnomObservable.unsubscribe(mValidateSubscribtion);
	}

	@Override
	protected void validate() {
		if(validateDemo()) {
			return;
		}
		super.validate();
	}

	@Override
	protected void startLoader() {
		clearErrors(true);
		loader.startProgressAnimation(getResources().getInteger(R.integer.omnom_validate_duration), new Runnable() {
			@Override
			public void run() {
			}
		});
		mValidateSubscribtion = AndroidObservable.bindActivity(this, ValidationObservable.validateSmart(this, mIsDemo)
		                                                                                 .map(OmnomObservable.getValidationFunc(this,
		                                                                                                                        mErrorHelper,
		                                                                                                                        mInternetErrorClickListener))
		                                                                                 .isEmpty())
		                                         .subscribe(new Action1<Boolean>() {
			                                         @Override
			                                         public void call(Boolean hasNoErrors) {
				                                         if(hasNoErrors) {
					                                         readBeacons();
				                                         } else {
					                                         startErrorTransition();
					                                         final View viewById = findViewById(R.id.panel_bottom);
					                                         if(viewById != null) {
						                                         viewById.animate().translationY(200).start();
					                                         }
				                                         }
			                                         }
		                                         }, new Action1<Throwable>() {
			                                         @Override
			                                         public void call(Throwable throwable) {
				                                         startErrorTransition();
				                                         mErrorHelper.showInternetError(mInternetErrorClickListener);
			                                         }
		                                         });
	}

	private void readBeacons() {
		scanBleDevices(true, new Runnable() {
			@Override
			public void run() {
				api.decode(new BeaconDecodeRequest(getResources().getInteger(R.integer.ble_scan_duration), mBeacons)).subscribe(
						new Action1<BeaconDecodeResponse>() {
							@Override
							public void call(final BeaconDecodeResponse beaconDecodeResponse) {
								// TODO: discuss and implement
							}
						},
						new Action1<Throwable>() {
							@Override
							public void call(final Throwable throwable) {
								Log.e(TAG, "readBeacons", throwable);
							}
						});
			}
		});
	}

	private void reportMixPanel(final TableDataResponse tableDataResponse) {
		getMixPanelHelper().track(OnTableMixpanelEvent.createEventBluetooth(getUserData(), tableDataResponse.getRestaurantId(),
		                                                                    tableDataResponse.getId()));
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void scanBleDevices(final boolean enable, final Runnable endCallback) {
		if(enable) {
			mBeacons.clear();
			findViewById(android.R.id.content).postDelayed(new Runnable() {
				@Override
				public void run() {
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					if(endCallback != null) {
						endCallback.run();
					}
				}
			}, getResources().getInteger(R.integer.ble_scan_duration));
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			if(endCallback != null) {
				endCallback.run();
			}
		}
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_validate;
	}
}