package com.omnom.android.linker.utils;

import android.text.TextUtils;

import hugo.weaving.DebugLog;

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
}
