package com.omnom.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.adapter.EnteringPagerAdapter;
import com.omnom.android.view.ViewPagerIndicatorCircle;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by mvpotter on 12/3/2014.
 */
public class EnteringFragment extends Fragment {

	@InjectView(R.id.pager)
	protected ViewPager mPager;

	public static EnteringFragment newInstance() {
		return new EnteringFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		OmnomApplication.get(getActivity()).inject(this);
		final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_entering, container, false);
		ButterKnife.inject(this, view);
		PagerAdapter mPagerAdapter = new EnteringPagerAdapter(getActivity().getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		ViewPagerIndicatorCircle mPagerIndicator = (ViewPagerIndicatorCircle) view.findViewById(R.id.pager_indicator);
		mPagerIndicator.setViewPager(mPager);
		mPager.setOnPageChangeListener(mPagerIndicator);
		return view;
	}

}
