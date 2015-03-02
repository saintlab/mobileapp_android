package com.omnom.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.restaurateur.model.order.Order;

import java.util.List;

/**
 * Created by Ch3D on 10.10.2014.
 */
public class OrdersPagerAdapter extends FragmentPagerAdapter {
	private List<Order> mOrders;

	private int mBgColor;

	private Fragment mCurrentFragment;

	private String mRequestId;

	private int mLastAnimated = -1;

	private int removedItemIndex = -1;

	public OrdersPagerAdapter(FragmentManager fm, List<Order> orders, String requestId, final int bgColor) {
		super(fm);
		mOrders = orders;
		mRequestId = requestId;
		mBgColor = bgColor;
	}

	public Fragment getCurrentFragment() {
		return mCurrentFragment;
	}

	@Override
	public float getPageWidth(final int position) {
		return super.getPageWidth(position);
	}

	@Override
	public int getItemPosition(final Object object) {
		if (object instanceof OrderFragment) {
			final Order order = ((OrderFragment) object).getOrder();
			final int orderIndex = mOrders.indexOf(order);
			if (orderIndex == -1 || removedItemIndex > -1 && removedItemIndex <= orderIndex) {
				return POSITION_NONE;
			} else {
				return POSITION_UNCHANGED;
			}
		}
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
		final boolean isSingle = getCount() == 1;
		final Fragment fragment = OrderFragment.newInstance(mOrders.get(position), mRequestId,
															mBgColor, position, isAnimate && !isSingle, isSingle);
		if(isAnimate) {
			mLastAnimated = position;
		}
		return fragment;
	}

	@Override
	public long getItemId(int position) {
		if (position >= 0 && position < mOrders.size()) {
			return mOrders.get(position).getId().hashCode() + position;
		}
		return position;
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		super.finishUpdate(container);
		removedItemIndex = -1;
	}

	public void updateOrders(final List<Order> orders) {
		updateOrders(orders, -1);
	}

	public void updateOrders(final List<Order> orders, int removedItemIndex) {
		this.removedItemIndex = removedItemIndex;
		mOrders = orders;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mOrders.size();
	}
}
