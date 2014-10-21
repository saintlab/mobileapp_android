package com.omnom.android.utils.utils;

import android.text.TextUtils;

import hugo.weaving.DebugLog;

/**
 * Created by Ch3D on 31.07.2014.
 */
public class StringUtils {
	public static final String EMPTY_STRING = "";
	public static final String WHITESPACE = " ";

	public static String concat(String delimiter, String... data) {
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

	public static String formatCurrency(double value) {
		return formatCurrency(String.valueOf(value));
	}

	public static String formatCurrency(double value, String currency) {
		return formatCurrency(String.valueOf(value), currency);
	}

	public static String formatCurrency(String s) {
		return s.indexOf(".") < 0 ? s : s.replaceAll("0*$", "").replaceAll("\\.$", "");
	}

	public static String formatCurrency(String s, String currency) {
		if(s.endsWith(currency)) {
			s = s.substring(0, s.length() - 1);
		}
		String result = s.indexOf(".") < 0 ? s : s.replaceAll("0*$", "").replaceAll("\\.$", "");
		return result + currency;
	}

	@DebugLog
	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for(int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static String filterAmount(final String s) {
		StringBuilder result = new StringBuilder();
		for(int i = 0; i < s.length() - 1; i++) {
			final char c = s.charAt(i);
			if(Character.isDigit(c) || c == ',' || c == '.') {
				result.append(c);
			}
		}
		return result.toString();
	}
}
