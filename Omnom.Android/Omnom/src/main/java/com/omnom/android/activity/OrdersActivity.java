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

	public enum OrderPosition {
		FIRST, MIDDLE, LAST
	}

	public static final int REQUEST_CODE_CARDS = 100;

	public static final int PAGE_MARGIN = -120;

	public static final int OFFSCREEN_PAGE_LIMIT = 3;

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
		mPager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);
		mIndicator.setViewPager(mPager);
		mPager.setOnPageChangeListener(mIndicator);
		mTextInfo.setText(getString(R.string.your_has_n_orders, mPagerAdapter.getCount()));
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
				currentFragment.downscale(new Runnable() {
					@Override
					public void run() {
						if(isFirstItem()) {
							restoreMargin(PAGE_MARGIN, OrderPosition.FIRST);
						} else if(isLastItem()) {
							restoreMargin(PAGE_MARGIN, OrderPosition.LAST);
						} else {
							restoreMargin(PAGE_MARGIN, OrderPosition.MIDDLE);
						}
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

	private boolean isLastItem() {return mPager.getCurrentItem() == mPagerAdapter.getCount() - 1;}

	private boolean isFirstItem() {return mPager.getCurrentItem() == 0;}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_orders;
	}

	public void animatePageMargingFirstOrLast(final boolean isFirst) {
		AnimationUtils.animateAlpha(mTextInfo, false);
		AnimationUtils.animateAlpha(mIndicator, false);
		ValueAnimator va = ValueAnimator.ofInt(mPager.getPageMargin(), 0);
		mPager.beginFakeDrag();
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				final Integer animatedValue = (Integer) animation.getAnimatedValue();
				mPager.fakeDragBy(isFirst ? -animatedValue * 2 : animatedValue * 2);
				mPager.setPageMargin(animatedValue);
			}
		});
		va.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(final Animator animation) {
				mPager.endFakeDrag();
			}
		});
		va.start();
		mPager.setEnabled(false);
	}

	public void animatePageMarginMiddle() {
		AnimationUtils.animateAlpha(mTextInfo, false);
		AnimationUtils.animateAlpha(mIndicator, false);
		ValueAnimator va = ValueAnimator.ofInt(mPager.getPageMargin(), 0);
		mPager.beginFakeDrag();
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public int mLastValue = mPager.getPageMargin();

			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				final Integer animatedValue = (Integer) animation.getAnimatedValue();
				mPager.setPageMargin(animatedValue);
				final int xOffset = (animatedValue - mLastValue);
				mPager.fakeDragBy(xOffset);
				mLastValue = animatedValue;
			}
		});
		va.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(final Animator animation) {
				mPager.endFakeDrag();
			}
		});
		va.start();
		mPager.setEnabled(false);
	}

	public void restoreMargin(final int value, final OrderPosition pos) {
		AnimationUtils.animateAlpha(mTextInfo, true);
		AnimationUtils.animateAlpha(mIndicator, true);
		ValueAnimator va = ValueAnimator.ofInt(mPager.getPageMargin(), value);
		mPager.beginFakeDrag();
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public int mLastValue = 0;

			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				final Integer animatedValue = (Integer) animation.getAnimatedValue();
				mPager.setPageMargin(animatedValue);
				switch(pos) {
					case FIRST:
						mPager.fakeDragBy(-(animatedValue - mLastValue));
						break;

					case MIDDLE:
						mPager.fakeDragBy(animatedValue - mLastValue);
						break;

					case LAST:
						mPager.fakeDragBy(animatedValue);
						break;
				}
				mLastValue = animatedValue;
			}
		});
		va.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(final Animator animation) {
				mPager.endFakeDrag();
			}
		});
		va.start();
		mPager.setEnabled(true);
	}

	public boolean checkFragment(final OrderFragment orderFragment) {
		return orderFragment != null && orderFragment == mPagerAdapter.getCurrentFragment();
	}

}
