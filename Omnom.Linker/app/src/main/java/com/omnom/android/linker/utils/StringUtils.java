package com.omnom.android.linker.utils;

import android.text.TextUtils;

/**
 * Created by Ch3D on 31.07.2014.
 */
public class StringUtils {
	public static final String EMPTY_STRING = "";

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
}
