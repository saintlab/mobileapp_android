package com.omnom.android.fragment;

import java.math.BigDecimal;

/**
 * Created by Ch3D on 12.11.2014.
 */
public class OrderSplitCommitEvent {
	private BigDecimal mAmount;

	public OrderSplitCommitEvent(final BigDecimal amount) {

		mAmount = amount;
	}

	public BigDecimal getAmount() {
		return mAmount;
	}
}
