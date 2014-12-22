package com.omnom.android.utils.utils;

import java.math.BigDecimal;

/**
 * Created by Ch3D on 04.12.2014.
 */
public class AmountHelper {
	public static final int PERCENTAGE = 100;

	public static int toInt(final double value) {
		return (int) (value * PERCENTAGE);
	}

	public static double toDouble(final int value) {
		return ((double) value / PERCENTAGE);
	}

	public static double toDouble(final BigDecimal value) {
		return value.doubleValue() / PERCENTAGE;
	}

	public static String format(final BigDecimal value) {
		return format(value.doubleValue());
	}

	public static String format(final double value) {
		if (value == (long) value) {
			return String.format("%d", (long) value);
		} else {
			return String.format("%.2f", value).replace(',', '.');
		}
	}
}
