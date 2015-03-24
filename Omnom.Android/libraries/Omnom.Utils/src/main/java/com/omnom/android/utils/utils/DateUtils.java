package com.omnom.android.utils.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mvpotter on 2/7/2015.
 */
public final class DateUtils {

	private DateUtils() {
		throw new UnsupportedOperationException("Unable to create an instance of static class");
	}

	public static String getOnDayPreposition(final Date date) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == Calendar.TUESDAY) {
			return "во";
		} else {
			return "в";
		}
	}

	public static String getDayOfWeek(final Date date) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		final String dayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK,
				Calendar.LONG, AndroidUtils.russianLocale);
		if (dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) {
			return dayName.replaceAll(".$", "у");
		}

		return dayName;
	}

}
