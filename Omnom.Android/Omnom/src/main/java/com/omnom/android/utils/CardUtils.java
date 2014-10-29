package com.omnom.android.utils;

import java.util.Calendar;

/**
 * Created by Ch3D on 29.10.2014.
 */
public class CardUtils {
	public static String preparePan(final String s) {
		StringBuilder result = new StringBuilder();
		final int length = s.length();
		for(int i = 0; i < length; i++) {
			final char c = s.charAt(i);
			if(Character.isDigit(c)) {
				result.append(c);
			}
		}
		return result.toString();
	}

	public static String prepareExpDare(final String s) {
		final int i = Calendar.getInstance().get(Calendar.YEAR);
		final String yearPrefix = String.valueOf(i).substring(0, 2);
		return new String(s.substring(0, 2)) + "." + yearPrefix + new String(s.substring(3, 5));
	}
}
