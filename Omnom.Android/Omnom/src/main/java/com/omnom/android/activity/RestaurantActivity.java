package com.omnom.android.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.View;
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

	public static void start(BaseOmnomActivity activity, Restaurant restaurant, final int topTranslation) {
		start(activity, restaurant, false, topTranslation);
	}

	public static void start(BaseOmnomActivity activity, Restaurant restaurant, boolean finish) {
		start(activity, restaurant, finish, 0);
	}

	public static void start(BaseOmnomActivity activity, Restaurant restaurant, boolean finish, final int topTranslation) {
		final Intent intent = new Intent(activity, RestaurantActivity.class);
		intent.putExtra(EXTRA_RESTAURANT, restaurant);
		intent.putExtra(EXTRA_TRANSLATION_TOP, topTranslation);
		activity.start(intent, finish);
	}

	protected RestaurantsAdapter.RestaurantViewHolder mRestaurantViewHolder;

	@InjectView(R.id.btn_call)
	protected Button btnCall;

	@InjectView(R.id.img_cover)
	protected View viewCover;

	@InjectView(R.id.main_content)
	protected View viewMain;

	private Restaurant mRestaurant;

	private boolean mFinishing = false;

	private int mTopTranslation;

	@OnClick(R.id.txt_reserve)
	protected void doReserve() {
		if(!mFinishing) {
			// TODO: Implement
		}
	}

	@OnClick(R.id.txt_im_inside)
	protected void doImInside() {
		if(!mFinishing) {
			ValidateActivityShortcut.start(this);
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
		mTopTranslation = intent.getIntExtra(EXTRA_TRANSLATION_TOP, 0);
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

		final View panelBottom = findViewById(R.id.panel_bottom);
		final int topBarHeight = getResources().getDimensionPixelSize(R.dimen.restaurants_topbar_height);
		final int translationY = topBarHeight - mTopTranslation;

		btnCall.animate().translationYBy(translationY);
		btnCall.animate().alpha(0).start();
		panelBottom.animate().translationYBy(translationY);
		viewMain.animate().translationYBy(translationY);
		AnimationUtils.animateAlpha(panelBottom, false);
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
