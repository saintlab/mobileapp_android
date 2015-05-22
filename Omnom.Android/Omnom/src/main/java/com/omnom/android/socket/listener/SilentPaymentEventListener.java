package com.omnom.android.socket.listener;

import android.content.Context;

import com.omnom.android.socket.event.PaymentSocketEvent;
import com.squareup.otto.Subscribe;

/**
 * Created by Ch3D on 02.12.2014.
 * <p/>
 * Similar to #PaymentEventListener but do not show Crouton automatically
 * instead just notifies #PaymentListener
 */
public class SilentPaymentEventListener extends PaymentEventListener {
	public interface PaymentListener {
		void onPaymentEvent(PaymentSocketEvent event);
	}

	private PaymentListener mListener;

	public SilentPaymentEventListener(final Context context, PaymentListener listener) {
		super(context);
		mListener = listener;
	}

	@Subscribe
	public void onPaymentEvent(final PaymentSocketEvent event) {
		if(mListener != null) {
			mListener.onPaymentEvent(event);
		}
	}
}
