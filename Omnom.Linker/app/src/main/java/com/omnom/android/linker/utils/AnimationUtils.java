package com.omnom.android.linker.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import hugo.weaving.DebugLog;

/**
 * Created by Ch3D on 29.07.2014.
 */
public class AnimationUtils {
	public static final long DURATION_LONG  = 1000;
	public static final long DURATION_SHORT = 350;

	public static void animateAlpha(final View view, final boolean visible) {
		animateAlpha(view, visible, null);
	}

	public static void animateAlpha(final View view, final boolean visible, final Runnable callback) {
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
		view.animate().setDuration(DURATION_SHORT).
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

	@DebugLog
	public static void translateUp(final Iterable<View> views, final int translation, final Runnable endCallback) {
		final AnimationBuilder builder = AnimationBuilder.create(0, -translation);
		prepareTranslation(views, endCallback, builder).start();
	}

	@DebugLog
	public static void translateDown(final Iterable<View> views, final int translation, final Runnable endCallback) {
		final AnimationBuilder builder = AnimationBuilder.create(-translation, 0);
		prepareTranslation(views, endCallback, builder).start();
	}

	@DebugLog
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

	@DebugLog
	public static void scaleWidth(final View view, final int width, final Runnable updateCallback, final Runnable endCallback) {
		AnimationBuilder builder = AnimationBuilder.create(view.getMeasuredWidth(), width);
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

	@DebugLog
	public static void scaleHeight(final View view, int height) {
		AnimationBuilder.create(view.getMeasuredHeight(), height).addListener(new AnimationBuilder.UpdateLisetener() {
			@Override
			public void invoke(ValueAnimator animation) {
				view.getLayoutParams().height = (Integer) animation.getAnimatedValue();
				view.requestLayout();
			}
		}).build().start();
	}

}
