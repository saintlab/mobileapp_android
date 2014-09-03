package com.omnom.android.linker.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.omnom.android.linker.R;

/**
 * Created by Ch3D on 29.07.2014.
 */
public class AnimationUtils {
//	public static final long DURATION_LONG  = 1000;
//	public static final long DURATION_SHORT = 350;

	public static void animateAlpha(final View view, final boolean visible) {
		animateAlpha(view, visible, view.getResources().getInteger(R.integer.default_animation_duration_short));
	}

	public static void animateAlpha(final View view, final boolean visible, long duration) {
		animateAlpha(view, visible, null, duration);
	}

	public static void animateAlpha(final View view, final boolean visible, final Runnable callback) {
		animateAlpha(view, visible, callback, view.getResources().getInteger(R.integer.default_animation_duration_short));
	}

	public static void animateAlpha(final View view, final boolean visible, final Runnable callback, long duration) {
		final Boolean tag = (Boolean) view.getTag();
		if(tag != null && tag == visible) {
			// skip
			return;
		}

		view.setAlpha(visible ? 0 : 1);
		if(visible) {
			ViewUtils.setVisible(view, visible);
		}
		view.setTag(visible);
		view.animate().setDuration(duration).
				setInterpolator(new AccelerateDecelerateInterpolator()).
				setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						if(callback != null) {
							view.post(callback);
						}
					}
				}).alpha(visible ? 1 : 0).start();
	}

	public static void translateUp(final Iterable<View> views, final int translation, final Runnable endCallback) {
		final AnimationBuilder builder = AnimationBuilder.create(views.iterator().next(), 0, -translation);
		prepareTranslation(views, endCallback, builder).start();
	}

	public static void translateDown(final Iterable<View> views, final int translation, final Runnable endCallback) {
		final AnimationBuilder builder = AnimationBuilder.create(views.iterator().next(), -translation, 0);
		prepareTranslation(views, endCallback, builder).start();
	}

	private static ValueAnimator prepareTranslation(final Iterable<View> views, final Runnable endCallback, AnimationBuilder builder) {
		return builder.addListener(new AnimationBuilder.UpdateLisetener() {
			@Override
			public void invoke(ValueAnimator animation1) {
				for(View v : views) {
					v.setTranslationY((Integer) animation1.getAnimatedValue());
				}
			}
		}).onEnd(endCallback).build();
	}

	public static void scaleWidth(final View view, final int width, final Runnable updateCallback, final Runnable endCallback) {
		AnimationBuilder builder = AnimationBuilder.create(view, view.getMeasuredWidth(), width);
		builder.addListener(new AnimationBuilder.UpdateLisetener() {
			@Override
			public void invoke(ValueAnimator animation) {
				view.getLayoutParams().width = (Integer) animation.getAnimatedValue();
				view.requestLayout();
				if(updateCallback != null) {
					view.post(updateCallback);
				}
			}
		});
		if(endCallback != null) {
			builder.onEnd(endCallback);
		}
		builder.build().start();
	}

	public static void scaleWidth(final View view, final int width, final long duration,
	                              final Runnable endCallback) {
		AnimationBuilder builder = AnimationBuilder.create(view, view.getMeasuredWidth(), width);
		builder.setDuration(duration);
		builder.addListener(new AnimationBuilder.UpdateLisetener() {
			@Override
			public void invoke(ValueAnimator animation) {
				view.getLayoutParams().width = (Integer) animation.getAnimatedValue();
				view.requestLayout();
			}
		});
		if(endCallback != null) {
			builder.onEnd(endCallback);
		}
		builder.build().start();
	}

	public static void scaleHeight(final View view, int height) {
		AnimationBuilder.create(view, view.getMeasuredHeight(), height).addListener(new AnimationBuilder.UpdateLisetener() {
			@Override
			public void invoke(ValueAnimator animation) {
				view.getLayoutParams().height = (Integer) animation.getAnimatedValue();
				view.requestLayout();
			}
		}).build().start();
	}

	public static void scaleHeight(final View view, int height, long duration) {
		AnimationBuilder builder = AnimationBuilder.create(view, view.getMeasuredHeight(), height);
		builder.setDuration(duration);
		builder.addListener(new AnimationBuilder.UpdateLisetener() {
			@Override
			public void invoke(ValueAnimator animation) {
				view.getLayoutParams().height = (Integer) animation.getAnimatedValue();
				view.requestLayout();
			}
		}).build().start();
	}

	public static void scale(final View view, int size, Runnable endCallback) {
		scaleHeight(view, size);
		scaleWidth(view, size, null, endCallback);
	}

	public static void scale(final View view, int size, long duration, Runnable endCallback) {
		scaleHeight(view, size, duration);
		scaleWidth(view, size, duration, endCallback);
	}
}
