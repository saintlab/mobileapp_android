package com.omnom.android.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
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

	public static final int PAGE_MARGIN = -120;

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

	private OrdersPagerAdaper mPagerAdapter;

	private ArrayList<Order> orders = null;

	private int bgColor;

	@Override
	public void initUi() {
		mPagerAdapter = new OrdersPagerAdaper(getSupportFragmentManager(), orders, bgColor);
		mPager.setAdapter(mPagerAdapter);
		mPager.setPageMargin(PAGE_MARGIN);
		mPager.setClipToPadding(true);
		mPager.setClipChildren(true);
		mIndicator.setViewPager(mPager);
		mPager.setOnPageChangeListener(mIndicator);
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
			if(!currentFragment.isDownscaled() && !currentFragment.isInPickerMode()) {
				mPager.setEnabled(true);
				currentFragment.upscale(new Runnable() {
					@Override
					public void run() {
						animatePageMargin(PAGE_MARGIN, null, mPager.getCurrentItem() == 0);
					}
				});
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

	// TODO: Refactoring!!!
	public void fixMarging(Runnable runnable) {
		AnimationUtils.animateAlpha(mTextInfo, false);
		AnimationUtils.animateAlpha(mIndicator, false);
		// animatePageMargin(0, runnable, false);
		animatePageMargin(0, runnable);
		mPager.setEnabled(false);
	}

	// TODO: Refactoring!!!
	private void animatePageMargin(int value, final Runnable endCallback) {
		ValueAnimator va = ValueAnimator.ofInt(mPager.getPageMargin(), value);
		mPager.beginFakeDrag();
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				final Integer animatedValue = (Integer) animation.getAnimatedValue();
				if(endCallback == null) {
					mPager.fakeDragBy(animatedValue);
				}
				mPager.setPageMargin(animatedValue);
			}
		});
		va.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(final Animator animation) {
				mPager.endFakeDrag();
				if(endCallback != null) {
					endCallback.run();
				} else {
					mPager.requestLayout();
				}
			}
		});
		va.start();
	}

	// TODO: Refactoring!!!
	private void animatePageMargin(int value, final Runnable endCallback, final boolean fakeDrag) {
		ValueAnimator va = ValueAnimator.ofInt(mPager.getPageMargin(), value);
		mPager.beginFakeDrag();
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public int mLastValue = 0;

			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				final Integer animatedValue = (Integer) animation.getAnimatedValue();
				if(endCallback == null) {
					mPager.setPageMargin(animatedValue);
					if(fakeDrag) {
						mPager.fakeDragBy(-(animatedValue - mLastValue));
					} else {
						mPager.fakeDragBy(animatedValue - mLastValue);
					}
				} else {
					mPager.fakeDragBy(animatedValue);
					mPager.setPageMargin(animatedValue);
				}
				mLastValue = animatedValue;
			}
		});
		va.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(final Animator animation) {
				mPager.endFakeDrag();
				if(endCallback != null) {
					endCallback.run();
				} else {
					mPager.requestLayout();
				}
			}
		});
		va.start();
	}

	public boolean checkFragment(final OrderFragment orderFragment) {
		return orderFragment != null && orderFragment == mPagerAdapter.getCurrentFragment();
	}
}
