package com.omnom.android.linker.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
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

	private TransitionDrawable transitionDrawable;
	private boolean mAnimate = true;

	private void animateValidation() {
		if(!mAnimate) {
			return;
		}

		final int durationShort = getResources().getInteger(R.integer.default_animation_duration_short);
		final int durationSplash = getResources().getInteger(R.integer.splash_screen_timeout);
		final int animationDuration = getResources().getInteger(R.integer.splash_animation_duration);
		final int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.loader_logo_size);
		final float upperLogoPoint = getResources().getDimension(R.dimen.loader_margin_top);
		final float loaderBgSize = getResources().getDimension(R.dimen.loader_size);

		findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				AnimationUtils.animateAlpha(imgBill, false, durationShort);
				AnimationUtils.animateAlpha(imgCards, false, durationShort);
				AnimationUtils.animateAlpha(imgLogo, false, durationShort);
				AnimationUtils.animateAlpha(imgRing, false, durationShort);
				AnimationUtils.translateUp(Collections.singletonList((View) imgFork), -(int) upperLogoPoint, null, animationDuration);
				AnimationUtils.scale(imgBackground, (int) loaderBgSize, animationDuration, null);
				transitionDrawable.startTransition(durationShort);
				AnimationUtils.scaleWidth(imgFork, dimensionPixelSize, durationShort, null);
				postDelayed(animationDuration, new Runnable() {
					@Override
					public void run() {
						if(!isFinishing()) {
							Intent intent = new Intent(SimpleSplashActivity.this, ValidationActivity.class);
							intent.putExtra(EXTRA_LOADER_ANIMATION, EXTRA_LOADER_ANIMATION_SCALE_DOWN);
							startActivity(intent, R.anim.fake_fade_in_short, R.anim.fake_fade_out_short, true);
						}
					}
				});
			}
		}, durationSplash);
		getWindow().setBackgroundDrawableResource(R.drawable.bg_wood);
		imgFork.setImageDrawable(transitionDrawable);
		mAnimate = false;
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Workaround for white loader circle (reproducable from second app run)
		final GradientDrawable sd = (GradientDrawable) imgBackground.getDrawable();
		final int color = getResources().getColor(R.color.loader_bg_transparent);
		sd.setColors(new int[]{color, color});
		sd.invalidateSelf();

		boolean hasToken = !TextUtils.isEmpty(getPreferences().getAuthToken(getActivity()));
		System.err.println(">>> hasAuthToken = " + hasToken);
		if(hasToken) {
			animateValidation();
		} else {
			animateLogin();
		}
	}

	private void animateLogin() {
		if(!mAnimate) {
			return;
		}
		findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(SimpleSplashActivity.this, LoginActivity.class);
				intent.putExtra(EXTRA_LOADER_ANIMATION, EXTRA_LOADER_ANIMATION_SCALE_UP);
				startActivity(intent, android.R.anim.fade_in, android.R.anim.fade_out, true);
			}
		}, getResources().getInteger(R.integer.splash_screen_timeout));
		getWindow().setBackgroundDrawableResource(R.drawable.bg_wood);
		imgFork.setImageDrawable(transitionDrawable);
		mAnimate = false;
	}

	@Override
	public void initUi() {
		final BluetoothManager btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		final BluetoothAdapter adapter = btManager.getAdapter();
		if(adapter != null && !adapter.isEnabled()) {
			adapter.enable();
		}
		transitionDrawable = new TransitionDrawable(
				new Drawable[]{getResources().getDrawable(R.drawable.ic_splash_fork_n_knife),
						getResources().getDrawable(R.drawable.ic_fork_n_knife)});
		transitionDrawable.setCrossFadeEnabled(true);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_simple_splash;
	}
}
