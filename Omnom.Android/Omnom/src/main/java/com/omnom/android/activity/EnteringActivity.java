package com.omnom.android.activity;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.adapter.EnteringPagerAdapter;
import com.omnom.android.view.ViewPagerIndicatorCircle;

import butterknife.InjectView;
import butterknife.OnClick;

public class EnteringActivity extends BaseOmnomFragmentActivity {

	public static void start(BaseOmnomActivity activity, int animIn, int animOut) {
		Intent intent = new Intent(activity, EnteringActivity.class);
		intent.putExtra(EXTRA_LOADER_ANIMATION, EXTRA_LOADER_ANIMATION_SCALE_UP);
		activity.startActivity(intent, animIn, animOut, true);
	}

	@InjectView(R.id.pager)
	protected ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private ViewPagerIndicatorCircle mPagerIndicator;

	@OnClick(R.id.btn_register)
	public void doRegister() {
		final Intent intent = new Intent(this, UserRegisterActivity.class);
		startActivity(intent, R.anim.slide_in_up, R.anim.fake_fade_out_long, false);
	}

	@OnClick(R.id.btn_enter)
	public void doEnter() {
		final Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent, R.anim.slide_in_up, R.anim.fake_fade_out_long, false);
	}

	@Override
	public void initUi() {
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
