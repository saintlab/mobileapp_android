package com.omnom.android.linker.beacon;

import com.omnom.android.linker.model.restaurant.Restaurant;

/**
 * Created by Ch3D on 18.09.2014.
 */
public interface BeaconRssiProvider {
	public void updateRssiThreshold(Restaurant restaurant);
}
