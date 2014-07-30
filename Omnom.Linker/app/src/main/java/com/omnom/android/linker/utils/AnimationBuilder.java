package com.omnom.android.linker.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by Ch3D on 30.07.2014.
 */
public class AnimationBuilder {
	private ValueAnimator animator;

	private AnimationBuilder() {
	}

	public static AnimationBuilder create(int... values) {
		AnimationBuilder animationBuilder = new AnimationBuilder().ofInt(values);
		animationBuilder.initDefaults();
		return animationBuilder;
	}

	public void setDuration(int duration) {
		animator.setDuration(duration);
	}

	private void initDefaults() {
		animator.setDuration(AnimationUtils.DURATION_LONG);
		animator.setInterpolator(new AccelerateDecelerateInterpolator());
	}

	public AnimationBuilder ofInt(int... values) {
		animator = ValueAnimator.ofInt(values);
		return this;
	}

	public AnimationBuilder addListener(final UpdateLisetener lisetener) {
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				lisetener.invoke(animation);
			}
		});
		return this;
	}

	public AnimationBuilder onEnd(final Action action) {
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				action.invoke();
			}
		});
		return this;
	}

	public ValueAnimator build() {
		return animator;
	}

	public AnimationBuilder setInterpolator(Interpolator interpolator) {
		animator.setInterpolator(interpolator);
		return this;
	}

	public interface Action {
		public void invoke();
	}

	public interface UpdateLisetener {
		public void invoke(ValueAnimator animation);
	}
}