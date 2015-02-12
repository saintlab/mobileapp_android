package com.omnom.android.activity.base;

import android.os.Bundle;

import com.omnom.android.OmnomApplication;
import com.omnom.android.activity.helper.ActivityHelper;
import com.omnom.android.activity.helper.OmnomActivityHelper;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.UserData;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.utils.UserHelper;
import com.omnom.android.utils.activity.BaseFragmentActivity;

import javax.inject.Inject;

import retrofit.http.HEAD;

/**
 * Created by Ch3D on 01.10.2014.
 */
public abstract class BaseOmnomFragmentActivity extends BaseFragmentActivity {

	private static final String TAG = BaseOmnomFragmentActivity.class.getSimpleName();

	@Inject
	protected AuthService authenticator;

	protected ActivityHelper activityHelper;

	private boolean isBusy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityHelper = new OmnomActivityHelper(getActivity(), authenticator);
	}

	@Override
	protected void onStart() {
		super.onStart();
		activityHelper.onStart();
	}

	@Override
	protected void onDestroy() {
		activityHelper.onDestroy();
		getMixPanelHelper().flush();
		super.onDestroy();
	}

	protected UserData getUserData() {
		return UserHelper.getUserData(this);
	}

	public final MixPanelHelper getMixPanelHelper() {
		return OmnomApplication.getMixPanelHelper(this);
	}

	protected boolean isBusy() {
		return isBusy;
	}

	protected void busy(final boolean isBusy) {
		this.isBusy = isBusy;
	}

}
