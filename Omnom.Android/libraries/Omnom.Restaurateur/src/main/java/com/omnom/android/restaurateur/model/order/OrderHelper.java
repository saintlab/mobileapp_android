package com.omnom.android.restaurateur.model.order;

import java.math.BigDecimal;

/**
 * Created by Ch3D on 20.10.2014.
 */
public class OrderHelper {
	public static int getTipsAmount(final Order order, int percent) {
		return getTipsAmount(BigDecimal.valueOf(order.getAmountToPay()), percent);
	}

	public static int getTipsAmount(final BigDecimal amount, final int percent) {
		// final BigDecimal divide = amount.divide(BigDecimal.valueOf(100), BigDecimal.ROUND_DOWN);
		final double divide = amount.doubleValue() / 100;
		final double v = percent * divide;
		return (int) Math.round(v);
	}

	public static boolean isPercentTips(final Order order) {
		return order.getAmountToPay() > order.getTips().getThreshold();
	}

	// TODO: avoid BigDecimal.doubleValue()
	public static boolean isPercentTips(final Order order, final BigDecimal amount) {
		return amount.doubleValue() >= order.getTips().getThreshold();
	}
}
