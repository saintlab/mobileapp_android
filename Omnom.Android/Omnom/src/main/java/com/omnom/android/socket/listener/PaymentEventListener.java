package com.omnom.android.socket.listener;

import android.content.Context;
import android.content.Intent;

import com.omnom.android.socket.event.OrderCloseSocketEvent;
import com.omnom.android.socket.event.OrderCreateSocketEvent;
import com.omnom.android.socket.event.OrderUpdateSocketEvent;
import com.omnom.android.socket.event.PaymentSocketEvent;
import com.omnom.android.utils.Extras;
import com.squareup.otto.Subscribe;

/**
 * Created by Ch3D on 02.12.2014.
 * <p/>
 * Listen to #PaymentSocketEvent and notifies a user with #Crouton
 */
public class PaymentEventListener extends BaseEventListener {

	public PaymentEventListener(final Context context) {
		super(context);
	}

	@Subscribe
	public void onPaymentEvent(final PaymentSocketEvent event) {
		final Intent intent = new Intent(Extras.ACTION_EVENT_PAYMENT);
		intent.putExtra(Extras.EXTRA_PAYMENT_EVENT, event);
		mContext.sendOrderedBroadcast(intent, null);
	}

	@Subscribe
	public void onOrderUpdateEvent(final OrderUpdateSocketEvent event) {
		System.err.println(">>> onOrderUpdateEvent" + event);
		final Intent intent = new Intent(Extras.ACTION_EVENT_ORDER_UPDATE);
		intent.putExtra(Extras.ACTION_EVENT_ORDER_UPDATE, event);
		mContext.sendBroadcast(intent, null);
	}

	@Subscribe
	public void onOrderCreateEvent(final OrderCreateSocketEvent event) {
		System.err.println(">>> onOrderCreateEvent" + event);
		final Intent intent = new Intent(Extras.ACTION_EVENT_ORDER_CREATE);
		intent.putExtra(Extras.ACTION_EVENT_ORDER_CREATE, event);
		mContext.sendBroadcast(intent, null);
	}

	@Subscribe
	public void onOrderCloseEvent(final OrderCloseSocketEvent event) {
		System.err.println(">>> onOrderCloseEvent" + event);
		final Intent intent = new Intent(Extras.ACTION_EVENT_ORDER_CLOSE);
		intent.putExtra(Extras.ACTION_EVENT_ORDER_CLOSE, event);
		mContext.sendBroadcast(intent, null);
	}
}
