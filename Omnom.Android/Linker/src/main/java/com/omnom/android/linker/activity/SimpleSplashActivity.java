package com.omnom.android.linker.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;

public class SimpleSplashActivity extends BaseActivity {
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				boolean hasToken = !TextUtils.isEmpty(getPreferences().getAuthToken(getActivity()));
				final Class<?> cls = hasToken ? ValidationActivity.class : LoginActivity.class;
				Intent intent = new Intent(SimpleSplashActivity.this, cls);
				intent.putExtra(EXTRA_LOADER_ANIMATION, hasToken ? EXTRA_LOADER_ANIMATION_SCALE_DOWN : EXTRA_LOADER_ANIMATION_SCALE_UP);
				startActivity(intent, android.R.anim.fade_in, android.R.anim.fade_out, true);
			}
		}, getResources().getInteger(R.integer.splash_screen_timeout));
	}

	@Override
	public void initUi() {
		final BluetoothManager btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		final BluetoothAdapter adapter = btManager.getAdapter();
		if(adapter != null && !adapter.isEnabled()) {
			adapter.enable();
		}
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_simple_splash;
	}
}
