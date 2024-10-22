package com.omnom.android.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.activity.helper.OmnomActivityHelper;
import com.omnom.android.adapter.OrdersPagerAdapter;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.socket.ActivityPaymentBroadcastReceiver;
import com.omnom.android.socket.OrderEventBroadcastReceiver;
import com.omnom.android.socket.OrderEventIntentFilter;
import com.omnom.android.socket.PaymentEventIntentFilter;
import com.omnom.android.socket.event.OrderCloseSocketEvent;
import com.omnom.android.socket.event.OrderCreateSocketEvent;
import com.omnom.android.socket.event.OrderUpdateSocketEvent;
import com.omnom.android.socket.event.PaymentSocketEvent;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.DialogUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.view.OrdersViewPager;
import com.omnom.android.view.ViewPagerIndicatorCircle;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;

public class OrdersActivity extends BaseOmnomFragmentActivity
		implements OrderEventBroadcastReceiver.Listener, ActivityPaymentBroadcastReceiver.PaymentListener {

	public static final int REQUEST_CODE_CARDS = 100;

	public static final int OFFSCREEN_PAGE_LIMIT = 3;

	public static final String TAG_ANDROID_SWITCHER = "android:switcher:";

	public static final String TAG_SWITCHER_DELIMITER = ":";

	public static void start(BaseOmnomActivity activity, Restaurant restaurant, ArrayList<Order> orders, String requestId,
	                         final String bgColor, int code, boolean isDemo) {
		final Intent intent = getIntent(activity, restaurant, orders, requestId, bgColor, isDemo);
		activity.startActivityForResult(intent, code);
	}

	private static Intent getIntent(final Context context, Restaurant restaurant, final ArrayList<Order> orders, final String requestId,
	                                final String bgColor, final boolean isDemo) {
		final Intent intent = new Intent(context, OrdersActivity.class);
		intent.putParcelableArrayListExtra(OrdersActivity.EXTRA_ORDERS, orders);
		intent.putExtra(OrdersActivity.EXTRA_RESTAURANT, restaurant);
		intent.putExtra(OrdersActivity.EXTRA_REQUEST_ID, requestId);
		intent.putExtra(OrdersActivity.EXTRA_ACCENT_COLOR, bgColor);
		intent.putExtra(OrdersActivity.EXTRA_DEMO_MODE, isDemo);
		return intent;
	}

	public static void start(BaseOmnomFragmentActivity activity, Restaurant restaurant, ArrayList<Order> orders, String requestId,
	                         final String bgColor, int code, boolean isDemo) {
		final Intent intent = getIntent(activity, restaurant, orders, requestId, bgColor, isDemo);
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

	private OrdersPagerAdapter mPagerAdapter;

	private ArrayList<Order> orders = null;

	private String requestId;

	private int bgColor;

	private int margin;

	private boolean mDemo;

	private Restaurant mRestaurant;

	private ActivityPaymentBroadcastReceiver mPaymentReceiver;

	private PaymentEventIntentFilter mPaymentFilter;

	private OrderEventBroadcastReceiver mOrderEventReceiver;

	private OrderEventIntentFilter mOrderEventsFilter;

	private Subscription mPaymentEventsSubscription;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPaymentReceiver = new ActivityPaymentBroadcastReceiver(this, this);
		mOrderEventReceiver = new OrderEventBroadcastReceiver(this);
		mPaymentFilter = new PaymentEventIntentFilter(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
		mOrderEventsFilter = new OrderEventIntentFilter();
	}

	@Override
	public void onOrderCreateEvent(final OrderCreateSocketEvent event) {
		if(orders != null) {
			orders.add(event.getOrder());
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mPagerAdapter.updateOrders(orders);
					mTextInfo.setText(getString(R.string.your_has_n_orders, mPagerAdapter.getCount()));
					final OrderFragment currentFragment = (OrderFragment) mPagerAdapter.getCurrentFragment();
					if(currentFragment != null && !currentFragment.isDownscaled()) {
						showOther(mPager.getCurrentItem(), false);
					}
				}
			});
		}
	}

	@Override
	public void onOrderPaymentEvent(final PaymentSocketEvent event) {
		final Order order = event.getPaymentData().getOrder();
		updateOrder(order, true);
	}

	@Override
	public void onOrderUpdateEvent(final OrderUpdateSocketEvent event) {
		updateOrder(event.getOrder(), false);
	}

	@Override
	public void onOrderCloseEvent(final OrderCloseSocketEvent event) {
		final Order order = event.getOrder();
		final OrderFragment currentFragment = (OrderFragment) mPagerAdapter.getCurrentFragment();
		// Return from current bill view if it is opened
		if(currentFragment != null && order.getId().equals(currentFragment.getOrderId()) &&
				!currentFragment.isDownscaled()) {
			DialogUtils.showDialog(getActivity(), R.string.order_closed, R.string.exit,
			                       new DialogInterface.OnClickListener() {
				                       @Override
				                       public void onClick(final DialogInterface dialog,
				                                           final int which) {
					                       closeOrder(order);
				                       }
			                       });
		} else {
			closeOrder(order);
		}
	}

	private void updateOrder(final Order order, final boolean skipDialog) {
		final int position = replaceOrder(orders, order);
		if(order != null && position >= 0 && mPagerAdapter != null) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mPagerAdapter.updateOrders(orders);
					final Fragment currentFragment = findFragmentByPosition(position);
					if(currentFragment != null) {
						((OrderFragment) currentFragment).onOrderUpdate(order, skipDialog);
					}
				}
			});
		}
	}

	public void closeOrder(final Order order) {
		if(orders != null) {
			final int removedItemIndex = orders.indexOf(order);
			orders.remove(order);
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(orders.size() == 0) {
						close();
						return;
					}
					final int currentItemIndex = mPager.getCurrentItem();
					final OrderFragment currentFragment = (OrderFragment) mPagerAdapter.getCurrentFragment();
					// Return from current bill view if it is opened
					if(currentFragment != null &&
							order.getId().equals(currentFragment.getOrderId()) &&
							!currentFragment.isDownscaled()) {
						mPager.setEnabled(true);
						currentFragment.downscale(false);
					}

					mPagerAdapter.updateOrders(orders, removedItemIndex);
					mPager.setCurrentItem(currentItemIndex >= orders.size() ? orders.size() - 1 : currentItemIndex);
					showOther(mPager.getCurrentItem(), orders.size() > 1);
					mTextInfo.setText(getString(R.string.your_has_n_orders, mPagerAdapter.getCount()));
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
		mPagerAdapter = new OrdersPagerAdapter(getSupportFragmentManager(), orders, requestId, bgColor);
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

		registerReceiver(mPaymentReceiver, mPaymentFilter);
		registerReceiver(mOrderEventReceiver, mOrderEventsFilter);
		mPaymentEventsSubscription = OmnomActivityHelper.processPaymentEvents(getActivity());

		OmnomActivityHelper.processPaymentEvents(getActivity());
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mPaymentReceiver);
		unregisterReceiver(mOrderEventReceiver);

		unsubscribe(mPaymentEventsSubscription);
	}

	@Override
	protected void handleIntent(Intent intent) {
		orders = intent.getParcelableArrayListExtra(EXTRA_ORDERS);
		requestId = intent.getStringExtra(EXTRA_REQUEST_ID);
		mRestaurant = intent.getParcelableExtra(EXTRA_RESTAURANT);
		final String colorStr = intent.getStringExtra(EXTRA_ACCENT_COLOR);
		bgColor = RestaurantHelper.getBackgroundColor(colorStr);
		mDemo = intent.getBooleanExtra(EXTRA_DEMO_MODE, false);
	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == Extras.REQUEST_CODE_LOGIN && resultCode == Activity.RESULT_OK) {
			final OrderFragment currentFragment = (OrderFragment) mPagerAdapter.getCurrentFragment();
			if(currentFragment != null) {
				currentFragment.showCardsActivity();
			}
			return;
		}
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
				currentFragment.downscale(true);
				AnimationUtils.animateAlpha(mTextInfo, true);
				AnimationUtils.animateAlpha(mIndicator, true);
				ViewUtils.setVisibleGone(mBtnClose, true);
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
		ViewUtils.setVisibleGone(mBtnClose, visible);
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

	public Restaurant getRestaurant() {
		return mRestaurant;
	}
}
