package com.omnom.android.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.widget.Button;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.adapter.RestaurantsAdapter;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.ViewUtils;

import java.util.Calendar;

import butterknife.InjectView;
import butterknife.OnClick;

public class RestaurantActivity extends BaseOmnomActivity {

	public static void start(BaseOmnomActivity activity, Restaurant restaurant) {
		start(activity, restaurant, false);
	}

	public static void start(BaseOmnomActivity activity, Restaurant restaurant, boolean finish) {
		final Intent intent = new Intent(activity, RestaurantActivity.class);
		intent.putExtra(EXTRA_RESTAURANT, restaurant);
		activity.start(intent, finish);
	}

	protected RestaurantsAdapter.RestaurantViewHolder mRestaurantViewHolder;

	@InjectView(R.id.btn_call)
	protected Button btnCall;

	private Restaurant mRestaurant;

	private boolean mFinishing = false;

	@OnClick(R.id.txt_reserve)
	protected void doReserve() {
		if(!mFinishing) {
			// TODO: Implement
		}
	}

	@OnClick(R.id.txt_im_inside)
	protected void doImInside() {
		if(!mFinishing) {
			// TODO: Implement
		}
	}

	@OnClick(R.id.btn_call)
	protected void doCall() {
		if(!mFinishing) {
			AndroidUtils.openDialer(this, mRestaurant.phone());
		}
	}

	@OnClick(R.id.txt_order)
	protected void doMakeOrder() {
		if(!mFinishing) {
			// TODO: Implement
		}
	}

	@OnClick(R.id.btn_close)
	protected void doClose() {
		if(!mFinishing) {
			finish();
		}
	}

	@OnClick(R.id.btn_demo)
	protected void doDemo() {
		if(!mFinishing) {
			ValidateActivity.startDemo(this, R.anim.fake_fade_in_instant, R.anim.fake_fade_out_instant, EXTRA_LOADER_ANIMATION_SCALE_DOWN);
		}
	}

	@Override
	protected void handleIntent(final Intent intent) {
		mRestaurant = intent.getParcelableExtra(EXTRA_RESTAURANT);
	}

	@Override
	public void finish() {
		if(mFinishing) {
			// skip : already finishing
			return;
		}
		mFinishing = true;

		if(mRestaurantViewHolder == null) {
			finishSimple();
			return;
		}

		final int translationY = getResources().getDimensionPixelSize(R.dimen.restaurants_topbar_height);
		mRestaurantViewHolder.minimize(translationY);
		btnCall.animate().alpha(0).start();

		AnimationUtils.animateAlpha(findViewById(R.id.panel_bottom), false);

		AnimationUtils
				.scaleHeight(mRestaurantViewHolder.imgCover, getResources().getDimensionPixelSize(R.dimen.restaurant_cover_height_small),
				             new Runnable() {
					             @Override
					             public void run() {
						             finishSimple();
					             }
				             });
	}

	private void finishSimple() {
		RestaurantActivity.super.finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
		final String phone = mRestaurant.phone();
		if(!TextUtils.isEmpty(phone)) {
			btnCall.setText(PhoneNumberUtils.formatNumber(phone));
		} else {
			ViewUtils.setVisible(btnCall, false);
		}
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_restaurant;
	}
}
