package com.omnom.android.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.client.android.CaptureActivity;
import com.omnom.android.BuildConfig;
import com.omnom.android.R;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.utils.AndroidUtils;

import javax.inject.Inject;

import altbeacon.beacon.Beacon;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.functions.Func1;

public class ValidateActivityCamera extends ValidateActivity {
	public static final String DEVICE_ID_GENYMOTION = "000000000000000";

	private static final String TAG = ValidateActivityCamera.class.getSimpleName();

	public class DemoBeacon extends Beacon {
		public DemoBeacon() {
			super();
		}

		@Override
		public String getIdValue(final int i) {
			switch(i) {
				case 0:
					return "E2C56DB5-DFFB-48D2-B060-D0F5A71096E0";

				case 1:
					return "1";

				case 2:
					return "A";
				default:
					return super.getIdValue(i);
			}
		}
	}

	@Inject
	protected RestaurateurObeservableApi api;

	private Subscription mCheckQrSubscribtion;

	private String mQrData;

	private Subscription mFindBeaconSubscription;

	@Override
	protected void startLoader() {
		if(BuildConfig.DEBUG && AndroidUtils.getDeviceId(this).equals(DEVICE_ID_GENYMOTION)) {
			findTableForQr("http://www.riston.ru/wishes");
			return;
		}

		final Intent intent = new Intent(this, CaptureActivity.class);
		intent.putExtra(CaptureActivity.EXTRA_SHOW_BACK, false);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			final ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right,
			                                                                            R.anim.slide_out_left);
			startActivityForResult(intent, REQUEST_CODE_SCAN_QR, activityOptions.toBundle());
		} else {
			startActivityForResult(intent, REQUEST_CODE_SCAN_QR);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OmnomObservable.unsubscribe(mCheckQrSubscribtion);
	}

	@Override
	protected void validate() {
		if(mIsDemo) {
			loader.startProgressAnimation(10000, new Runnable() {
				@Override
				public void run() {
				}
			});
			Beacon demoBeacon = new DemoBeacon();

			final TableDataResponse[] table = new TableDataResponse[1];
			mFindBeaconSubscription = AndroidObservable.bindActivity(getActivity(), api.findBeacon(demoBeacon).flatMap(
					new Func1<TableDataResponse, Observable<Restaurant>>() {
						@Override
						public Observable<Restaurant> call(TableDataResponse tableDataResponse) {
							if(tableDataResponse.hasAuthError()) {
								EnteringActivity.start(ValidateActivityCamera.this);
								throw new RuntimeException("Wrong auth token");
							} else {
								table[0] = tableDataResponse;
								return api.getRestaurant(tableDataResponse.getRestaurantId());
							}
						}
					})).subscribe(new Action1<Restaurant>() {
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
			return;
		}
		if(TextUtils.isEmpty(mQrData)) {
			super.validate();
		}
	}

	@Override
	protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			if(requestCode == REQUEST_CODE_SCAN_QR) {
				loader.startProgressAnimation(10000, new Runnable() {
					@Override
					public void run() {
					}
				});
				mQrData = data.getExtras().getString(CaptureActivity.EXTRA_SCANNED_URI);
				findTableForQr(mQrData);
			}
		} else {
			finish();
		}
	}

	private void findTableForQr(final String mQrData) {
		final TableDataResponse[] table = new TableDataResponse[1];
		mCheckQrSubscribtion = AndroidObservable.bindActivity(this, api.checkQrCode(mQrData).flatMap(
				new Func1<TableDataResponse, Observable<Restaurant>>() {
					@Override
					public Observable<Restaurant> call(TableDataResponse tableDataResponse) {
						table[0] = tableDataResponse;
						return api.getRestaurant(tableDataResponse.getRestaurantId());
					}
				})).subscribe(new Action1<Restaurant>() {
			@Override
			public void call(final Restaurant restaurant) {
				onDataLoaded(restaurant, table[0]);
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				Log.e(TAG, "findTableFor: qr = " + mQrData, throwable);
			}
		});
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_validate;
	}
}