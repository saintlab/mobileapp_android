package com.omnom.android.activity;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.service.bluetooth.BackgroundBleService;
import com.omnom.android.utils.activity.BaseActivity;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationBuilder;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.view.MultiplyImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public class SplashActivity extends BaseOmnomActivity {

	public static final float SCALE_FACTOR_FORK_LARGE = 0.4258f; // 104px / 178px = small_fork / large_fork

	public static final float SCALE_FACTOR_FORK_SMALL = 1 / SCALE_FACTOR_FORK_LARGE;

	@InjectView(R.id.img_logo)
	protected ImageView imgLogo;

	@InjectView(R.id.img_fork)
	protected ImageView imgFork;

	@InjectView(R.id.img_fork_large)
	protected ImageView imgForkLarge;

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

	private int durationSplash;

	private boolean mAnimate = true;

	public static void start(BaseActivity context, int enterAnim, int exitAnim, int durationSplash) {
		final Intent intent = new Intent(context, SplashActivity.class);
		intent.putExtra(EXTRA_DURATION_SPLASH, durationSplash);
		context.start(intent, enterAnim, exitAnim, true);
	}

	private void animateValidation() {
		if(!mAnimate) {
			return;
		}

		final int durationShort = getResources().getInteger(R.integer.default_animation_duration_short);
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
				// transitionDrawable.startTransition(durationShort);

				// AnimationUtils.scaleWidth(imgFork, dimensionPixelSize, durationShort, null);
				postDelayed(animationDuration, new Runnable() {
					@Override
					public void run() {
						if(!isFinishing()) {
							ValidateActivity.start(SplashActivity.this, R.anim.fake_fade_in, R.anim.fake_fade_out_instant,
							                       EXTRA_LOADER_ANIMATION_SCALE_DOWN, false);
						}
					}
				});
			}
		}, durationSplash);
		getWindow().setBackgroundDrawableResource(R.drawable.bg_wood);
		mAnimate = false;
	}

	/**
	 * Animate of fork_n_knife logo and loader
	 */
	private void animateMultiply() {
		final float upperLogoPoint = getResources().getDimension(R.dimen.loader_margin_top) + 4;
		final int animationDuration = getResources().getInteger(R.integer.splash_animation_duration);

		// translating up animation
		final AnimationBuilder translationSmallBuilder = AnimationBuilder.create(getActivity(), 0, (int) upperLogoPoint);
		translationSmallBuilder.setDuration(animationDuration);
		final List<View> views = new ArrayList<View>();
		views.add(imgFork);
		views.add(imgForkLarge);
		final ValueAnimator translationAnimator = AnimationUtils.prepareTranslation(
				views,
				null,
				translationSmallBuilder);
		translationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				imgMultiply.setOffset(-(Integer) animation.getAnimatedValue() + 4);
			}
		});

		// loader/circle downscaling animation
		float end = getResources().getDimension(R.dimen.loader_size);
		float start = getResources().getDimension(R.dimen.loader_size_huge);
		AnimationBuilder builder = AnimationBuilder.create(getActivity(), (int) start, (int) end);
		builder.setDuration(getResources().getInteger(R.integer.splash_animation_duration));
		builder.addListener(new AnimationBuilder.UpdateLisetener() {
			@Override
			public void invoke(ValueAnimator animation) {
				imgMultiply.setRadius((Integer) animation.getAnimatedValue() / 2);
				imgMultiply.invalidate();
			}
		});
		final ValueAnimator multiplyAnimator = builder.build();

		// main color animation
		ValueAnimator alphaAnimator = ValueAnimator.ofInt(0, 255);
		alphaAnimator.setDuration(animationDuration);
		alphaAnimator.setInterpolator(new AccelerateInterpolator(2.2f));
		alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				imgMultiply.setFillAlpha((Integer) animation.getAnimatedValue());
			}
		});

		ValueAnimator alphaAnimtorForkSmall = ValueAnimator.ofFloat(1, 0);
		alphaAnimtorForkSmall.setDuration(animationDuration);
		alphaAnimtorForkSmall.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				imgFork.setAlpha((Float) animation.getAnimatedValue());
			}
		});

		ValueAnimator scaleAnimator = ValueAnimator.ofFloat(SCALE_FACTOR_FORK_LARGE, 1.0f);
		scaleAnimator.setDuration(animationDuration);
		scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final float animatedValue = (Float) animation.getAnimatedValue();
				imgForkLarge.setScaleX(animatedValue);
				imgForkLarge.setScaleY(animatedValue);
				final float s = 1 + ((animatedValue - SCALE_FACTOR_FORK_LARGE) * SCALE_FACTOR_FORK_SMALL);
				imgFork.setScaleX(s);
				imgFork.setScaleY(s);
			}
		});

		imgForkLarge.animate().setDuration(animationDuration).translationXBy(7).start();
		imgFork.animate().setDuration(animationDuration).translationXBy(7).start();

		ValueAnimator alphaAnimtorForkLarge = ValueAnimator.ofFloat(0, 1);
		alphaAnimtorForkLarge.setDuration(animationDuration);
		alphaAnimtorForkLarge.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				imgForkLarge.setAlpha((Float) animation.getAnimatedValue());
			}
		});

		// simultaneous animation playback
		final AnimatorSet as = new AnimatorSet();
		as.playTogether(translationAnimator, alphaAnimator, alphaAnimtorForkSmall, alphaAnimtorForkLarge, multiplyAnimator, scaleAnimator);
		as.start();
	}

	@Override
	protected void onResume() {
		super.onResume();

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
				EnteringActivity.start(SplashActivity.this, R.anim.slide_in_right, R.anim.slide_out_left);
			}
		}, getResources().getInteger(R.integer.splash_screen_timeout));
		getWindow().setBackgroundDrawableResource(R.drawable.bg_wood);
		mAnimate = false;
	}

	@Override
	public void initUi() {
		imgForkLarge.setScaleX(0.4258f);
		imgForkLarge.setScaleY(0.4258f);

		imgMultiply.setRadius(getResources().getDimensionPixelSize(R.dimen.loader_size_huge) / 2);

		if(AndroidUtils.isKitKat()) {
			startBleServiceKK();
		} else if(AndroidUtils.isJellyBeanMR2()) {
			startBleServiceJB();
		}
	}

	@Override
	protected void handleIntent(Intent intent) {
		durationSplash = intent.getIntExtra(EXTRA_DURATION_SPLASH, getResources().getInteger(R.integer.splash_screen_timeout));
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