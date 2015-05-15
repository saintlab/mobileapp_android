package com.omnom.android.entrance;

import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.restaurateur.model.restaurant.Settings;

/**
 * Created by Ch3D on 13.05.2015.
 */
public class EntranceDataFactory {
	public static EntranceData create(Restaurant restaurant) {
		if(restaurant == null || restaurant.settings() == null) {
			return TableEntranceData.create();
		}

		final Settings settings = restaurant.settings();
		final boolean hasBar = settings.hasBar();
		final boolean hasNonBar = settings.hasPreOrder() || settings.hasTableOrder();
		if(RestaurantHelper.isBar(restaurant) || (hasBar && !hasNonBar)) {
			return BarEntranceData.create();
		}

		if(RestaurantHelper.isTakeAway(restaurant)) {
			return TakeawayEntranceData.create();
		}

		return TableEntranceData.create();
	}
}
