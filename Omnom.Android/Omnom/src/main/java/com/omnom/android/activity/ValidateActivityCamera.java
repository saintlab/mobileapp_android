package com.omnom.android.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;

import com.google.zxing.client.android.CaptureActivity;
import com.omnom.android.R;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.table.TableDataResponse;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class ValidateActivityCamera extends ValidateActivity {
	@Inject
	protected RestaurateurObeservableApi api;

	@Override
	protected void startLoader() {
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
	protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			if(requestCode == REQUEST_CODE_SCAN_QR) {
				loader.startProgressAnimation(10000, new Runnable() {
					@Override
					public void run() {
					}
				});
				final String mQrData = data.getExtras().getString(CaptureActivity.EXTRA_SCANNED_URI);
				api.checkQrCode(mQrData).flatMap(new Func1<TableDataResponse, Observable<Restaurant>>() {
					@Override
					public Observable<Restaurant> call(TableDataResponse tableDataResponse) {
						return api.getRestaurant(tableDataResponse.getRestaurantId());
					}
				}).subscribe(new Action1<Restaurant>() {
					@Override
					public void call(final Restaurant restaurant) {
						onRestaurantLoaded(restaurant);
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						// TODO:
					}
				});
			}
		} else {
			finish();
		}
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_validate;
	}
}