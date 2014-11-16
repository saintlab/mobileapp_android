package com.omnom.android.restaurateur.model.order;

import android.support.annotation.Nullable;

import java.math.BigDecimal;

/**
 * Created by Ch3D on 20.10.2014.
 */
public class OrderHelper {
	public static int getTipsAmount(final @Nullable Order order, int percent) {
		if(order == null) {
			return 0;
		}
		return getTipsAmount(BigDecimal.valueOf(order.getAmountToPay()), percent);
	}

	public static int getTipsAmount(final BigDecimal amount, final int percent) {
		// final BigDecimal divide = amount.divide(BigDecimal.valueOf(100), BigDecimal.ROUND_DOWN);
		final double divide = amount.doubleValue() / 100;
		final double v = percent * divide;
		return (int) Math.round(v);
	}

	public static boolean isPercentTips(final @Nullable Order order) {
		if(order == null) {
			return false;
		}
		return order.getAmountToPay() > order.getTips().getThreshold();
	}

	// TODO: avoid BigDecimal.doubleValue()
	public static boolean isPercentTips(final @Nullable Order order, final @Nullable BigDecimal amount) {
		if(order == null || amount == null || order.getTips() == null) {
			return false;
		}
		return amount.doubleValue() >= order.getTips().getThreshold();
	}
}
