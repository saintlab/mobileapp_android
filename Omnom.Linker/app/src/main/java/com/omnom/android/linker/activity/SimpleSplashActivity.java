package com.omnom.android.linker.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.utils.StringUtils;

public class SimpleSplashActivity extends BaseActivity {
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(LoginActivity.class, android.R.anim.fade_in, android.R.anim.fade_out);
				boolean hasToken = !TextUtils.isEmpty(getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE).getString(AUTH_TOKEN,
				                                                                                                     StringUtils
						                                                                                                     .EMPTY_STRING));
				startActivity(hasToken ? ValidationActivity.class : LoginActivity.class);
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
