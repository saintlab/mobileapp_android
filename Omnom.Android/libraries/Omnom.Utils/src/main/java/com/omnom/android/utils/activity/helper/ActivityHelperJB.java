package com.omnom.android.utils.activity.helper;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.omnom.android.utils.R;
import com.omnom.android.utils.activity.OmnomActivity;

/**
 * Created by Ch3D on 31.07.2014.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ActivityHelperJB extends ActivityHelperBase {
	ActivityHelperJB(OmnomActivity activity) {
		super(activity);
	}

	@Override
	public void start(final Intent intent) {
		mActivity.getActivity().startActivity(intent, getDefaultOptions());
	}

	private Bundle getDefaultOptions() {
		return ActivityOptions.makeCustomAnimation(mActivity.getActivity(), R.anim.fade_in, R.anim.fake_fade_out).toBundle();
	}

	@Override
	public void startForResult(final Intent intent, final int animIn, final int animOut, int code) {
		ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(mActivity.getActivity(), animIn, animOut);
		mActivity.getActivity().startActivityForResult(intent, code, activityOptions.toBundle());
	}

	@Override
	public void start(Class<?> cls, int animIn, int aninOut, boolean finish) {
		Intent intent = new Intent(mActivity.getActivity(), cls);
		if(Build.VERSION.SDK_INT >= 16) {
			ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(mActivity.getActivity(), animIn, aninOut);
			mActivity.getActivity().startActivity(intent, activityOptions.toBundle());
			if(finish) {
				mActivity.getActivity().finish();
			}
		} else {
			if(finish) {
				mActivity.getActivity().finish();
			}
			mActivity.getActivity().startActivity(intent);
		}
	}

	@Override
	public void start(Intent intent, boolean finish) {
		start(intent, R.anim.fade_in, R.anim.fake_fade_out, finish);
	}

	@Override
	public void start(Intent intent, int animIn, int aninOut) {
		start(intent, animIn, aninOut, false);
	}

	@Override
	public void start(Intent intent, int animIn, int aninOut, boolean finish) {
		ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(mActivity.getActivity(), animIn, aninOut);
		mActivity.getActivity().startActivity(intent, activityOptions.toBundle());
		if(finish) {
			mActivity.getActivity().finish();
		}
	}
}
