package com.omnom.android.linker.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.omnom.android.linker.R;
import com.omnom.android.linker.utils.AnimationUtils;

public class SimpleSplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_splash);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				final Intent intent = new Intent(SimpleSplashActivity.this, LoginActivity.class);
				if (Build.VERSION.SDK_INT >= 16) {
					ActivityOptions activityOptions =
							ActivityOptions.makeCustomAnimation(SimpleSplashActivity.this, R.anim.fade_in, R.anim.fake_fade_out);
					startActivity(intent, activityOptions.toBundle());
					finish();
				} else {
					finish();
					startActivity(intent);
				}
			}
		}, AnimationUtils.DURATION_LONG);
	}
}
