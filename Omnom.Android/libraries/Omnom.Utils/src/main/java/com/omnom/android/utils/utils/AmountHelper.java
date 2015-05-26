package com.omnom.android.utils.utils;

import java.math.BigDecimal;

/**
 * Created by Ch3D on 04.12.2014.
 */
public class AmountHelper {
	public static String getSeparator(final String amount) {
		for(int i = 1; i < amount.length(); i++) {
			final char previous = amount.charAt(i - 1);
			final char current = amount.charAt(i);
			if(!Character.isDigit(previous) && Character.isDigit(current)) {
				return String.valueOf(previous);
			}
		}
		return null;
	}

	private static String format(final BigDecimal value) {
		return format(value.doubleValue());
	}

	private static String format(final double value) {
		if(value == (long) value) {
			return String.format("%d", (long) value);
		} else {
			return String.format("%.2f", value);
		}
	}
}
