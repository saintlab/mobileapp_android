package com.omnom.android.utils.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mvpotter on 2/7/2015.
 */
public final class DateUtils {

	public static final String DATE_PATTERN_DDMMYYYY = "dd/MM/yyyy";

	public static final SimpleDateFormat DATE_FORMAT_DDMMYYYY = new SimpleDateFormat(DATE_PATTERN_DDMMYYYY);

	private static final String TAG = DateUtils.class.getSimpleName();

	public static boolean isTomorrow(final Date date) {
		final Calendar calendarDate = Calendar.getInstance();
		calendarDate.setTime(date);
		return isSameDay(calendarDate, getTomorrow());
	}

	public static Calendar getTomorrow() {
		final Calendar calendarTomorrow = Calendar.getInstance();
		calendarTomorrow.add(Calendar.DAY_OF_MONTH, 1);
		return calendarTomorrow;
	}

	public static boolean isSameDay(final Calendar dateCalendar, final Calendar calendarTomorrow) {
		return dateCalendar.get(Calendar.YEAR) == calendarTomorrow.get(Calendar.YEAR) &&
				dateCalendar.get(Calendar.DAY_OF_YEAR) == calendarTomorrow.get(Calendar.DAY_OF_YEAR);
	}

	public static String getWeekday(final Context context, final Date date) {
		return android.text.format.DateUtils.formatDateTime(context, date.getTime(),
		                                                    android.text.format.DateUtils.FORMAT_SHOW_WEEKDAY |
				                                                    android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE);
	}

	public static String getOnDayPreposition(final Date date) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if(dayOfWeek == Calendar.TUESDAY) {
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
		if(dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) {
			return dayName.replaceAll(".$", "у");
		}

		return dayName;
	}

	public static String getDayAndMonth(final Date date) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		final int dayNumber = calendar.get(Calendar.DAY_OF_MONTH);
		final String monthName = calendar.getDisplayName(Calendar.MONTH,
		                                                 Calendar.LONG,
		                                                 AndroidUtils.russianLocale);
		return dayNumber + StringUtils.WHITESPACE + monthName;
	}

	@Nullable
	public static Date parseDate(SimpleDateFormat dateFormat, String s) {
		try {
			return dateFormat.parse(s);
		} catch(ParseException e) {
			Log.e(TAG, "Unable to parse date = " + s, e);
		}
		return null;
	}

	public static Date parseDate(String s) {
		return parseDate(DATE_FORMAT_DDMMYYYY, s);
	}

	private DateUtils() {
		throw new UnsupportedOperationException("Unable to create an instance of static class");
	}

}
