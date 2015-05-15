package com.omnom.android.activity.animation;

import android.support.v4.app.FragmentActivity;

import java.lang.ref.WeakReference;

/**
 * Created by Ch3D on 15.05.2015.
 */
public class FragmentActivityTransitionController {
	protected final WeakReference<FragmentActivity> mActivityRef;

	public FragmentActivityTransitionController(final WeakReference<FragmentActivity> activityWeakReference) {
		mActivityRef = activityWeakReference;
	}
}
