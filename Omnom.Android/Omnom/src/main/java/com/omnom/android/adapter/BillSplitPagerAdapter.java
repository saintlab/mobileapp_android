package com.omnom.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.omnom.android.fragment.BillItemsFragment;
import com.omnom.android.fragment.BillSplitPersonsFragment;
import com.omnom.android.fragment.SplitFragment;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.utils.SparseBooleanArrayParcelable;

/**
 * Created by Ch3D on 11.11.2014.
 */
public class BillSplitPagerAdapter extends FragmentStatePagerAdapter {

	public static final int PAGES_COUNT = 2;

	private final SparseBooleanArrayParcelable mStates;

	private Order mOrder;

	private int mGuestsCount;

	private Fragment[] mCurrentFragments = new Fragment[PAGES_COUNT];

	public BillSplitPagerAdapter(final FragmentManager fm, final Order order, final SparseBooleanArrayParcelable states, final int
			guestsCount) {
		super(fm);
		mOrder = order;
		mStates = states;
		mGuestsCount = guestsCount;
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

	public Fragment getCurrentFragment(int pos) {
		if(pos >= PAGES_COUNT) {
			return null;
		}
		return mCurrentFragments[pos];
	}

	@Override
	public void setPrimaryItem(final ViewGroup container, final int position, final Object object) {
		if(getCurrentFragment(position) != object) {
			if(mCurrentFragments[position] != null && object instanceof SplitFragment && mCurrentFragments[position] != object) {
				SplitFragment sf = (SplitFragment) object;
				sf.updateAmount();
			}
			mCurrentFragments[position] = (Fragment) object;
		}
		super.setPrimaryItem(container, position, object);
	}

	@Override
	public void destroyItem(final ViewGroup container, final int position, final Object object) {
		super.destroyItem(container, position, object);
		if(position < PAGES_COUNT) {
			mCurrentFragments[position] = null;
		}
	}

	@Override
	public Fragment getItem(final int position) {
		switch(position) {
			case 0:
				return BillItemsFragment.newInstance(mOrder, mStates);
			case 1:
				return BillSplitPersonsFragment.newInstance(mOrder, mGuestsCount);
		}
		return null;
	}

	@Override
	public int getCount() {
		return PAGES_COUNT;
	}
}
