package com.omnom.android.activity;

import android.app.Activity;

import com.omnom.android.OmnomApplication;
import com.omnom.android.utils.observable.BaseErrorHandler;
import com.omnom.android.utils.utils.UserDataHolder;

/**
 * Created by Ch3D on 06.10.2014.
 */
public abstract class OmnomBaseErrorHandler extends BaseErrorHandler {
	public OmnomBaseErrorHandler(Activity activity) {
		super(activity);
	}

	public OmnomBaseErrorHandler(Activity activity, UserDataHolder dataHolder) {
		super(activity, dataHolder);
	}

	@Override
	protected void onTokenExpired() {
		((OmnomApplication) mActivity.getApplication()).logout();
	}
}
