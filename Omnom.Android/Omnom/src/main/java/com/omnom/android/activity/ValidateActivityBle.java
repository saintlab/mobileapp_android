package com.omnom.android.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.view.View;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.beacon.BeaconFilter;
import com.omnom.android.preferences.PreferenceHelper;
import com.omnom.android.restaurateur.model.beacon.BeaconFindRequest;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.utils.ObservableUtils;
import com.omnom.android.utils.loader.LoaderError;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.observable.ValidationObservable;

import java.util.ArrayList;
import java.util.List;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.BeaconParser;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import hugo.weaving.DebugLog;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.functions.Func1;

public class ValidateActivityBle extends ValidateActivity {

	private static final String TAG = ValidateActivityBle.class.getSimpleName();

	private BluetoothAdapter.LeScanCallback mLeScanCallback;

	private BluetoothAdapter mBluetoothAdapter;

	private BeaconParser parser;

	private ArrayList<Beacon> mBeacons = new ArrayList<Beacon>();

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
		mValidateSubscribtion = AndroidObservable.bindActivity(this, ValidationObservable.validateSmart(this)
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
				final OmnomApplication application = OmnomApplication.get(getActivity());
				final BeaconFilter filter = new BeaconFilter(application);
				final List<Beacon> nearBeacons = filter.filterBeacons(mBeacons);
				final int size = nearBeacons.size();
				application.cacheBeacon(null);
				if(size == 0) {
					startErrorTransition();
					mErrorHelper.showErrorDemo(LoaderError.WEAK_SIGNAL, mInternetErrorClickListener);
				} else if(size > 1) {
					startErrorTransition();
					mErrorHelper.showError(LoaderError.TWO_BEACONS, mInternetErrorClickListener);
				} else if(size == 1) {
					final Beacon beacon = nearBeacons.get(0);
					application.cacheBeacon(new BeaconFindRequest(beacon));
					final TableDataResponse[] table = new TableDataResponse[1];
					mFindBeaconSubscription = AndroidObservable.bindActivity(getActivity(),
					                                                         api.findBeacon(beacon)
					                                                            .flatMap(
							                                                            new Func1<TableDataResponse,
									                                                            Observable<Restaurant>>() {
								                                                            @Override
								                                                            public Observable<Restaurant> call
										                                                            (TableDataResponse tableDataResponse) {
									                                                            if(tableDataResponse.hasAuthError()) {
										                                                            EnteringActivity.start(
												                                                            ValidateActivityBle.this);
										                                                            throw new RuntimeException(
												                                                            "Wrong auth token");
									                                                            } else if(tableDataResponse.hasErrors()) {
										                                                            startErrorTransition();
										                                                            mErrorHelper.showErrorDemo(
												                                                            LoaderError.WEAK_SIGNAL,
												                                                            mInternetErrorClickListener);
										                                                            return Observable.empty();
									                                                            } else {
										                                                            table[0] = tableDataResponse;
										                                                            return api.getRestaurant(
												                                                            tableDataResponse
														                                                            .getRestaurantId(),
												                                                            mPreloadBackgroundFunction);
									                                                            }
								                                                            }
							                                                            }))
					                                           .subscribe(
							                                           new Action1<Restaurant>() {
								                                           @Override
								                                           public void call(final Restaurant restaurant) {
									                                           onDataLoaded(restaurant, table[0]);
									                                           ((PreferenceHelper) getPreferences()).saveBeacon(
											                                           getActivity(), beacon);
								                                           }
							                                           },
							                                           new ObservableUtils.BaseOnErrorHandler(getActivity()) {
								                                           @Override
								                                           protected void onError(final Throwable throwable) {
									                                           startErrorTransition();
									                                           mErrorHelper.showErrorDemo(
											                                           LoaderError.NO_CONNECTION_TRY,
											                                           mInternetErrorClickListener);
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