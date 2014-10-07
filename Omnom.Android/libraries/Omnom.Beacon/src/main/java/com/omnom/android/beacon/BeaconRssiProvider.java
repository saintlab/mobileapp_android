package com.omnom.android.beacon;

import com.omnom.android.restaurateur.model.restaurant.Restaurant;

/**
 * Created by Ch3D on 18.09.2014.
 */
public interface BeaconRssiProvider {
	public void updateRssiThreshold(Restaurant restaurant);
}
