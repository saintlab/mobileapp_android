package com.omnom.android.entrance;

import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;

/**
 * Created by Ch3D on 13.05.2015.
 */
public class EntranceDataFactory {
	public static EntranceData create(Restaurant restaurant) {
		if(RestaurantHelper.isBar(restaurant)) {
			return BarEntranceData.create();
		}
		if(RestaurantHelper.isTakeAway(restaurant)) {
			return TakeawayEntranceData.create();
		}
		return TableEntranceData.create();
	}
}
