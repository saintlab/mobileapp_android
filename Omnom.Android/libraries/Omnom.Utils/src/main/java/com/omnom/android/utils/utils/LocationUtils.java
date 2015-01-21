package com.omnom.android.utils.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

/**
 * Created by mvpotter on 1/19/2015.
 */
public class LocationUtils {

	public static LocationManager getLocationManager(final Context context) {
		return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}

	public static Location getLastKnownLocation(final Context context) {
		final LocationManager locationManager = getLocationManager(context);
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return location;
	}

}
