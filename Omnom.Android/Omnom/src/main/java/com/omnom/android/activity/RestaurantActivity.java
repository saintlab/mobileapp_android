package com.omnom.android.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import android.widget.ImageView;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.adapter.RestaurantsAdapter;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;

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

	@OnClick(R.id.btn_close)
	protected void doClose() {
		finish();
	}

	@OnClick(R.id.btn_demo)
	protected void doDemo() {
		ValidateActivity.start(this,
		                       R.anim.fake_fade_in_instant,
		                       R.anim.fake_fade_out_instant,
		                       EXTRA_LOADER_ANIMATION_SCALE_DOWN, true);
	}

	@Override
	protected void handleIntent(final Intent intent) {
		mRestaurant = intent.getParcelableExtra(EXTRA_RESTAURANT);
	}

	@Override
	public void finish() {
		final ImageView imgCover = mRestaurantViewHolder.imgCover;
		imgCover.animate().translationYBy(getResources().getDimensionPixelSize(R.dimen.view_size_default)).start();
		AnimationUtils.scaleHeight(imgCover,
		                           getResources().getDimensionPixelSize(R.dimen.restaurant_cover_height_small), new Runnable() {
					@Override
					public void run() {
						RestaurantActivity.super.finish();
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					}
				});
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
