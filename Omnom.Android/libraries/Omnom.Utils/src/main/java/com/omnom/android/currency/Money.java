package com.omnom.android.currency;

/**
 * Created by Ch3D on 26.05.2015.
 */
public class Money {
	public static final Money createFractional(long fractionalAmount, Currency currency) {
		return new Money(fractionalAmount, currency);
	}

	public static final Money create(double amount, Currency currency) {
		return createFractional((long) (amount * currency.getFractionalFactor()), currency);
	}

	private final long mFractionalAmount;

	private final Currency mCurrency;

	private Money(final long fractionalAmount, final Currency currency) {
		mFractionalAmount = fractionalAmount;
		mCurrency = currency;
	}
}
