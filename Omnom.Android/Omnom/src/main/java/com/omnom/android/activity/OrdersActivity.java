package com.omnom.android.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.adapter.OrdersPagerAdaper;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.utils.activity.BaseFragmentActivity;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.view.OrdersViewPager;
import com.omnom.android.view.ViewPagerIndicatorCircle;

import java.util.ArrayList;

import butterknife.InjectView;

public class OrdersActivity extends BaseFragmentActivity {

	public static final int REQUEST_CODE_CARDS = 100;

	public static final int OFFSCREEN_PAGE_LIMIT = 3;

	public static final String TAG_ANDROID_SWITCHER = "android:switcher:";

	public static final String TAG_SWITCHER_DELIMITER = ":";

	public static void start(BaseOmnomActivity activity, ArrayList<Order> orders, final String bgColor) {
		final Intent intent = new Intent(activity, OrdersActivity.class);
		intent.putParcelableArrayListExtra(OrdersActivity.EXTRA_ORDERS, orders);
		intent.putExtra(OrdersActivity.EXTRA_ACCENT_COLOR, bgColor);
		activity.startActivity(intent);
	}

	@InjectView(R.id.pager)
	protected OrdersViewPager mPager;

	@InjectView(R.id.pager_indicator)
	protected ViewPagerIndicatorCircle mIndicator;

	@InjectView(R.id.txt_info)
	protected TextView mTextInfo;

	@InjectView(R.id.root)
	protected View rootView;

	private OrdersPagerAdaper mPagerAdapter;

	private ArrayList<Order> orders = null;

	private int bgColor;

	private int margin;

	@Override
	public void initUi() {
		mPagerAdapter = new OrdersPagerAdaper(getSupportFragmentManager(), orders, bgColor);
		mPager.setAdapter(mPagerAdapter);
		margin = -(int) (((float) getResources().getDisplayMetrics().widthPixels * OrderFragment.FRAGMENT_SCALE_RATIO_SMALL) / 6);
		mPager.setPageMargin(margin);
		mPager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);
		mIndicator.setViewPager(mPager);
		mPager.setOnPageChangeListener(mIndicator);
		mTextInfo.setText(getString(R.string.your_has_n_orders, mPagerAdapter.getCount()));
		final Drawable background = getWindow().getDecorView().getBackground();
		background.setColorFilter(bgColor, PorterDuff.Mode.MULTIPLY);
		background.invalidateSelf();
	}

	@Override
	protected void handleIntent(Intent intent) {
		orders = intent.getParcelableArrayListExtra(EXTRA_ORDERS);
		final String colorStr = intent.getStringExtra(EXTRA_ACCENT_COLOR);
		bgColor = RestaurantHelper.getBackgroundColor(colorStr);
	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if(requestCode == REQUEST_CODE_CARDS && resultCode == RESULT_OK) {
			getActivity().finish();
		}
	}

	@Override
	public void onBackPressed() {
		final OrderFragment currentFragment = (OrderFragment) mPagerAdapter.getCurrentFragment();
		if(currentFragment != null) {
			if(!currentFragment.isInSplitMode() && !currentFragment.isDownscaled() && !currentFragment.isInPickerMode()) {
				if(mPagerAdapter.getCount() == 1) {
					super.onBackPressed();
					return;
				}
				mPager.setEnabled(true);
				showOther(mPager.getCurrentItem(), true);
				currentFragment.downscale();
				AnimationUtils.animateAlpha(mTextInfo, true);
				AnimationUtils.animateAlpha(mIndicator, true);
				return;
			} else {
				if(!currentFragment.onBackPressed()) {
					super.onBackPressed();
				}
			}
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_orders;
	}

	public boolean checkFragment(final OrderFragment orderFragment) {
		return orderFragment != null && orderFragment == mPagerAdapter.getCurrentFragment();
	}

	public ObjectAnimator getFragmentAnimation(int pos, boolean show) {
		final OrderFragment fragment = (OrderFragment) getSupportFragmentManager().findFragmentByTag(
				TAG_ANDROID_SWITCHER + mPager.getId() + TAG_SWITCHER_DELIMITER + mPagerAdapter.getItemId(pos));
		if(fragment != null) {
			final View view = fragment.getFragmentView();
			final int startAlpha = show ? 0 : 1;
			final int endAlpha = show ? 1 : 0;
			return ObjectAnimator.ofFloat(view, View.ALPHA, startAlpha, endAlpha);
		}
		return null;
	}

	public void showOther(int position, final boolean visible) {
		mPager.setEnabled(visible);
		AnimationUtils.animateAlpha(mTextInfo, visible);
		AnimationUtils.animateAlpha(mIndicator, visible);
		final ObjectAnimator fl = getFragmentAnimation(position - 1, visible);
		final ObjectAnimator fr = getFragmentAnimation(position + 1, visible);
		if(fl != null && fr != null) {
			final AnimatorSet as = new AnimatorSet();
			as.playTogether(fl, fr);
			as.start();
		} else if(fl != null) {
			fl.start();
		} else if(fr != null) {
			fr.start();
		}
	}
}
