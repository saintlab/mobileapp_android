package com.omnom.android.activity;

import android.content.Context;

import com.omnom.android.OmnomApplication;
import com.omnom.android.utils.observable.BaseErrorHandler;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.UserDataHolder;

/**
 * Created by Ch3D on 06.10.2014.
 */
public abstract class OmnomBaseErrorHandler extends BaseErrorHandler {
	public OmnomBaseErrorHandler(Context context) {
		super(context);
	}

	public OmnomBaseErrorHandler(Context context, UserDataHolder dataHolder) {
		super(context, dataHolder);
	}

	@Override
	protected void onTokenExpired() {
		OmnomApplication.get(mContext).getPreferences().setAuthToken(mContext, StringUtils.EMPTY_STRING);
		LoginActivity.start(mContext, mDataHolder);
	}
}
