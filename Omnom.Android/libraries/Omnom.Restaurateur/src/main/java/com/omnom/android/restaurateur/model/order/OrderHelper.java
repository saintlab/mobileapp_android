package com.omnom.android.restaurateur.model.order;

import android.support.annotation.Nullable;

import com.omnom.android.currency.Currency;
import com.omnom.android.currency.Money;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ch3D on 20.10.2014.
 */
public class OrderHelper {

	@Deprecated
	public static Money getTipsAmount(final @Nullable Order order, int percent, final Currency currency) {
		if(order == null) {
			return Money.getZero(currency);
		}
		return getTipsAmount(order.getAmountToPay(currency), percent);
	}

	/**
	 * Returns a given percent from specified amount
	 *
	 * @param amount  amount to pay
	 * @param percent percent (e.g. 15 for 15%)
	 * @return percent from specified amount
	 */
	public static Money getTipsAmount(final Money amount, final int percent) {
		return amount.getPercent(percent);
	}

	/**
	 * Returns tips value for a given button position.
	 *
	 * @param order    order data
	 * @param amount   amount to pay
	 * @param position tips button position
	 * @return recommended tips for specified button position
	 */
	public static int getTipsValue(final Order order, final Money amount, final int position) {
		if(order.getTips() == null) {
			return 5 * (position + 1);
		}
		List<TipsValue> tipsValues = order.getTips().getValues();
		int tipsValue;
		if(isPercentTips(order, amount)) {
			tipsValue = tipsValues.get(position).getPercent();
		} else {
			final int thresholdIndex = getThresholdIndex(order, amount);
			final int integer = tipsValues.get(position).getAmounts().get(thresholdIndex);
			final Money tip = Money.createFractional(integer, amount.getCurrency());
			tipsValue = tip.getBaseValue().intValue();
		}

		return tipsValue;
	}

	private static int getThresholdIndex(final Order order, final Money amount) {
		List<Integer> thresholds;
		if(order.getTips() == null) {
			thresholds = new ArrayList<Integer>();
			thresholds.add(Integer.MAX_VALUE);
		} else {
			thresholds = order.getTips().getThresholds();
		}
		int thresholdIndex = thresholds.size();
		for(int i = 0; i < thresholds.size(); i++) {
			final int thresholdValue = thresholds.get(i);
			if(amount.isLessOrEquals(Money.createFractional(thresholdValue, amount.getCurrency()))) {
				thresholdIndex = i;
				break;
			}
		}
		return thresholdIndex;
	}

	/**
	 * Checks if tips for specified amount should be displayed in percent.
	 *
	 * @param order  order data
	 * @param amount amount to pay
	 * @return true, if tips should be displayed in percent
	 */
	public static boolean isPercentTips(final @Nullable Order order, final @Nullable Money amount) {
		if(order == null || amount == null || order.getTips() == null) {
			return true;
		}
		List<Integer> thresholds = order.getTips().getThresholds();
		final Integer maxThreshold = thresholds.get(thresholds.size() - 1);
		return amount.isGreatherThan(Money.createFractional(maxThreshold, amount.getCurrency()));
	}
}
