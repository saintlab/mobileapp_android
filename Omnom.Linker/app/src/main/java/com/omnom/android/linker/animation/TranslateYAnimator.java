package com.omnom.android.linker.animation;

import android.animation.ValueAnimator;
import android.view.animation.AnticipateInterpolator;

import static com.omnom.android.linker.utils.AnimationUtils.DURATION_LONG;

/**
 * Created by Ch3D on 29.07.2014.
 */
public class TranslateYAnimator extends ValueAnimator {

	public static ValueAnimator create(int... values) {
		final ValueAnimator translationY = ValueAnimator.ofInt(values);
		translationY.setDuration(DURATION_LONG);
		translationY.setInterpolator(new AnticipateInterpolator());
//		translationY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//			@Override
//			public void onAnimationUpdate(ValueAnimator animation) {
//				progressBar.setTranslationY((Integer) animation.getAnimatedValue());
//				imgLoader.setTranslationY((Integer) animation.getAnimatedValue());
//				imgLogo.setTranslationY((Integer) animation.getAnimatedValue());
//			}
//		});
		return translationY;
	}

}
