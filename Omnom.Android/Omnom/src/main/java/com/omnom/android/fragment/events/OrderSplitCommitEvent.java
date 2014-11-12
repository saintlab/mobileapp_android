package com.omnom.android.fragment.events;

import java.math.BigDecimal;

/**
 * Created by Ch3D on 12.11.2014.
 */
public class OrderSplitCommitEvent {
	private String mOrderId;

	private BigDecimal mAmount;

	public OrderSplitCommitEvent(String orderId, final BigDecimal amount) {
		mOrderId = orderId;
		mAmount = amount;
	}

	public BigDecimal getAmount() {
		return mAmount;
	}

	public String getOrderId() {
		return mOrderId;
	}
}
