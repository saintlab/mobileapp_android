package com.omnom.android.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.utils.utils.AnimationUtils;

import java.util.Collections;

import butterknife.InjectView;

public class SplashActivity extends BaseOmnomActivity {

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
							ValidateActivity.start(SplashActivity.this, R.anim.fake_fade_in_short, R.anim.fake_fade_out_short,
							                       EXTRA_LOADER_ANIMATION_SCALE_DOWN);
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
		sd.setColor(getResources().getColor(R.color.loader_bg_transparent));
		sd.invalidateSelf();

		boolean hasToken = !TextUtils.isEmpty(getPreferences().getAuthToken(getActivity()));
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
				Intent intent = new Intent(SplashActivity.this, EnteringActivity.class);
				intent.putExtra(EXTRA_LOADER_ANIMATION, EXTRA_LOADER_ANIMATION_SCALE_UP);
				startActivity(intent, R.anim.slide_in_right, R.anim.slide_out_left, true);
			}
		}, getResources().getInteger(R.integer.splash_screen_timeout));
		getWindow().setBackgroundDrawableResource(R.drawable.bg_wood);
		imgFork.setImageDrawable(transitionDrawable);
		mAnimate = false;
	}

	@Override
	public void initUi() {
		transitionDrawable = new TransitionDrawable(
				new Drawable[]{getResources().getDrawable(R.drawable.ic_splash_fork_n_knife),
						getResources().getDrawable(R.drawable.ic_fork_n_knife)});
		transitionDrawable.setCrossFadeEnabled(true);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_splash;
	}
}
