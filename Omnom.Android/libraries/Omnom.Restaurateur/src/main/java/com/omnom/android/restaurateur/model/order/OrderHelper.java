package com.omnom.android.restaurateur.model.order;

import android.support.annotation.Nullable;

import com.omnom.android.utils.utils.AmountHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

	/**
	 * Returns a given percent from specified amount
	 *
	 * @param amount amount to pay
	 * @param percent percent (e.g. 15 for 15%)
	 * @return percent from specified amount
	 */
	public static int getTipsAmount(final BigDecimal amount, final int percent) {
		// final BigDecimal divide = amount.divide(BigDecimal.valueOf(100), BigDecimal.ROUND_DOWN);
		final double divide = AmountHelper.toDouble(amount) * 100;
		final double v = (percent * divide) / 100;
		return (int) Math.round(v);
	}

	/**
	 * Returns tips value for a given button position.
	 *
	 * @param order order data
	 * @param amount amount to pay
	 * @param position tips button position
	 * @return recommended tips for specified button position
	 */
	public static int getTipsValue(final Order order, final BigDecimal amount, final int position) {
		if (order.getTips() == null) {
			return 5 * (position + 1);
		}
		List<TipsValue> tipsValues = order.getTips().getValues();
		int tipsValue;
		if (isPercentTips(order, amount)) {
			tipsValue = tipsValues.get(position).getPercent();
		} else {
			final int thresholdIndex = getThresholdIndex(order, amount);
			tipsValue = (int) Math.round(AmountHelper.toDouble(tipsValues.get(position).getAmounts().get(thresholdIndex)));
		}

		return tipsValue;
	}

	private static int getThresholdIndex(final Order order, final BigDecimal amount) {
		List<Integer> thresholds;
		if (order.getTips() == null) {
			thresholds = new ArrayList<Integer>();
			thresholds.add(Integer.MAX_VALUE);
		} else {
			thresholds = order.getTips().getThresholds();
		}
		int thresholdIndex = thresholds.size();
		for (int i = 0; i < thresholds.size(); i++) {
			final int thresholdValue = (int) Math.round(AmountHelper.toDouble(thresholds.get(i)));
			if (amount.compareTo(BigDecimal.valueOf(thresholdValue)) <= 0 ) {
				thresholdIndex = i;
				break;
			}
		}
		return thresholdIndex;
	}

	/**
	 * Checks if tips for specified amount should be displayed in percent.
	 *
	 * @param order order data
	 * @param amount amount to pay
	 * @return true, if tips should be displayed in percent
	 */
	public static boolean isPercentTips(final @Nullable Order order, final @Nullable BigDecimal amount) {
		if(order == null || amount == null || order.getTips() == null) {
			return true;
		}
		List<Integer> thresholds = order.getTips().getThresholds();
		int maxThreshold = (int) Math.round(AmountHelper.toDouble(thresholds.get(thresholds.size() - 1)));
		return amount.compareTo(BigDecimal.valueOf(maxThreshold)) > 0;
	}
}
