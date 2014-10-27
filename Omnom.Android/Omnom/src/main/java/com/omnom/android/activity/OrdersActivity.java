package com.omnom.android.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.adapter.OrdersPagerAdaper;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.utils.activity.BaseFragmentActivity;

import java.util.ArrayList;

import butterknife.InjectView;

public class OrdersActivity extends BaseFragmentActivity {

	public static void start(BaseOmnomActivity activity, ArrayList<Order> orders, final String bgColor) {
		final Intent intent = new Intent(activity, OrdersActivity.class);
		intent.putParcelableArrayListExtra(OrdersActivity.EXTRA_ORDERS, orders);
		intent.putExtra(OrdersActivity.EXTRA_ACCENT_COLOR, bgColor);
		activity.startActivity(intent);
	}

	@InjectView(R.id.pager)
	protected ViewPager mPager;

	private OrdersPagerAdaper mPagerAdapter;
	private ArrayList<Order> orders = null;
	private int bgColor;

	@Override
	public void initUi() {
		mPagerAdapter = new OrdersPagerAdaper(getSupportFragmentManager(), orders, bgColor);
		mPager.setAdapter(mPagerAdapter);
	}

	@Override
	protected void handleIntent(Intent intent) {
		orders = intent.getParcelableArrayListExtra(EXTRA_ORDERS);
		final String colorStr = intent.getStringExtra(EXTRA_ACCENT_COLOR);
		bgColor = RestaurantHelper.getBackgroundColor(colorStr);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_orders;
	}
}
