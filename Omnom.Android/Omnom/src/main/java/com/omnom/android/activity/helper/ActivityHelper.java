package com.omnom.android.activity.helper;

import android.location.Location;

/**
 * Created by mvpotter on 1/27/2015.
 */
public interface ActivityHelper extends com.omnom.android.utils.activity.helper.ActivityHelper {

	void onApplicationLaunch(ApplicationLaunchListener mListener);
	Location getLocation();

}
