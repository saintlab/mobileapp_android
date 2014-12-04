package com.omnom.android.socket.event;

import com.omnom.android.auth.UserData;
import com.omnom.android.restaurateur.model.order.PaymentData;
import com.omnom.android.restaurateur.model.order.Transaction;
import com.omnom.android.utils.UserHelper;

/**
 * Created by Ch3D on 26.11.2014.
 */
public class PaymentSocketEvent extends BaseSocketEvent {
	private PaymentData mPaymentData;

	public static PaymentSocketEvent createDemoEvent(UserData userData, double amount) {
		final PaymentData data = new PaymentData();
		data.setUser(UserHelper.toPaymentUser(userData));
		data.setTransaction(new Transaction((int) (amount * 100), 0));
		return new PaymentSocketEvent(data);
	}

	public PaymentSocketEvent(final PaymentData paymentData) {
		super();
		mPaymentData = paymentData;
	}

	public PaymentData getPaymentData() {
		return mPaymentData;
	}
}
