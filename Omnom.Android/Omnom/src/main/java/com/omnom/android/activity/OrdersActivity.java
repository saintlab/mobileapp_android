package com.omnom.android.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.adapter.OrdersPagerAdaper;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.socket.event.PaymentSocketEvent;
import com.omnom.android.socket.listener.PaymentEventListener;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.view.OrdersViewPager;
import com.omnom.android.view.ViewPagerIndicatorCircle;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

public class OrdersActivity extends BaseOmnomFragmentActivity {

	public static final int REQUEST_CODE_CARDS = 100;

	public static final int OFFSCREEN_PAGE_LIMIT = 3;

	public static final String TAG_ANDROID_SWITCHER = "android:switcher:";

	public static final String TAG_SWITCHER_DELIMITER = ":";

	public static void start(BaseOmnomActivity activity, ArrayList<Order> orders, String requestId,
	                         final String bgColor, int code, boolean isDemo) {
		final Intent intent = getIntent(activity, orders, requestId, bgColor, isDemo);
		activity.startActivityForResult(intent, code);
	}

	private static Intent getIntent(final Context context, final ArrayList<Order> orders, final String requestId,
	                                final String bgColor, final boolean isDemo) {
		final Intent intent = new Intent(context, OrdersActivity.class);
		intent.putParcelableArrayListExtra(OrdersActivity.EXTRA_ORDERS, orders);
		intent.putExtra(OrdersActivity.EXTRA_REQUEST_ID, requestId);
		intent.putExtra(OrdersActivity.EXTRA_ACCENT_COLOR, bgColor);
		intent.putExtra(OrdersActivity.EXTRA_DEMO_MODE, isDemo);
		return intent;
	}

	public static void start(BaseOmnomFragmentActivity activity, ArrayList<Order> orders, String requestId,
	                         final String bgColor, int code, boolean isDemo) {
		final Intent intent = getIntent(activity, orders, requestId, bgColor, isDemo);
		activity.startActivityForResult(intent, code);
	}

	@InjectView(R.id.pager)
	protected OrdersViewPager mPager;

	@InjectView(R.id.pager_indicator)
	protected ViewPagerIndicatorCircle mIndicator;

	@InjectView(R.id.txt_info)
	protected TextView mTextInfo;

	@InjectView(R.id.btn_close)
	protected TextView mBtnClose;

	@InjectView(R.id.root)
	protected View rootView;

	private OrdersPagerAdaper mPagerAdapter;

	private ArrayList<Order> orders = null;

	private String requestId;

	private int bgColor;

	private int margin;

	private boolean mDemo;

	private PaymentEventListener mPaymentListener;

