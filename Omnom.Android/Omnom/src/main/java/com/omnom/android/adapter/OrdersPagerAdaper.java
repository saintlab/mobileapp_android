package com.omnom.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.restaurateur.model.order.Order;

import java.util.List;

/**
 * Created by Ch3D on 10.10.2014.
 */
public class OrdersPagerAdaper extends FragmentStatePagerAdapter {
	private final List<Order> mOrders;

	public OrdersPagerAdaper(FragmentManager fm, List<Order> orders) {
		super(fm);
		mOrders = orders;
	}

	@Override
	public Fragment getItem(int i) {
		return OrderFragment.newInstance(mOrders.get(i));
	}

	@Override
	public int getCount() {
		return mOrders.size();
	}
}
