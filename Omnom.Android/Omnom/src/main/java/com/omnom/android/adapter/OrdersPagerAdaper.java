package com.omnom.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.restaurateur.model.order.Order;

import java.util.List;

/**
 * Created by Ch3D on 10.10.2014.
 */
public class OrdersPagerAdaper extends FragmentStatePagerAdapter {
	private final List<Order> mOrders;

	private int mBgColor;

	private Fragment mCurrentFragment;

	private int mLastAnimated = -1;

	public OrdersPagerAdaper(FragmentManager fm, List<Order> orders, final int bgColor) {
		super(fm);
		mOrders = orders;
		mBgColor = bgColor;
	}

	public Fragment getCurrentFragment() {
		return mCurrentFragment;
	}

	@Override
	public int getItemPosition(final Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public void setPrimaryItem(final ViewGroup container, final int position, final Object object) {
		if(getCurrentFragment() != object) {
			mCurrentFragment = ((Fragment) object);
		}
		super.setPrimaryItem(container, position, object);
	}

	@Override
	public Fragment getItem(int position) {
		final boolean isAnimate = (position > mLastAnimated) && (mLastAnimated < 2) && (position < 2);
		final Fragment fragment = OrderFragment.newInstance(mOrders.get(position), mBgColor, position, isAnimate);
		if(isAnimate) {
			mLastAnimated = position;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return mOrders.size();
	}
}
