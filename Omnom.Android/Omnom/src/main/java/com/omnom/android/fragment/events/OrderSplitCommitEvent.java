package com.omnom.android.fragment.events;

import com.omnom.android.utils.SparseBooleanArrayParcelable;

import java.math.BigDecimal;

/**
 * Created by Ch3D on 12.11.2014.
 */
public class OrderSplitCommitEvent {
	private String mOrderId;

	private SparseBooleanArrayParcelable mStates;

	private BigDecimal mAmount;

	private int mTagSplitType;

	public OrderSplitCommitEvent(String orderId, final SparseBooleanArrayParcelable states, final BigDecimal amount, final int tagSplitType) {
		mOrderId = orderId;
		mStates = states;
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

	public SparseBooleanArrayParcelable getStates() {
		return mStates;
	}
}
