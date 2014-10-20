package com.omnom.android.restaurateur.model.order;

/**
 * Created by Ch3D on 20.10.2014.
 */
public class OrderHelper {
	public static int getTipsAmount(final Order order, int percent) {
		return getTipsAmount(order.getAmountToPay(), percent);
	}

	public static int getTipsAmount(final double amount, final int percent) {
		return (int) Math.round(percent * (amount / 100));
	}

	public static boolean isPercentTips(final Order order) {
		return order.getAmountToPay() > order.getTips().getThreshold();
	}

	public static boolean isPercentTips(final Order order, final double amount) {
		return amount >= order.getTips().getThreshold();
	}
}
