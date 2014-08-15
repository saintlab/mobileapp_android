package com.omnom.android.linker.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

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
							callback.run();
						}
					}
				}).alpha(visible ? 1 : 0).start();
	}
}
