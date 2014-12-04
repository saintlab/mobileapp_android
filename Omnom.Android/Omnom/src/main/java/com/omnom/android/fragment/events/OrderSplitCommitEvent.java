package com.omnom.android.fragment.events;

import java.math.BigDecimal;

/**
 * Created by Ch3D on 12.11.2014.
 */
public class OrderSplitCommitEvent {
	private String mOrderId;

	private BigDecimal mAmount;

	private int mTagSplitType;

	public OrderSplitCommitEvent(String orderId, final BigDecimal amount, final int tagSplitType) {
		mOrderId = orderId;
		mAmount = amount;
		mTagSplitType = tagSplitType;
	}

	public BigDecimal getAmount() {
		return mAmount;
	}

	public String getOrderId() {
		return mOrderId;
	}

	public int getSplitType() {
		return mTagSplitType;
	}
}
