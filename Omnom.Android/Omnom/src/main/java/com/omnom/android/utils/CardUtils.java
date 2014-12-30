package com.omnom.android.utils;

import android.text.TextUtils;

import com.omnom.android.utils.utils.StringUtils;

import java.util.Calendar;

/**
 * Created by Ch3D on 29.10.2014.
 */
public class CardUtils {

	public static String maskPan(final String pan) {
		if (pan == null) {
			return null;
		}
		final int skipFromBeginning = 6;
		final int skipToEnd = 4;
		final String maskSign = ".";
		final int groupCount = 4;
		final String panWithoutSpaces = pan.replace(StringUtils.WHITESPACE, StringUtils.EMPTY_STRING);
		if (TextUtils.isEmpty(pan) || pan.length() < skipFromBeginning + skipToEnd) {
			return pan;
		}
		final String start = panWithoutSpaces.substring(0, skipFromBeginning);
		final String end = panWithoutSpaces.substring(panWithoutSpaces.length() - skipToEnd, panWithoutSpaces.length());
		final String middle = panWithoutSpaces.substring(skipFromBeginning, panWithoutSpaces.length() - skipToEnd).replaceAll(".", maskSign);
		final String maskedPan = start + middle + end;
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < maskedPan.length(); i++) {
			stringBuilder.append(maskedPan.charAt(i));
			if ((i + 1) % groupCount == 0 && i != maskedPan.length() - 1) {
				stringBuilder.append(StringUtils.WHITESPACE);
			}
		}

		return stringBuilder.toString();
	}

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

	public static String prepareExpDate(final String s) {
		final int i = Calendar.getInstance().get(Calendar.YEAR);
		final String yearPrefix = String.valueOf(i).substring(0, 2);
		return new String(s.substring(0, 2)) + "." + yearPrefix + new String(s.substring(3, 5));
	}

}
