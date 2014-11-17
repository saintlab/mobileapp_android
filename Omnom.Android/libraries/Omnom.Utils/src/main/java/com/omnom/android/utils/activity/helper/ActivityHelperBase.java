package com.omnom.android.utils.activity.helper;

import android.content.Intent;
import android.os.Build;

import com.omnom.android.utils.BaseOmnomApplication;
import com.omnom.android.utils.R;
import com.omnom.android.utils.activity.OmnomActivity;
import com.omnom.android.utils.preferences.PreferenceProvider;

/**
 * Created by Ch3D on 17.11.2014.
 */
public abstract class ActivityHelperBase implements ActivityHelper {

	public static ActivityHelper create(OmnomActivity activity) {
		if(Build.VERSION.SDK_INT >= 16) {
			return new ActivityHelperJB(activity);
		}
		return new ActivityHelperICS(activity);
	}

	protected final OmnomActivity mActivity;

	public ActivityHelperBase(OmnomActivity activity) {
		this.mActivity = activity;
		BaseOmnomApplication.get(activity).inject(activity);
	}

	@Override
	public void onPostCreate() {
		mActivity.initUi();
	}

	@Override
	public PreferenceProvider getPreferences() {
		return BaseOmnomApplication.get(mActivity).getPreferences();
	}

	@Override
	public void start(Class<?> cls) {
		start(cls, R.anim.fade_in, R.anim.fake_fade_out);
	}

	@Override
	public void start(Class<?> cls, boolean finish) {
		start(cls, R.anim.fade_in, R.anim.fake_fade_out, finish);
	}

	@Override
	public void start(Class<?> cls, int animIn, int aninOut) {
		start(cls, animIn, aninOut, true);
	}

	@Override
	public void start(final Class<?> cls, int delay) {
		mActivity.getActivity().findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				start(cls);
			}
		}, delay);
	}

	@Override
	public void start(final Intent intent, int delay) {
		mActivity.getActivity().findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				start(intent);
			}
		}, delay);
	}
}
