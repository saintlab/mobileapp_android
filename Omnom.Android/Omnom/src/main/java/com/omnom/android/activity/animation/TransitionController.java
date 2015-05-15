package com.omnom.android.activity.animation;

import android.app.Activity;

import java.lang.ref.WeakReference;

/**
 * Created by Ch3D on 15.05.2015.
 */
public abstract class TransitionController {
	protected final WeakReference<Activity> mActivityRef;

	public TransitionController(final WeakReference<Activity> activityWeakReference) {
		mActivityRef = activityWeakReference;
	}
}
