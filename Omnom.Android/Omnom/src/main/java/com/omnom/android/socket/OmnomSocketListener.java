package com.omnom.android.socket;

import com.omnom.android.socket.event.PaymentSocketEvent;

/**
 * Created by Ch3D on 26.11.2014.
 */
public interface OmnomSocketListener {
	public void onPayment(PaymentSocketEvent event);

	void onHandshake(boolean success);
}
