package com.omnom.android.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.adapter.OrdersPagerAdaper;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.utils.activity.BaseFragmentActivity;

import java.util.ArrayList;

import butterknife.InjectView;

public class OrdersActivity extends BaseFragmentActivity {

	public static void start(BaseOmnomActivity activity, ArrayList<Order> orders) {
		final Intent intent = new Intent(activity, OrdersActivity.class);
		intent.putParcelableArrayListExtra(OrdersActivity.EXTRA_ORDERS, orders);
		activity.startActivity(intent);
	}

	@InjectView(R.id.pager)
	protected ViewPager mPager;

	private OrdersPagerAdaper mPagerAdapter;
	private ArrayList<Order> orders = null;

	@Override
	public void initUi() {
		mPagerAdapter = new OrdersPagerAdaper(getSupportFragmentManager(), orders);
		mPager.setAdapter(mPagerAdapter);
	}

	@Override
	protected void handleIntent(Intent intent) {
		orders = intent.getParcelableArrayListExtra(EXTRA_ORDERS);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_orders;
	}
}
