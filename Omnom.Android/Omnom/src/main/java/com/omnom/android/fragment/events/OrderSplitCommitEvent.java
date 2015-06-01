package com.omnom.android.fragment.events;

import com.omnom.android.currency.Money;
import com.omnom.android.utils.SparseBooleanArrayParcelable;

/**
 * Created by Ch3D on 12.11.2014.
 */
public class OrderSplitCommitEvent {
	private String mOrderId;

	private int mGuestsCount;

	private SparseBooleanArrayParcelable mStates;

	private Money mAmount;

	private int mTagSplitType;

	public OrderSplitCommitEvent(String orderId, final int guestsCount,
	                             final SparseBooleanArrayParcelable states, final Money amount,
	                             final int tagSplitType) {
		mOrderId = orderId;
		mGuestsCount = guestsCount;
		mStates = states;
		mAmount = amount;
		mTagSplitType = tagSplitType;
	}

	public Money getAmount() {
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

	public int getGuestsCount() {
		return mGuestsCount;
	}
}
