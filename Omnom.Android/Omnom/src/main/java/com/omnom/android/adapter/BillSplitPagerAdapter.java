package com.omnom.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.omnom.android.fragment.BillItemsFragment;
import com.omnom.android.fragment.BillSplitPersonsFragment;
import com.omnom.android.fragment.SplitFragment;
import com.omnom.android.restaurateur.model.order.Order;

/**
 * Created by Ch3D on 11.11.2014.
 */
public class BillSplitPagerAdapter extends FragmentStatePagerAdapter {

	public static final int PAGES_COUNT = 2;

	private Order mOrder;

	private Fragment mCurrentFragment;

	public BillSplitPagerAdapter(final FragmentManager fm, final Order order) {
		super(fm);
		mOrder = order;
	}

	@Override
	public CharSequence getPageTitle(final int position) {
		switch(position) {
			case 0:
				return "По блюдам";
			case 1:
				return "Поровну";
		}
		return super.getPageTitle(position);
	}

	public Fragment getCurrentFragment() {
		return mCurrentFragment;
	}

	@Override
	public void setPrimaryItem(final ViewGroup container, final int position, final Object object) {
		if(getCurrentFragment() != object) {
			if(mCurrentFragment != null && object instanceof SplitFragment && mCurrentFragment != object) {
				SplitFragment sf = (SplitFragment) object;
				sf.updateAmount();
			}
			mCurrentFragment = (Fragment) object;
		}
		super.setPrimaryItem(container, position, object);
	}

	@Override
	public Fragment getItem(final int position) {
		switch(position) {
			case 0:
				return BillItemsFragment.newInstance(mOrder);
			case 1:
				return BillSplitPersonsFragment.newInstance(mOrder);
		}
		return null;
	}

	@Override
	public int getCount() {
		return PAGES_COUNT;
	}
}
