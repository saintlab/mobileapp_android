package com.saintlab.android.linker.activity;

import android.app.Activity;

import com.saintlab.android.linker.LinkerApplication;
import com.omnom.android.utils.observable.BaseErrorHandler;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.UserDataHolder;

/**
 * Created by Ch3D on 06.10.2014.
 */
public abstract class LinkerBaseErrorHandler extends BaseErrorHandler {
	public LinkerBaseErrorHandler(Activity activity) {
		super(activity);
	}

	public LinkerBaseErrorHandler(Activity activity, UserDataHolder dataHolder) {
		super(activity, dataHolder);
	}

	@Override
	protected void onTokenExpired() {
		LinkerApplication.get(mActivity).getPreferences().setAuthToken(mActivity, StringUtils.EMPTY_STRING);
		LoginActivity.start(mActivity, mDataHolder);
	}
}