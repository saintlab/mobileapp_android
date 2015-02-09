package com.omnom.android.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.view.View;

import com.omnom.android.R;
import com.omnom.android.fragment.menu.OrderUpdateEvent;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.mixpanel.model.OnTableMixpanelEvent;
import com.omnom.android.restaurateur.model.decode.BeaconDecodeRequest;
import com.omnom.android.restaurateur.model.decode.BeaconRecord;
import com.omnom.android.restaurateur.model.decode.RestaurantResponse;
import com.omnom.android.restaurateur.model.decode.RssiRecord;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.utils.ObservableUtils;
import com.omnom.android.utils.loader.LoaderError;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.observable.ValidationObservable;
import com.omnom.android.utils.utils.AndroidUtils;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.BeaconParser;
import hugo.weaving.DebugLog;
import retrofit.RetrofitError;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

public class ValidateActivityBle extends ValidateActivity {

	private static final String TAG = ValidateActivityBle.class.getSimpleName();

	protected BluetoothAdapter mBluetoothAdapter;

	protected BeaconParser parser;

	protected LinkedList<BeaconRecord> mBeacons = new LinkedList<BeaconRecord>();

	private BluetoothAdapter.LeScanCallback mLeScanCallback;

	private Subscription mValidateSubscribtion;

	private Subscription mFindBeaconSubscription;

	@Override
	public void initUi() {
		super.initUi();
		if(AndroidUtils.isKitKat()) {
			initBle();
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	protected void initBle() {
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
				});
			}
		};
	}

	protected BeaconRecord find(final LinkedList<BeaconRecord> beacons, final Beacon beacon) {
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
		final Runnable endCallback = new Runnable() {
			@Override
			public void run() {
				mFindBeaconSubscription = AndroidObservable.bindActivity(ValidateActivityBle.this,
				                                                         api.decode(new BeaconDecodeRequest(
						                                                         getResources().getInteger(
								                                                         R.integer.ble_scan_duration),
						                                                         Collections.unmodifiableList(mBeacons)
				                                                         ), mPreloadBackgroundFunction)
				                                                        )
				                                           .subscribe(new Action1<RestaurantResponse>() {
					                                           @Override
					                                           public void call(final RestaurantResponse response) {
						                                           if(response.hasErrors()) {
							                                           startErrorTransition();
							                                           mErrorHelper.showErrorDemo(
									                                           LoaderError.BACKEND_ERROR,
									                                           mInternetErrorClickListener);
						                                           } else {
							                                           handleDecodeResponse(OnTableMixpanelEvent.METHOD_BLUETOOTH,
							                                                                response);
						                                           }
					                                           }
				                                           }, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
					                                           @Override
					                                           protected void onError(final Throwable throwable) {
						                                           if(throwable instanceof RetrofitError) {
							                                           startErrorTransition();
							                                           mErrorHelper.showErrorDemo(
									                                           LoaderError.BACKEND_ERROR,
									                                           mInternetErrorClickListener);
						                                           } else {
							                                           startErrorTransition();
							                                           mErrorHelper.showErrorDemo(
									                                           LoaderError.NO_CONNECTION_TRY,
									                                           mInternetErrorClickListener);
						                                           }
					                                           }
				                                           });
			}
		};
		if(AndroidUtils.isKitKat()) {
			scanBleDevices(true, endCallback);
		}
	}

	@Override
	protected void reportMixPanel(final String requestId, final String method, final TableDataResponse tableDataResponse) {
		if(tableDataResponse == null) {
			return;
		}
		getMixPanelHelper().track(MixPanelHelper.Project.OMNOM,
		                          OnTableMixpanelEvent.create(requestId, getUserData(),
		                                                      tableDataResponse.getRestaurantId(),
		                                                      tableDataResponse.getId(), method));
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	protected void scanBleDevices(final boolean enable, final Runnable endCallback) {
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

	@Subscribe
	public void onOrderUpdate(OrderUpdateEvent event) {
		updateOrderData(event);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_validate;
	}
}