package com.omnom.android.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.omnom.android.R;
import com.omnom.android.fragment.menu.OrderUpdateEvent;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.mixpanel.model.OnTableMixpanelEvent;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.utils.activity.BaseActivity;
import com.omnom.android.utils.activity.BaseFragmentActivity;
import com.squareup.otto.Subscribe;

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
		start(context, R.anim.slide_out_left, R.anim.slide_in_right, EXTRA_LOADER_ANIMATION_SCALE_DOWN);
	}

	public static void start(BaseActivity context, int enterAnim, int exitAnim, int animationType) {
		Intent intent = createIntent(context, animationType, false, ConfirmPhoneActivity.TYPE_DEFAULT);
		context.startForResult(intent, enterAnim, exitAnim, REQUEST_CODE_CHANGE_TABLE);
	}

	public static void start(BaseFragmentActivity context, int enterAnim, int exitAnim, int animationType) {
		Intent intent = createIntent(context, animationType, false, ConfirmPhoneActivity.TYPE_DEFAULT);
		context.startForResult(intent, enterAnim, exitAnim, REQUEST_CODE_CHANGE_TABLE);
	}

	@Override
	protected void handleHashRestaurants(final String requestId, final Restaurant restaurant, final Menu menu) {
		TableDataResponse table = RestaurantHelper.getTable(restaurant);
		reportMixPanel(requestId, OnTableMixpanelEvent.METHOD_HASH, table);
		mMenu = menu;
		bindMenuData();
		onDataLoaded(restaurant, table, RestaurantHelper.hasOrders(restaurant), requestId);
	}

	@Override
	protected void handleRestaurants(final String requestId, final List<Restaurant> restaurants) {
		onWrongQr(requestId);
	}

	@Override
	protected void handleRestaurant(final String method, final String requestId, final Restaurant restaurant) {
		if(RestaurantHelper.getTable(restaurant) == null) {
			onWrongQr(requestId);
		} else {
			super.handleRestaurant(method, requestId, restaurant);
			setResult(RESULT_CODE_TABLE_CHANGED);
		}
	}

	@Override
	protected void handleEmptyResponse(final String requestId) {
		onWrongQr(requestId);
	}

	private void onWrongQr(final String requestId) {
		startErrorTransition();
		getErrorHelper().showWrongQrError(requestId, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				decode(true);
			}
		});
	}

	@Subscribe
	public void onOrderUpdate(OrderUpdateEvent event) {
		updateOrderData(event);
	}

}
