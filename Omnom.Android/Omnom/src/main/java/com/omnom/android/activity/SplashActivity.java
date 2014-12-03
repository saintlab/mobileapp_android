package com.omnom.android.activity;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.service.bluetooth.BackgroundBleService;
import com.omnom.android.utils.drawable.MultiplyImageView;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationBuilder;
import com.omnom.android.utils.utils.AnimationUtils;

import java.util.ArrayList;

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

	@InjectView(R.id.img_multiply)
	protected MultiplyImageView imgMultiply;

	@InjectView(R.id.img_bg)
	protected ImageView imgBackground;

	private TransitionDrawable transitionDrawable;

	private boolean mAnimate = true;

	private void animateValidation() {
		if (!mAnimate) {
			return;
		}

		final int durationShort = getResources().getInteger(R.integer.default_animation_duration_short);
		final int durationSplash = getResources().getInteger(R.integer.splash_screen_timeout);
		final int animationDuration = getResources().getInteger(R.integer.splash_animation_duration);
		final int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.loader_logo_size);

		findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				AnimationUtils.animateAlpha(imgBill, false, durationShort);
				AnimationUtils.animateAlpha(imgCards, false, durationShort);
				AnimationUtils.animateAlpha(imgLogo, false, durationShort);
				AnimationUtils.animateAlpha(imgRing, false, durationShort);

				animateMultiply();
				transitionDrawable.startTransition(durationShort);

				AnimationUtils.scaleWidth(imgFork, dimensionPixelSize, durationShort, null);
				postDelayed(animationDuration, new Runnable() {
					@Override
					public void run() {
						if (!isFinishing()) {
							ValidateActivity.start(SplashActivity.this, R.anim.fake_fade_in, R.anim.fake_fade_out_instant,
							                       EXTRA_LOADER_ANIMATION_SCALE_DOWN, false);
						}
					}
				});
			}
		}, durationSplash);
		getWindow().setBackgroundDrawableResource(R.drawable.bg_wood);
		imgFork.setImageDrawable(transitionDrawable);
		mAnimate = false;
	}

	private void animateMultiply() {
		final float upperLogoPoint = getResources().getDimension(R.dimen.loader_margin_top);
		final int animationDuration = getResources().getInteger(R.integer.splash_animation_duration);

		final AnimatorSet as = new AnimatorSet();

		ArrayList<View> translateViews = new ArrayList<View>(2);
		translateViews.add(imgFork);

		final AnimationBuilder translationBuilder = AnimationBuilder.create(imgFork, 0, (int) upperLogoPoint);
		translationBuilder.setDuration(animationDuration);
		final ValueAnimator translationAnimator = AnimationUtils.prepareTranslation(translateViews, null, translationBuilder);
		translationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				imgMultiply.setOffset(-(Integer) animation.getAnimatedValue());
			}
		});

		float end = getResources().getDimension(R.dimen.loader_size);
		float start = getResources().getDimension(R.dimen.loader_size_huge);
		AnimationBuilder builder = AnimationBuilder.create(imgMultiply, (int) start, (int) end);
		builder.setDuration(getResources().getInteger(R.integer.splash_animation_duration));
		builder.addListener(new AnimationBuilder.UpdateLisetener() {
			@Override
			public void invoke(ValueAnimator animation) {
				imgMultiply.setRadius((Integer) animation.getAnimatedValue() / 2);
				imgMultiply.invalidate();
			}
		});

		ValueAnimator alphaAnimator = ValueAnimator.ofInt(0, 255);
		alphaAnimator.setDuration(animationDuration);
		alphaAnimator.setInterpolator(new AccelerateInterpolator(2.2f));
		alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				imgMultiply.setFillAlpha((Integer) animation.getAnimatedValue());
			}
		});

		final ValueAnimator multiplyAnimator = builder.build();
		as.playTogether(translationAnimator, alphaAnimator, multiplyAnimator);
		as.start();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Workaround for white loader circle (reproducable from second app run)
//		final GradientDrawable sd = (GradientDrawable) imgBackground.getDrawable();
//		sd.setColor(getResources().getColor(R.color.loader_bg_transparent));
//		sd.invalidateSelf();

//		boolean hasToken = !TextUtils.isEmpty(getPreferences().getAuthToken(getActivity()));
//		if(hasToken) {
		animateValidation();
//		} else {
//			animateLogin();
//		}
	}

	private void animateLogin() {
		if (!mAnimate) {
			return;
		}
		findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				EnteringActivity.start(SplashActivity.this, R.anim.slide_in_right, R.anim.slide_out_left);
			}
		}, getResources().getInteger(R.integer.splash_screen_timeout));
		getWindow().setBackgroundDrawableResource(R.drawable.bg_wood);
		imgFork.setImageDrawable(transitionDrawable);
		mAnimate = false;
	}

	@Override
	public void initUi() {
		imgMultiply.setRadius(getResources().getDimensionPixelSize(R.dimen.loader_size_huge) / 2);

		if (AndroidUtils.isKitKat()) {
			startBleServiceKK();
		} else if (AndroidUtils.isJellyBeanMR2()) {
			startBleServiceJB();
		}

		transitionDrawable = new TransitionDrawable(new Drawable[]{getResources().getDrawable(R.drawable.ic_splash_fork_n_knife),
				getResources().getDrawable(R.drawable.ic_fork_n_knife)});
		transitionDrawable.setCrossFadeEnabled(true);
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void startBleServiceKK() {
		final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		final Intent intent = new Intent(this, BackgroundBleService.class);
		final PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent, 0);
		final long triggerMillis = SystemClock.elapsedRealtime() + (AlarmManager.INTERVAL_FIFTEEN_MINUTES / 15);
		alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerMillis, alarmIntent);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void startBleServiceJB() {
		final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		final Intent intent = new Intent(this, BackgroundBleService.class);
		final PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent, 0);
		final long triggerMillis = SystemClock.elapsedRealtime() + (AlarmManager.INTERVAL_FIFTEEN_MINUTES / 15);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerMillis, alarmIntent);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_splash;
	}
}
