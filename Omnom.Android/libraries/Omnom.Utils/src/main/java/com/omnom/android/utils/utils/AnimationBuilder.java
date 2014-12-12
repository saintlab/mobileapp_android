package com.omnom.android.utils.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.omnom.android.utils.R;

/**
 * Created by Ch3D on 30.07.2014.
 */
public class AnimationBuilder {
	public interface UpdateLisetener {
		public void invoke(ValueAnimator animation);
	}

	public static AnimationBuilder create(Context context, int... values) {
		AnimationBuilder animationBuilder = new AnimationBuilder().ofInt(values);
		animationBuilder.initDefaults(context);
		return animationBuilder;
	}

	private ValueAnimator animator;

	private AnimationBuilder() {
	}

	public void setDuration(long duration) {
		animator.setDuration(duration);
	}

	private void initDefaults(final Context context) {
		animator.setDuration(context.getResources().getInteger(R.integer.default_animation_duration_long));
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
				if (lisetener != null) {
					lisetener.invoke(animation);
				}
			}
		});
		return this;
	}

	public AnimationBuilder onEnd(final Runnable action) {
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (action != null) {
					action.run();
				}
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
}
