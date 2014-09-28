package com.omnom.android.linker.model.restaurant;

import android.content.Context;
import android.text.TextUtils;

import com.omnom.android.linker.R;
import com.omnom.android.linker.utils.StringUtils;

/**
 * Created by Ch3D on 09.09.2014.
 */
public class RestaurantHelper {

	public static String getAddress(final Context context, final Restaurant restaurant) {
		final Address address = restaurant.getAddress();
		if(address != null) {
			final String floor = !TextUtils.isEmpty(address.getFloor())
					? address.getFloor() + context.getString(R.string.floor_suffix) : StringUtils.EMPTY_STRING;
			return StringUtils.concat(context.getString(R.string.restaurant_address_delimiter),
			                          address.getCity(),
			                          address.getStreet(),
			                          address.getBuilding(),
			                          floor);
		}
		return StringUtils.EMPTY_STRING;
	}

	public static String getLogo(Restaurant restaurant) {
		if(restaurant != null && restaurant.getDecoration() != null) {
			return restaurant.getDecoration().getLogo();
		}
		return StringUtils.EMPTY_STRING;
	}
}