package com.omnom.android.restaurateur.model.restaurant;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.omnom.android.restaurateur.R;
import com.omnom.android.restaurateur.model.restaurant.schedule.DailySchedule;
import com.omnom.android.utils.utils.StringUtils;

import java.util.Calendar;

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

	public static String getAddressSmall(final Context context, final Restaurant restaurant) {
		final Address address = restaurant.getAddress();
		if(address != null) {
			return StringUtils.concat(context.getString(R.string.restaurant_address_delimiter),
			                          address.getStreet(),
			                          address.getBuilding());
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

	public static String getOpenedTime(Context context, Restaurant restaurant, int weekDay) {
		final DailySchedule dailySchedule = getDailySchedule(restaurant, weekDay);
		if(dailySchedule.isClosed()) {
			return context.getString(R.string.restaurant_closed);
		}
		return context.getString(R.string.restaurant_schedule_from_till, dailySchedule.getOpenTime(), dailySchedule.getCloseTime());
	}

	/**
	 * weekDay is integer in form of {@code Calendar.MODAY} ,..., {@code Calendar.SUNDAY}
	 *
	 * @see java.util.Calendar#MONDAY
	 * @see java.util.Calendar#SUNDAY
	 */
	public static DailySchedule getDailySchedule(Restaurant restaurant, int weekDay) {
		final Schedules schedules = restaurant.getSchedules();
		if(schedules == null) {
			return DailySchedule.NULL;
		}
		switch(weekDay) {
			case Calendar.MONDAY:
				return schedules.getWorkingSchedule().getMonday();
			case Calendar.TUESDAY:
				return schedules.getWorkingSchedule().getTuesday();
			case Calendar.WEDNESDAY:
				return schedules.getWorkingSchedule().getWednesday();
			case Calendar.THURSDAY:
				return schedules.getWorkingSchedule().getThursday();
			case Calendar.FRIDAY:
				return schedules.getWorkingSchedule().getFriday();
			case Calendar.SATURDAY:
				return schedules.getWorkingSchedule().getSaturday();
			case Calendar.SUNDAY:
				return schedules.getWorkingSchedule().getSunday();

			default:
				return schedules.getWorkingSchedule().getMonday();
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