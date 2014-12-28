package com.omnom.android.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.utils.activity.BaseActivity;

import java.util.List;

/**
 * Created by Ch3D on 28.12.2014.
 */
public class ValidateActivityShortcut extends ValidateActivityCamera {

	protected static Intent createIntent(Context context, int animationType, boolean isDemo, int userEnterType) {
		final Intent intent = new Intent(context, ValidateActivityShortcut.class);
		intent.putExtra(EXTRA_LOADER_ANIMATION, animationType);
		intent.putExtra(EXTRA_DEMO_MODE, isDemo);
		intent.putExtra(EXTRA_CONFIRM_TYPE, userEnterType);
		return intent;
	}

	public static void start(BaseActivity context, int enterAnim, int exitAnim, int animationType) {
		Intent intent = createIntent(context, animationType, false, ConfirmPhoneActivity.TYPE_DEFAULT);
		intent.putExtra(EXTRA_ANIMATION_EXIT, EXTRA_ANIMATION_SLIDE_OUT_RIGHT);
		context.start(intent, enterAnim, exitAnim, false);
	}

	@Override
	protected void handleRestaurants(final List<Restaurant> restaurants) {
		onWrongQr();
	}

	@Override
	protected void handleRestaurant(final Restaurant restaurant) {
		if(!RestaurantHelper.hasOnlyTable(restaurant)) {
			onWrongQr();
		} else {
			super.handleRestaurant(restaurant);
		}
	}

	@Override
	protected void handleEmpty() {
		onWrongQr();
	}

	private void onWrongQr() {
		startErrorTransition();
		mErrorHelper.showWrongQrError(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				startLoader();
			}
		});
	}
}
