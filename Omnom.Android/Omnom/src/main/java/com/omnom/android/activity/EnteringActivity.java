package com.omnom.android.activity;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.omnom.android.R;
import com.omnom.android.view.ViewPagerIndicatorCircle;
import com.omnom.util.activity.BaseFragmentActivity;

public class EnteringActivity extends BaseFragmentActivity {

	private ViewPager mPager;

	private PagerAdapter mPagerAdapter;
	private ViewPagerIndicatorCircle mPagerIndicator;

	@Override
	public void initUi() {
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new EnteringPagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPagerIndicator = (ViewPagerIndicatorCircle) findViewById(R.id.pager_indicator);
		mPagerIndicator.setViewPager(mPager);
		mPager.setOnPageChangeListener(mPagerIndicator);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_entering;
	}
}
