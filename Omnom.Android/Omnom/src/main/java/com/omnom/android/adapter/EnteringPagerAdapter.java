package com.omnom.android.adapter;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.omnom.android.R;
import com.omnom.android.fragment.EnteringPageFragment;

/**
 * Created by Ch3D on 29.09.2014.
 */
public class EnteringPagerAdapter extends FragmentStatePagerAdapter {
	private static final int COUNT = 3;

	private int[] colors = new int[]{
			Color.parseColor("#aaCE1200"),
			Color.parseColor("#aa089E7D"),
			Color.parseColor("#aa266BA6")
	};

	private int[] icons = new int[]{
			R.drawable.ic_credit_cards,
			R.drawable.ic_intro_bell_ringing,
			R.drawable.ic_intro_split_bill
	};

	private int[] titles = new int[]{
			R.string.entering_pay_with_phone,
			R.string.entering_call_waiter,
			R.string.entering_split_bill
	};

	public EnteringPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}

	@Override
	public int getCount() {
		return COUNT;
	}

	@Override
	public Fragment getItem(int i) {
		return EnteringPageFragment.newInstance(colors[i], icons[i], titles[i]);
	}
}
