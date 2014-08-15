package com.omnom.android.linker.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.utils.AnimationUtils;
import com.omnom.android.linker.utils.StringUtils;

public class SimpleSplashActivity extends BaseActivity {
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				boolean hasToken = !TextUtils.isEmpty(getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE).getString(AUTH_TOKEN,
				                                                                                                     StringUtils
						                                                                                                     .EMPTY_STRING));
				startActivity(hasToken ? ValidationActivity.class : LoginActivity.class);
			}
		}, AnimationUtils.DURATION_LONG);
	}

	@Override
	public void initUi() {

	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_simple_splash;
	}
}
