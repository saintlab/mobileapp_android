package com.omnom.android.activity.helper;

import android.app.Activity;
import android.location.Location;

import com.omnom.android.OmnomApplication;
import com.omnom.android.utils.preferences.PreferenceProvider;
import com.omnom.android.utils.utils.LocationUtils;

/**
 * Created by mvpotter on 1/27/2015.
 */
public class OmnomActivityHelper implements ActivityHelper {

	private static final String TAG = OmnomActivityHelper.class.getSimpleName();

	protected final Activity mActivity;

	private Location mCurrentLocation = null;

	public OmnomActivityHelper(Activity activity) {
		this.mActivity = activity;
	}

	@Override
	public PreferenceProvider getPreferences() {
		return OmnomApplication.get(mActivity).getPreferences();
	}

	@Override
	public void onPostCreate() {

	}

	@Override
	public void onStart() {

	}

	@Override
	public void onResume() {

	}

	@Override
	public void onPause() {

	}

	@Override
	public void onStop() {

	}

	@Override
	public void onDestroy() {

	}

	@Override
	public Location getLocation() {
		if (mCurrentLocation == null) {
			mCurrentLocation = LocationUtils.getLastKnownLocation(mActivity);
		}
		return mCurrentLocation;
	}

}
