package com.omnom.android.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.omnom.android.R;
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
		intent.putExtra(EXTRA_SKIP_VIEW_RENDERING, true);
		return intent;
	}

	public static void start(BaseActivity context) {
		start(context, R.anim.fake_fade_in_instant, R.anim.slide_out_down, EXTRA_LOADER_ANIMATION_SCALE_DOWN);
	}

	public static void start(BaseActivity context, int enterAnim, int exitAnim, int animationType) {
		Intent intent = createIntent(context, animationType, false, ConfirmPhoneActivity.TYPE_DEFAULT);
		intent.putExtra(EXTRA_ANIMATION_EXIT, EXTRA_ANIMATION_SLIDE_OUT_RIGHT);
		context.startForResult(intent, enterAnim, exitAnim, REQUEST_CODE_CHANGE_TABLE);
	}

	@Override
	protected void handleRestaurants(final String requestId, final List<Restaurant> restaurants) {
		onWrongQr(requestId);
	}

	@Override
	protected void handleRestaurant(final String requestId, final Restaurant restaurant) {
		if(!RestaurantHelper.hasOnlyTable(restaurant)) {
			onWrongQr(requestId);
		} else {
			super.handleRestaurant(requestId, restaurant);
			setResult(RESULT_CODE_TABLE_CHANGED);
		}
	}

	@Override
	protected void handleEmptyResponse(final String requestId) {
		onWrongQr(requestId);
	}

	private void onWrongQr(final String requestId) {
		startErrorTransition();
		mErrorHelper.showWrongQrError(requestId, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				startLoader();
			}
		});
	}
}
