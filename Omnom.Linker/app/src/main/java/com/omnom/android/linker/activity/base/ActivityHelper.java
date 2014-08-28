package com.omnom.android.linker.activity.base;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.omnom.android.linker.LinkerApplication;
import com.omnom.android.linker.R;

/**
 * Created by Ch3D on 31.07.2014.
 */
public class ActivityHelper {
	private OmnomActivity mActivity;

	public ActivityHelper(OmnomActivity activity) {
		this.mActivity = activity;
		LinkerApplication.get(activity).inject(activity);
	}

	public void onPostCreate() {
		mActivity.initUi();
	}

	public void startActivity(final Intent intent, int delay) {
		mActivity.getActivity().findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(intent);
			}
		}, delay);
	}

	public void startActivity(final Intent intent) {
		mActivity.getActivity().startActivity(intent, getDefaultOptions());
	}

	private Bundle getDefaultOptions() {
		return ActivityOptions.makeCustomAnimation(mActivity.getActivity(), R.anim.fade_in, R.anim.fake_fade_out).toBundle();
	}

	public void startActivity(final Class<?> cls, int delay) {
		mActivity.getActivity().findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(cls);
			}
		}, delay);
	}

	public void startActivity(Class<?> cls, int animIn, int aninOut) {
		Intent intent = new Intent(mActivity.getActivity(), cls);
		if(Build.VERSION.SDK_INT >= 16) {
			ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(mActivity.getActivity(), animIn, aninOut);
			mActivity.getActivity().startActivity(intent, activityOptions.toBundle());
			mActivity.getActivity().finish();
		} else {
			mActivity.getActivity().finish();
			mActivity.getActivity().startActivity(intent);
		}
	}

	public void startActivity(Intent intent, int animIn, int aninOut) {
		if(Build.VERSION.SDK_INT >= 16) {
			ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(mActivity.getActivity(), animIn, aninOut);
			mActivity.getActivity().startActivity(intent, activityOptions.toBundle());
			mActivity.getActivity().finish();
		} else {
			mActivity.getActivity().finish();
			mActivity.getActivity().startActivity(intent);
		}
	}

	public void startActivity(Class<?> cls) {
		startActivity(cls, R.anim.fade_in, R.anim.fake_fade_out);
	}
}