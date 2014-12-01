package com.omnom.android.utils.drawable;

import android.graphics.drawable.Drawable;

/**
 * Created by Ch3D on 01.12.2014.
 */
public class TransitionDrawable extends android.graphics.drawable.TransitionDrawable {
	private final int mDuration;

	private boolean transitioned;

	public TransitionDrawable(final int duration, final Drawable[] layers) {
		super(layers);
		mDuration = duration;
	}

	@Override
	public void startTransition(final int durationMillis) {
		super.startTransition(durationMillis);
		transitioned = true;
	}

	public void startTransition() {
		startTransition(mDuration);
	}

	@Override
	public void resetTransition() {
		super.resetTransition();
		transitioned = false;
	}

	@Override
	public void reverseTransition(final int duration) {
		super.reverseTransition(duration);
		transitioned = false;
	}

	public void reverseTransition() {
		reverseTransition(mDuration);
	}

	public boolean isTransitioned() {
		return transitioned;
	}

}
