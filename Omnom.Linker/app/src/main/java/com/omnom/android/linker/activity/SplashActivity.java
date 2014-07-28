package com.omnom.android.linker.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.omnom.android.linker.R;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				if (Build.VERSION.SDK_INT >= 16) {
					ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(SplashActivity
							                                                                      .this, android.R.anim.fade_in,
					                                                                      android.R.anim.fade_out);
					finish();
					startActivity(new Intent(SplashActivity.this, LoginActivity.class), activityOptions.toBundle());
				} else {
					finish();
					startActivity(new Intent(SplashActivity.this, LoginActivity.class));
				}
			}
		}, 2000);
	}
}
