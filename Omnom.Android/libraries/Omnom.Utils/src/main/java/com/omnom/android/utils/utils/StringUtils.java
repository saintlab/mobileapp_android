package com.omnom.android.utils.utils;

import android.text.TextUtils;
import android.util.Log;

import java.util.Collection;

import hugo.weaving.DebugLog;

/**
 * Created by Ch3D on 31.07.2014.
 */
public class StringUtils {

	public static final String NEXT_STRING = "\n";

	private static final String TAG = StringUtils.class.getSimpleName();

	public static final String EMPTY_STRING = "";

	public static final String WHITESPACE = " ";

	public static final String NON_BREAKING_WHITESPACE = "\u00A0";

	private static final int METERS_IN_KILOMETER = 1000;

	public static String concat(String delimiter, Collection<String> data) {
		if (data == null || data.isEmpty()) {
			return StringUtils.EMPTY_STRING;
		}
		String[] dataArray = new String[data.size()];
		dataArray = data.toArray(dataArray);
		return concat(delimiter, dataArray);
	}

	public static String concat(String delimiter, String... data) {
		if (data == null || data.length == 0) {
			return StringUtils.EMPTY_STRING;
		}
		final StringBuilder sb = new StringBuilder();
		for(final String item : data) {
			if(!TextUtils.isEmpty(item)) {
				sb.append(TextUtils.isEmpty(item) ? StringUtils.EMPTY_STRING : item + delimiter);
			}
		}
		String string = sb.toString();
		if(string.length() > 0) {
			return string.substring(0, string.length() - delimiter.length());
		}
		return StringUtils.EMPTY_STRING;
	}

	public static String formatCurrency(final String decimalSeparator, final String s) {
		String regexDecimalSeparator = decimalSeparator;
		if (regexDecimalSeparator.equals(".")) {
			regexDecimalSeparator = "\\.";
		}
		return s.indexOf(decimalSeparator) < 0 ? s : s.replaceAll("0*$", "").replaceAll(regexDecimalSeparator + "$", "");
	}

	@DebugLog
	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for(int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static String filterAmount(final String s, final char decimalSymbol) {
		StringBuilder result = new StringBuilder();
		for(int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			if(Character.isDigit(c) || c == decimalSymbol) {
				result.append(c);
			}
		}
		return result.toString();
	}

	public static String filterDigits(final String s) {
		StringBuilder result = new StringBuilder();
		for(int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			if(Character.isDigit(c)) {
				result.append(c);
			}
		}
		return result.toString();
	}

	public static String removeWhitespaces(final String s) {
		return s.replace(WHITESPACE, EMPTY_STRING);
	}

	public static boolean hasDigits(final String s) {
		for(int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			if(Character.isDigit(c)) {
				return true;
			}
		}
		return false;
	}

	public static int toInt(final String str) {
		try {
			return Integer.valueOf(str);
		} catch(NumberFormatException e) {
			Log.e(TAG, "Unable to convert " + str + " to int");
			return 0;
		}
	}

	/**
	 * Formats distance to be displayed in meters or in kilometers if distance is long enough.
	 *
	 * @param distance distance in meters
	 * @return distance in meters or kilometers
	 */
	public static String formatDistance(final double distance) {
		if ((int) distance / METERS_IN_KILOMETER > 0) {
			return String.format("~%.2fкм", (distance / METERS_IN_KILOMETER));
		} else {
			return String.format("~%dм", (int) distance);
		}
	}

	public static String formatOrderItemPrice(final double quantity, final double pricePerItem) {
		final String pricePerItemStr = AmountHelper.format(pricePerItem);
		if (quantity != 1.0) {
			return String.format("%.1f x %s", quantity, pricePerItemStr);
		} else {
			return pricePerItemStr;
		}
	}

}
