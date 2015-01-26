package com.omnom.android.activity.base;

import android.location.Location;
import android.os.Bundle;

import com.google.gson.Gson;
import com.omnom.android.OmnomApplication;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.activity.helper.ActivityHelper;
import com.omnom.android.activity.helper.OmnomActivityHelper;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.UserData;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;
import com.omnom.android.utils.UserHelper;
import com.omnom.android.utils.activity.BaseActivity;

import javax.inject.Inject;

/**
 * Created by Ch3D on 01.10.2014.
 */
public abstract class BaseOmnomActivity extends BaseActivity {

	private Gson mGson;

	private boolean isBusy;

	@Inject
	protected AuthService authenticator;

	@Inject
	protected RestaurateurObservableApi api;

	@Inject
	protected Acquiring mAcquiring;

	protected ActivityHelper activityHelper;

	public final MixPanelHelper getMixPanelHelper() {
		return OmnomApplication.getMixPanelHelper(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityHelper = new OmnomActivityHelper(getActivity());
		mGson = new Gson();
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

	protected boolean isBusy() {
		return isBusy;
	}

	protected void busy(final boolean isBusy) {
		this.isBusy = isBusy;
	}

	protected Location getLocation() {
		return activityHelper.getLocation();
	}

}
