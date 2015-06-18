package com.omnom.android.utils.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.omnom.android.utils.R;

import java.util.Collections;

/**
 * Created by Ch3D on 30.07.2014.
 */
public class AnimationBuilder {
	public interface UpdateLisetener {
		public void invoke(ValueAnimator animation);
	}

	public static AnimationBuilder create(Context context, View view, int... values) {
		return create(context, Collections.singleton(view), values);
	}

	public static AnimationBuilder create(Context context, Iterable<View> views, int... values) {
		AnimationBuilder animationBuilder = new AnimationBuilder(views).ofInt(values);
		animationBuilder.initDefaults(context);
		return animationBuilder;
	}

	private final Iterable<View> mViews;

	private ValueAnimator animator;

	private Runnable mEndAction;

	private AnimationBuilder(final Iterable<View> views) {
		mViews = views;
	}

	public void setDuration(long duration) {
		animator.setDuration(duration);
	}

	private void initDefaults(final Context context) {
		animator.setDuration(context.getResources().getInteger(R.integer.default_animation_duration_long));
		animator.setInterpolator(new AccelerateDecelerateInterpolator());
		for(final View view : mViews) {
			view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		}
	}

	public AnimationBuilder ofInt(int... values) {
		animator = ValueAnimator.ofInt(values);
		return this;
	}

	public AnimationBuilder addListener(final UpdateLisetener lisetener) {
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				if(lisetener != null) {
					lisetener.invoke(animation);
				}
			}
		});
		return this;
	}

	public AnimationBuilder onEnd(final Runnable action) {
		mEndAction = action;
		return this;
	}

	public ValueAnimator build() {
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationCancel(final Animator animation) {
				for(final View view : mViews) {
					view.setLayerType(View.LAYER_TYPE_NONE, null);
				}
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				for(final View view : mViews) {
					view.setLayerType(View.LAYER_TYPE_NONE, null);
				}
				if(mEndAction != null) {
					mEndAction.run();
				}
			}
		});
		return animator;
	}

	public AnimationBuilder setInterpolator(Interpolator interpolator) {
		animator.setInterpolator(interpolator);
		return this;
	}
}
