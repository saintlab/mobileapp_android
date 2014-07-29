package com.omnom.android.linker.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.omnom.android.linker.R;
import com.omnom.android.linker.utils.AnimationUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.omnom.android.linker.utils.AnimationUtils.DURATION_LONG;
import static com.omnom.android.linker.utils.AnimationUtils.DURATION_SHORT;

public class SplashActivity extends Activity implements View.OnClickListener {
	public static final int PROGRESS_MAX = 100;

	@InjectView(R.id.img_loader)
	protected ImageView imgLoader;

	@InjectView(R.id.img_logo)
	protected ImageView imgLogo;

	@InjectView(R.id.img_logo_left)
	protected ImageView imgLogoLeft;

	@InjectView(R.id.img_logo_right)
	protected ImageView imgLogoRight;

	@InjectView(R.id.progress)
	protected ProgressBar progressBar;

	@InjectView(R.id.stub_login)
	protected ViewStub viewStub;

	private Button btnLogin;

	private int loaderSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		ButterKnife.inject(this);
		loaderSize = getResources().getDimensionPixelSize(R.dimen.loader_size);
	}

	public void performLogin() {
		ValueAnimator valueAnimator = prepareLogoYAnimation(-loaderSize, 0);
		valueAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				findViewById(R.id.view_login_root).setVisibility(View.GONE);

				new CountDownTimer(DURATION_LONG, 10) {
					@Override
					public void onTick(long millisUntilFinished) {
						progressBar.setProgress((int) ((DURATION_LONG - millisUntilFinished) / 10));
					}

					@Override
					public void onFinish() {
						progressBar.setProgress(DURATION_LONG);
						animateEnd();
					}
				}.start();
			}
		});
		valueAnimator.start();
	}

	private void startValidationActivity() {
		final Intent intent = new Intent(this, ValidationActivity.class);
		if (Build.VERSION.SDK_INT >= 16) {
			ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
			startActivity(intent, activityOptions.toBundle());
			finish();
		} else {
			finish();
			startActivity(intent);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			progressBar.postDelayed(new Runnable() {
				@Override
				public void run() {
					animateStart();
				}
			}, DURATION_LONG);
		}
	}

	private void animateEnd() {
		final ValueAnimator widthAnimator = prepareIntValueAnimator(imgLoader.getMeasuredWidth(), imgLoader.getMeasuredWidth() * 10);
		final ValueAnimator heightAnimator = prepareIntValueAnimator(imgLoader.getMeasuredHeight(), imgLoader.getMeasuredHeight() * 10);
		heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				imgLoader.getLayoutParams().height = (Integer) animation.getAnimatedValue();
				imgLoader.requestLayout();
			}
		});
		widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				imgLoader.getLayoutParams().width = (Integer) animation.getAnimatedValue();
				imgLoader.requestLayout();
			}
		});
		widthAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				progressBar.setProgress(PROGRESS_MAX);
				AnimationUtils.animateAlpha(progressBar, false);
				progressBar.postDelayed(new Runnable() {
					@Override
					public void run() {
						startValidationActivity();
					}
				}, DURATION_SHORT);
			}
		});
		widthAnimator.start();
		heightAnimator.start();
	}

	private void animateStart() {
		AnimationUtils.animateAlpha(progressBar, true);
		final ValueAnimator translationY = prepareLogoYAnimation(0, -loaderSize);
		translationY.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				viewStub.setVisibility(View.VISIBLE);
				btnLogin = (Button) findViewById(R.id.btn_login);
				btnLogin.setOnClickListener(SplashActivity.this);
			}
		});

		final ValueAnimator widthAnimator = prepareIntValueAnimator(imgLoader.getMeasuredWidth(), loaderSize);
		final ValueAnimator heightAnimator = prepareIntValueAnimator(imgLoader.getMeasuredHeight(), loaderSize);

		heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				imgLoader.getLayoutParams().height = (Integer) animation.getAnimatedValue();
				imgLoader.requestLayout();
			}
		});
		widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				imgLoader.getLayoutParams().width = (Integer) animation.getAnimatedValue();
				imgLoader.requestLayout();
			}
		});
		widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				if (animation.getCurrentPlayTime() <= 0) {
					return;
				}
				if ((animation.getDuration() / animation.getCurrentPlayTime()) >= 3) {
					translationY.start();
					AnimationUtils.animateAlpha(imgLogoLeft, false);
					AnimationUtils.animateAlpha(imgLogoRight, false);
				}
			}
		});
		// imgLogo.animate().scaleX(1.6f).scaleY(1.6f).setDuration(DURATION_SHORT).start();
		widthAnimator.start();
		heightAnimator.start();
	}

	private ValueAnimator prepareIntValueAnimator(int... values) {
		ValueAnimator widthAnimator = ValueAnimator.ofInt(values);
		widthAnimator.setDuration(DURATION_LONG);
		widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		return widthAnimator;
	}

	private ValueAnimator prepareLogoYAnimation(int... values) {
		final ValueAnimator translationY = ValueAnimator.ofInt(values);
		translationY.setDuration(DURATION_LONG);
		translationY.setInterpolator(new AnticipateInterpolator());
		translationY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				progressBar.setTranslationY((Integer) animation.getAnimatedValue());
				imgLoader.setTranslationY((Integer) animation.getAnimatedValue());
				imgLogo.setTranslationY((Integer) animation.getAnimatedValue());
			}
		});
		return translationY;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_login:
				performLogin();
				break;
		}
	}
}