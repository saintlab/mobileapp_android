package com.omnom.android.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.view.View;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.beacon.BeaconFilter;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.observable.ValidationObservable;

import java.util.ArrayList;
import java.util.List;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.BeaconParser;
import hugo.weaving.DebugLog;
import rx.Observable;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.functions.Func1;

public class ValidateActivityBle extends ValidateActivity {

	private final View.OnClickListener mInternetErrorClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			validate();
		}
	};

	private BluetoothAdapter.LeScanCallback mLeScanCallback;
	private BluetoothAdapter mBluetoothAdapter;
	private BeaconParser parser;
	private ArrayList<Beacon> mBeacons = new ArrayList<Beacon>();

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
			BeaconFilter mFilter = new BeaconFilter(OmnomApplication.get(getActivity()));

			@Override
			@DebugLog
			public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						final Beacon beacon = parser.fromScanData(scanRecord, rssi, device);
						if(mFilter.check(beacon)) {
							mBeacons.add(beacon);
						}
					}
				});
			}
		};
	}

	@Override
	protected void startLoader() {
		loader.startProgressAnimation(getResources().getInteger(R.integer.validation_duration), new Runnable() {
			@Override
			public void run() {
			}
		});
		AndroidObservable.bindActivity(this, ValidationObservable.validateSmart(this)
		                                                         .map(OmnomObservable.getValidationFunc(this,
		                                                                                                mErrorHelper,
		                                                                                                mInternetErrorClickListener))
		                                                         .isEmpty())
		                 .subscribe(new Action1<Boolean>() {
			                 @Override
			                 public void call(Boolean hasNoErrors) {
				                 if(hasNoErrors) {
					                 readBeacons();
				                 }
			                 }
		                 }, new Action1<Throwable>() {
			                 @Override
			                 public void call(Throwable throwable) {
				                 mErrorHelper.showInternetError(mInternetErrorClickListener);
			                 }
		                 });
	}

	private void readBeacons() {
		scanBleDevices(true, new Runnable() {
			@Override
			public void run() {
				final BeaconFilter filter = new BeaconFilter(OmnomApplication.get(getActivity()));
				final List<Beacon> nearBeacons = filter.filterBeacons(mBeacons);
				final int size = nearBeacons.size();
				if(size == 0) {
					mErrorHelper.showError(R.drawable.ic_weak_signal,
					                       R.string.error_weak_beacon_signal,
					                       R.string.try_once_again,
					                       mInternetErrorClickListener);
				} else if(size > 1) {
					mErrorHelper.showError(R.drawable.ic_weak_signal,
					                       R.string.error_more_than_one_beacon,
					                       R.string.try_once_again,
					                       mInternetErrorClickListener);
				} else if(size == 1) {
					final Beacon beacon = nearBeacons.get(0);
					final TableDataResponse[] table = new TableDataResponse[1];
					api.findBeacon(beacon).flatMap(new Func1<TableDataResponse, Observable<Restaurant>>() {
						@Override
						public Observable<Restaurant> call(TableDataResponse tableDataResponse) {
							table[0] = tableDataResponse;
							return api.getRestaurant(tableDataResponse.getRestaurantId());
						}
					}).subscribe(new Action1<Restaurant>() {
						@Override
						public void call(final Restaurant restaurant) {
							onDataLoaded(restaurant, table[0]);
						}
					}, new Action1<Throwable>() {
						@Override
						public void call(Throwable throwable) {
							// TODO:
						}
					});
				}
			}
		});
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