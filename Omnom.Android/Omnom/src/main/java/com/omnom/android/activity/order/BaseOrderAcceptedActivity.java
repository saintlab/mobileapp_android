package com.omnom.android.activity.order;

import android.content.Intent;
import android.view.View;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomModeSupportActivity;
import com.omnom.android.entrance.BarEntranceData;
import com.omnom.android.entrance.DeliveryEntranceData;
import com.omnom.android.entrance.EntranceData;
import com.omnom.android.entrance.TakeawayEntranceData;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.utils.activity.OmnomActivity;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.view.HeaderView;

import java.text.SimpleDateFormat;

import butterknife.InjectView;

public abstract class BaseOrderAcceptedActivity extends BaseOmnomModeSupportActivity {

	protected final SimpleDateFormat ORDER_TIME_FORMAT = new SimpleDateFormat("dd MMMM в HH:mm", AndroidUtils.russianLocale);

	public static void start(OmnomActivity activity, Restaurant restaurant, EntranceData entranceData, int requestCode) {
		final Intent intent = new Intent(activity.getActivity(), getOrderAcceptedActivity(entranceData));
		intent.putExtra(EXTRA_ENTRANCE_DATA, entranceData);
		intent.putExtra(EXTRA_RESTAURANT, restaurant);
		intent.putExtra(EXTRA_ACCENT_COLOR, RestaurantHelper.getBackgroundColor(restaurant));
		activity.startForResult(intent, R.anim.slide_in_up, R.anim.nothing_long, requestCode);
	}

	private static Class getOrderAcceptedActivity(final EntranceData entranceData) {
		if(entranceData == null) {
			throw new IllegalArgumentException("entranceData should not be null");
		}
		if(entranceData instanceof BarEntranceData) {
			return BarOrderAcceptedActivity.class;
		} else if(entranceData instanceof DeliveryEntranceData) {
			return LunchOrderAcceptedActivity.class;
		} else if(entranceData instanceof TakeawayEntranceData) {
			return TakeawayOrderAcceptedActivity.class;
		} else {
			throw new IllegalArgumentException("Unknown entrance data type");
		}
	}

	@InjectView(R.id.panel_top)
	protected HeaderView topPanel;

	private int mAccentColor;

	protected Restaurant mRestaurant;

	@Override
	public int getLayoutResource() {
		return R.layout.activity_order_accepted;
	}

	@Override
	protected void handleIntent(final Intent intent) {
		super.handleIntent(intent);
		mAccentColor = intent.getIntExtra(EXTRA_ACCENT_COLOR, 0);
		mRestaurant = intent.getParcelableExtra(EXTRA_RESTAURANT);
	}

	@Override
	public void initUi() {
		AndroidUtils.setAccentColor(getWindow(), mAccentColor);
		topPanel.setTitleBig(R.string.bill);
		topPanel.setButtonLeft(R.string.ready, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				finish();
			}
		});
	}

	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
		overridePendingTransition(R.anim.nothing, R.anim.slide_out_down);
	}

}
