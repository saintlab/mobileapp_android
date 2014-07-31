package com.omnom.android.linker.activity.base;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;

import com.omnom.android.linker.R;

import butterknife.ButterKnife;

/**
 * Created by Ch3D on 31.07.2014.
 */
public class ActivityHelper {
	private OmnomActivity mActivity;

	public ActivityHelper(OmnomActivity activity) {
		this.mActivity = activity;
	}

	public void onPostCreate() {
		ButterKnife.inject(mActivity.getActivity());
		mActivity.initUi();
	}

	public void startActivity(Class<?> cls) {
		Intent intent = new Intent(mActivity.getActivity(), cls);
		if (Build.VERSION.SDK_INT >= 16) {
			ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(mActivity.getActivity(), R.anim.fade_in, R.anim.fake_fade_out);
			mActivity.getActivity().startActivity(intent, activityOptions.toBundle());
			mActivity.getActivity().finish();
		} else {
			mActivity.getActivity().finish();
			mActivity.getActivity().startActivity(intent);
		}
	}
}
