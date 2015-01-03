package com.omnom.android.utils.activity.helper;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

import com.omnom.android.utils.R;
import com.omnom.android.utils.activity.OmnomActivity;

/**
 * Created by Ch3D on 17.11.2014.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class ActivityHelperICS extends ActivityHelperBase {

	ActivityHelperICS(OmnomActivity activity) {
		super(activity);
	}

	@Override
	public void start(final Intent intent) {
		mActivity.getActivity().startActivity(intent);
		mActivity.getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fake_fade_out);
	}

	@Override
	public void start(Class<?> cls, int animIn, int aninOut, boolean finish) {
		final Intent intent = new Intent(mActivity.getActivity(), cls);
		if(finish) {
			mActivity.getActivity().finish();
		}
		mActivity.getActivity().startActivity(intent);
		mActivity.getActivity().overridePendingTransition(animIn, aninOut);
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
	public void startForResult(final Intent intent, final int animIn, final int animOut,int code) {
		mActivity.getActivity().startActivityForResult(intent, code);
		mActivity.getActivity().overridePendingTransition(animIn, animOut);
	}

	@Override
	public void start(Intent intent, int animIn, int aninOut, boolean finish) {
		mActivity.getActivity().startActivity(intent);
		mActivity.getActivity().overridePendingTransition(animIn, aninOut);
		if(finish) {
			mActivity.getActivity().finish();
		}
	}

}
