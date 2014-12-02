package com.omnom.android.socket.event;

import com.omnom.android.restaurateur.model.order.PaymentData;

/**
 * Created by Ch3D on 26.11.2014.
 */
public class PaymentSocketEvent extends BaseSocketEvent {
	private PaymentData mPaymentData;

	public PaymentSocketEvent(final PaymentData paymentData) {
		super();
		mPaymentData = paymentData;
	}

	public PaymentData getPaymentData() {
		return mPaymentData;
	}
}
