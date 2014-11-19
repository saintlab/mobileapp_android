package com.omnom.android.restaurateur.model.restaurant;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.omnom.android.restaurateur.R;
import com.omnom.android.utils.utils.StringUtils;

/**
 * Created by Ch3D on 09.09.2014.
 */
public class RestaurantHelper {

	public static final String COLOR_PREFIX = "#";

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

	public static int getBackgroundColor(Restaurant restaurant) {
		final String decorationBg = restaurant.getDecoration().getBackgroundColor();
		if(!decorationBg.startsWith(COLOR_PREFIX)) {
			return Color.parseColor(COLOR_PREFIX + decorationBg);
		} else {
			return Color.parseColor(decorationBg);
		}
	}

	public static int getBackgroundColor(String decorationBg) {
		if(!decorationBg.startsWith(COLOR_PREFIX)) {
			return Color.parseColor(COLOR_PREFIX + decorationBg);
		} else {
			return Color.parseColor(decorationBg);
		}
	}

	public static boolean isPromoEnabled(final Restaurant restaurant) {
		if(restaurant == null) {
			return false;
		}
		// TODO: Implement when backend will be ready
		return false;
	}

	public static boolean isWaiterEnabled(final Restaurant restaurant) {
		if(restaurant == null) {
			return false;
		}
		// TODO: Implement when backend will be ready
		return false;
	}

	public static String getBackground(final Restaurant restaurant, final DisplayMetrics displayMetrics) {
		if(restaurant != null && restaurant.getDecoration() != null) {
			return restaurant.getDecoration().getBackgroundImage() + "?w=" + displayMetrics.widthPixels;
		}
		return StringUtils.EMPTY_STRING;
	}
}