	@Subscribe
	public void onPayment(final PaymentSocketEvent event) {
		final Order order = event.getPaymentData().getOrder();
		final int position = replaceOrder(orders, order);
		if(order != null && position >= 0 && mPagerAdapter != null) {
			mPagerAdapter.updateOrders(orders);
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					final Fragment currentFragment = findFragmentByPosition(position);
					if(currentFragment != null) {
						((OrderFragment) currentFragment).onPayment(order);
					}
				}
			});
		}
	}

	/**
	 * Returns fragment cached by FragmentPagerAdapter
	 *
	 * @param position fragment position
	 * @return fragment or null if not found
	 */
	public Fragment findFragmentByPosition(int position) {
		Fragment fragment = null;
		if(mPager != null && mPagerAdapter != null) {
			fragment = getSupportFragmentManager().findFragmentByTag(
					"android:switcher:" + mPager.getId() + ":" + mPagerAdapter.getItemId(position));
		}

		return fragment;
	}

	@Override
	public void initUi() {
		mPaymentListener = new PaymentEventListener(this);
		mPagerAdapter = new OrdersPagerAdaper(getSupportFragmentManager(), orders, requestId, bgColor);
		mPager.setAdapter(mPagerAdapter);
		margin = -(int) (((float) getResources().getDisplayMetrics().widthPixels * OrderFragment.FRAGMENT_SCALE_RATIO_SMALL) / 4.5);
		mPager.setPageMargin(margin);
		mPager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);
		mIndicator.setViewPager(mPager);
		mPager.setOnPageChangeListener(mIndicator);
		mTextInfo.setText(getString(R.string.your_has_n_orders, mPagerAdapter.getCount()));
		AndroidUtils.setAccentColor(getWindow(), bgColor);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(orders.size() > 0) {
			final Order order = orders.get(0);
			if(order != null) {
				mPaymentListener.initTableSocket(order.getTableId());
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mPaymentListener.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPaymentListener.onDestroy();
	}

	@Override
	protected void handleIntent(Intent intent) {
		orders = intent.getParcelableArrayListExtra(EXTRA_ORDERS);
		requestId = intent.getStringExtra(EXTRA_REQUEST_ID);
		final String colorStr = intent.getStringExtra(EXTRA_ACCENT_COLOR);
		bgColor = RestaurantHelper.getBackgroundColor(colorStr);
		mDemo = intent.getBooleanExtra(EXTRA_DEMO_MODE, false);
	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if(requestCode == REQUEST_CODE_CARDS && resultCode == RESULT_OK) {
			close();
		}
	}

	@OnClick(R.id.btn_close)
	public void onClose() {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		final OrderFragment currentFragment = (OrderFragment) mPagerAdapter.getCurrentFragment();
		if(currentFragment != null) {
			if(!currentFragment.isInSplitMode() && !currentFragment.isDownscaled() && !currentFragment.isInPickerMode()) {
				if(mPagerAdapter.getCount() == 1) {
					close();
					return;
				}
				mPager.setEnabled(true);
				showOther(mPager.getCurrentItem(), true);
				currentFragment.downscale();
				AnimationUtils.animateAlpha(mTextInfo, true);
				AnimationUtils.animateAlpha(mIndicator, true);
				ViewUtils.setVisible(mBtnClose, true);
				return;
			} else {
				if(!currentFragment.onBackPressed()) {
					close();
				}
			}
		} else {
			close();
		}
	}

	public int getOrdersCount() {
		int count = 0;
		if(mPagerAdapter != null) {
			count = mPagerAdapter.getCount();
		}
		return count;
	}

	public void close() {
		setResult(RESULT_OK);
		finish();
		overridePendingTransition(R.anim.nothing, R.anim.slide_out_up);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_orders;
	}

	public boolean checkFragment(final OrderFragment orderFragment) {
		return orderFragment != null && orderFragment == mPagerAdapter.getCurrentFragment();
	}

	public ObjectAnimator getFragmentAnimation(int pos, boolean show) {
		if(mPagerAdapter != null) {
			final OrderFragment fragment = (OrderFragment) getSupportFragmentManager()
					.findFragmentByTag(TAG_ANDROID_SWITCHER + mPager.getId() + TAG_SWITCHER_DELIMITER + mPagerAdapter.getItemId(pos));
			if(fragment != null) {
				final View view = fragment.getFragmentView();
				final int startAlpha = show ? 0 : 1;
				final int endAlpha = show ? 1 : 0;
				return ObjectAnimator.ofFloat(view, View.ALPHA, startAlpha, endAlpha);
			}
		}
		return null;
	}

	public void showOther(int position, final boolean visible) {
		mPager.setEnabled(visible);
		AnimationUtils.animateAlpha(mTextInfo, visible);
		AnimationUtils.animateAlpha(mIndicator, visible);
		ViewUtils.setVisible(mBtnClose, visible);
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

	public boolean isDemo() {
		return mDemo;
	}

	private int replaceOrder(final List<Order> orders, final Order orderToReplace) {
		int position = -1;
		if(orders == null || orderToReplace == null) {
			return position;
		}
		for(int i = 0; i < orders.size(); i++) {
			if(orders.get(i).getId().equals(orderToReplace.getId())) {
				position = i;
				break;
			}
		}
		if(position >= 0) {
			final Order originalOrder = orders.get(position);
			orderToReplace.setTips(originalOrder.getTips());
			orders.set(position, orderToReplace);
		}

		return position;
	}

}
