package com.omnom.android.linker.utils;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by Ch3D on 29.07.2014.
 */
public class AnimationUtils {
	public static final int DURATION_LONG  = 1000;
	public static final int DURATION_SHORT = 350;

	public static void animateAlpha(final View view, final boolean visible) {
		final Boolean tag = (Boolean) view.getTag();
		if(tag != null && tag == visible) {
			// skip
			return;
		}

		if(visible) {
			ViewUtils.setVisible(view, visible);
		}
		view.setAlpha(visible ? 0 : 1);
		view.setTag(visible);
		view.animate().setDuration(DURATION_SHORT).
				setInterpolator(new AccelerateDecelerateInterpolator()).
				alpha(visible ? 1 : 0).start();
	}
}
