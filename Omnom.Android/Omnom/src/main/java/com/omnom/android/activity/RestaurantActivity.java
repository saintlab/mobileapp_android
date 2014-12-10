package com.omnom.android.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.adapter.RestaurantsAdapter;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.utils.utils.AndroidUtils;

import java.util.Calendar;

import butterknife.InjectView;
import butterknife.OnClick;

public class RestaurantActivity extends BaseOmnomActivity {

	public static void start(BaseOmnomActivity activity, Restaurant restaurant) {
		final Intent intent = new Intent(activity, RestaurantActivity.class);
		intent.putExtra(EXTRA_RESTAURANT, restaurant);
		activity.start(intent, false);
	}

	protected RestaurantsAdapter.RestaurantViewHolder mRestaurantViewHolder;

	@InjectView(R.id.btn_call)
	protected Button btnCall;

	private Restaurant mRestaurant;

	@OnClick(R.id.txt_reserve)
	protected void doReserve() {

	}

	@OnClick(R.id.txt_im_inside)
	protected void doImInside() {

	}

	@OnClick(R.id.btn_call)
	protected void doCall() {
		AndroidUtils.openDialer(this, mRestaurant.getPhone());
	}

	@OnClick(R.id.txt_order)
	protected void doMakeOrder() {

	}

	@Override
	protected void handleIntent(final Intent intent) {
		mRestaurant = intent.getParcelableExtra(EXTRA_RESTAURANT);
	}

	@Override
	public void initUi() {
		if(mRestaurant == null) {
			finish();
			return;
		}
		final int weekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		mRestaurantViewHolder = new RestaurantsAdapter.RestaurantViewHolder(this);
		final ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.order_item_price));
		mRestaurantViewHolder.bindData(this, mRestaurant, colorDrawable, weekDay);
		btnCall.setText(mRestaurant.getPhone());
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_restaurant;
	}
}
