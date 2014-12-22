package com.omnom.android.mixpanel.model;

import com.omnom.android.fragment.OrderFragment;

/**
 * Created by Ch3D on 22.12.2014.
 */
public class PaymentSuccessMixpanelEvent implements MixpanelEvent {

	public static final String EVENT_TITLE = "payment_success";

	private final String orderId;

	private final String tableId;

	private final String restaurantId;

	private final int tip;

	private final String tipsWay;

	private final String splitWay;

	private final int totalAmount;

	private final int tipValue;

	private final int billId;

	public PaymentSuccessMixpanelEvent(final OrderFragment.PaymentDetails details, final int billId) {
		this.billId = billId;
		orderId = details.getOrderId();
		tableId = details.getTableId();
		restaurantId = details.getRestaurantName();
		tip = details.getTip();
		tipValue = details.getTipValue();
		tipsWay = TipsWay.values()[details.getTipsWay()].name();
		splitWay = SplitWay.values()[details.getSplitWay()].name();
		totalAmount = (int) (details.getAmount() * 100);
	}

	@Override
	public String getName() {
		return EVENT_TITLE;
	}
}
