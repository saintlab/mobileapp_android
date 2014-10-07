package com.omnom.android.linker.activity;

import android.content.Context;

import com.omnom.android.linker.LinkerApplication;
import com.omnom.android.utils.observable.BaseErrorHandler;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.UserDataHolder;

/**
 * Created by Ch3D on 06.10.2014.
 */
public abstract class LinkerBaseErrorHandler extends BaseErrorHandler {
	public LinkerBaseErrorHandler(Context context) {
		super(context);
	}

	public LinkerBaseErrorHandler(Context context, UserDataHolder dataHolder) {
		super(context, dataHolder);
	}

	@Override
	protected void onTokenExpired() {
		LinkerApplication.get(mContext).getPreferences().setAuthToken(mContext, StringUtils.EMPTY_STRING);
		LoginActivity.start(mContext, mDataHolder);
	}
}