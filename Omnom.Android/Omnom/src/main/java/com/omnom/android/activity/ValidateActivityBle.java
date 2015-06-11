package com.omnom.android.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.omnom.android.R;
import com.omnom.android.activity.validate.ValidateActivity;
import com.omnom.android.fragment.menu.OrderUpdateEvent;
import com.omnom.android.menu.model.MenuResponse;
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
import com.omnom.android.utils.utils.BluetoothUtils;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.BeaconParser;
import hugo.weaving.DebugLog;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

public abstract class ValidateActivityBle extends ValidateActivity {

	public static class OnScanComplete implements Action0 {

		private final Subscription mSubscription;

		private final Runnable mCallback;

		private OnScanComplete(Subscription subscription, Runnable callback) {
			mSubscription = subscription;
			mCallback = callback;
		}

		@Override
		public void call() {
			OmnomObservable.unsubscribe(mSubscription);
			mCallback.run();
		}
	}

	private static final String TAG = ValidateActivityBle.class.getSimpleName();

	protected final Action1<Throwable> mOnErrorScan = new Action1<Throwable>() {
		@Override
		@DebugLog
		public void call(final Throwable throwable) {
			Log.e(TAG, "scanBleDevices", throwable);
		}
	};

	protected BeaconParser parser;

	protected LinkedList<BeaconRecord> mBeacons = new LinkedList<>();

	protected final Action1<Beacon> mOnNextScanAction = new Action1<Beacon>() {
		@Override
		@DebugLog
		public void call(final Beacon beacon) {
			BeaconRecord record = find(mBeacons, beacon);
			if(record == null) {
				record = BeaconRecord.create(beacon);
				mBeacons.add(record);
			}
			record.addRssi(new RssiRecord(beacon.getRssi(), System.currentTimeMillis()));
		}
	};

	private final Runnable endCallback = new Runnable() {
		@Override
		public void run() {
			decodeBeacons();
		}
	};

	protected Subscription mBleScanSubscription;

	protected final OnScanComplete mOnCompleteScan = new OnScanComplete(mBleScanSubscription, endCallback);

	private void decodeBeacons() {
		final Observable<RestaurantResponse> decodeObservable =
				api.decode(new BeaconDecodeRequest(getResources().getInteger(R.integer.ble_scan_duration),
				                                   Collections.unmodifiableList(mBeacons)), mPreloadBgFunc);

		subscribe(concatMenuObservable(decodeObservable),
		          new Action1<Pair<RestaurantResponse, MenuResponse>>() {
			          @Override
			          public void call(final Pair<RestaurantResponse, MenuResponse> pair) {
				          final RestaurantResponse response = pair.first;
				          if(response.hasErrors()) {
					          startErrorTransition();
					          getErrorHelper().showErrorDemo(LoaderError.BACKEND_ERROR, mInternetErrorClickListener);
				          } else {
					          handleDecodeResponse(OnTableMixpanelEvent.METHOD_BLUETOOTH, response);
				          }
			          }
		          }, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
					@Override
					protected void onError(final Throwable throwable) {
						startErrorTransition();
						if(throwable instanceof RetrofitError) {
							getErrorHelper().showErrorDemo(LoaderError.BACKEND_ERROR, mInternetErrorClickListener);
						} else {
							getErrorHelper().showErrorDemo(LoaderError.NO_CONNECTION_TRY, mInternetErrorClickListener);
						}
					}
				});
	}

	@Override
	public void initUi() {
		super.initUi();
		if(BluetoothUtils.isBluetoothEnabled(this) && AndroidUtils.isJellyBeanMR2()) {
			initBle();
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(AndroidUtils.isJellyBeanMR2()) {
			OmnomObservable.unsubscribe(mBleScanSubscription);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	protected void initBle() {
		parser = new BeaconParser();
		parser.setBeaconLayout(getResources().getString(R.string.redbear_beacon_layout));
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
	protected void validate() {
		if(validateDemo()) {
			return;
		}
		super.validate();
	}

	@Override
	protected void decode(final boolean startProgressAnimation) {
		if(startProgressAnimation) {
			clearErrors(true);
			mViewHelper.startProgressAnimation(getResources().getInteger(R.integer.omnom_validate_duration));
		}
		subscribe(ValidationObservable.validateSmart(this, mIsDemo)
		                              .map(OmnomObservable.getValidationFunc(this,
		                                                                     getErrorHelper(),
		                                                                     mInternetErrorClickListener)).isEmpty(),
		          new Action1<Boolean>() {
			          @Override
			          public void call(Boolean hasNoErrors) {
				          if(hasNoErrors) {
					          scanBleDevices(getResources().getInteger(R.integer.ble_scan_duration));
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
						getErrorHelper().showInternetError(mInternetErrorClickListener);
					}
				});
	}

	@Override
	protected void reportMixPanel(final String requestId, final String method, final TableDataResponse tableDataResponse) {
		if(tableDataResponse == null) {
			return;
		}
		track(MixPanelHelper.Project.OMNOM, OnTableMixpanelEvent.create(requestId, getUserData(),
		                                                                tableDataResponse.getRestaurantId(),
		                                                                tableDataResponse.getId(), method));
	}

	protected final void scanBleDevices(final int scanDuration) {
		if(!BluetoothUtils.isBluetoothEnabled(this)) {
			endCallback.run();
		} else {
			startBleScan(scanDuration);
		}
	}

	protected final void startBleScan(final int scanDuration) {
		mBeacons.clear();
		final Observable<Beacon> scanResultObservable = getBeaconsObservable(scanDuration);
		mBleScanSubscription = scanResultObservable.subscribe(mOnNextScanAction,
		                                                      mOnErrorScan,
		                                                      mOnCompleteScan);
	}

	protected abstract Observable<Beacon> getBeaconsObservable(final int scanDuration);

	@Subscribe
	public void onOrderUpdate(OrderUpdateEvent event) {
		updateOrderData(event);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_validate;
	}
}