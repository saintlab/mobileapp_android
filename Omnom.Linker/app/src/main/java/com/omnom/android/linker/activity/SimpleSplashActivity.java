package com.omnom.android.linker.activity;

import android.os.Bundle;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;

public class SimpleSplashActivity extends BaseActivity {
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(LoginActivity.class, android.R.anim.fade_in, android.R.anim.fade_out);
			}
		}, getResources().getInteger(R.integer.splash_screen_timeout));
	}

	@Override
	public void initUi() {

	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_simple_splash;
	}
}
