package com.omnom.android.linker.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.utils.AnimationUtils;

import java.util.Collections;

import butterknife.InjectView;

public class SimpleSplashActivity extends BaseActivity {

	@InjectView(R.id.img_logo)
	protected ImageView imgLogo;

	@InjectView(R.id.img_fork)
	protected ImageView imgFork;

	@InjectView(R.id.img_bill)
	protected ImageView imgBill;

	@InjectView(R.id.img_ring)
	protected ImageView imgRing;

	@InjectView(R.id.img_cards)
	protected ImageView imgCards;

	@InjectView(R.id.img_bg)
	protected ImageView imgBackground;

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		final TransitionDrawable transitionDrawable = new TransitionDrawable(
				new Drawable[]{getResources().getDrawable(R.drawable.ic_splash_fork_n_knife),
						getResources().getDrawable(R.drawable.ic_fork_n_knife)});
		transitionDrawable.setCrossFadeEnabled(true);
		findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				AnimationUtils.animateAlpha(imgBill, false);
				AnimationUtils.animateAlpha(imgCards, false);
				AnimationUtils.animateAlpha(imgLogo, false);
				AnimationUtils.animateAlpha(imgRing, false);
				AnimationUtils.translateUp(Collections.singletonList((View) imgFork),
				                           -(int) getResources().getDimension(R.dimen.loader_margin_top),
				                           null);
				AnimationUtils.scale(imgBackground, getResources().getDimensionPixelSize(R.dimen.loader_size), null);
				final int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.loader_logo_size);
				System.err.println(">>> " + dimensionPixelSize);
				AnimationUtils.scaleWidth(imgFork, dimensionPixelSize, 350, null);
				transitionDrawable.startTransition(300);
				postDelayed(1500, new Runnable() {
					@Override
					public void run() {
						boolean hasToken = !TextUtils.isEmpty(getPreferences().getAuthToken(getActivity()));
						final Class<?> cls = hasToken ? ValidationActivity.class : LoginActivity.class;
						Intent intent = new Intent(SimpleSplashActivity.this, cls);
						intent.putExtra(EXTRA_LOADER_ANIMATION, hasToken ? EXTRA_LOADER_ANIMATION_SCALE_DOWN :
								EXTRA_LOADER_ANIMATION_SCALE_UP);
						startActivity(intent, R.anim.fake_fade_in, R.anim.fake_fade_out, true);
					}
				});
			}
		}, getResources().getInteger(R.integer.splash_screen_timeout));
		getWindow().setBackgroundDrawableResource(R.drawable.bg_wood);
		imgFork.setImageDrawable(transitionDrawable);
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
