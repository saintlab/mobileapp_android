package com.omnom.android.socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.omnom.android.socket.event.OrderCloseSocketEvent;
import com.omnom.android.socket.event.OrderCreateSocketEvent;
import com.omnom.android.socket.event.OrderUpdateSocketEvent;
import com.omnom.android.utils.Extras;

/**
 * Created by Ch3D on 22.05.2015.
 */
public class OrderEventBroadcastReceiver extends BroadcastReceiver {

	public interface Listener {
		void onOrderCreateEvent(final OrderCreateSocketEvent event);

		void onOrderUpdateEvent(final OrderUpdateSocketEvent event);

		void onOrderCloseEvent(final OrderCloseSocketEvent event);
	}

	private final Listener mListener;

	public OrderEventBroadcastReceiver(final Listener listener) {
		mListener = listener;
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		if(Extras.ACTION_EVENT_ORDER_CLOSE.equals(intent.getAction())) {
			final OrderCloseSocketEvent parcelableExtra = intent.getParcelableExtra(Extras.ACTION_EVENT_ORDER_CLOSE);
			mListener.onOrderCloseEvent(parcelableExtra);
		} else if(Extras.ACTION_EVENT_ORDER_CREATE.equals(intent.getAction())) {
			final OrderCreateSocketEvent parcelableExtra = intent.getParcelableExtra(Extras.ACTION_EVENT_ORDER_CREATE);
			mListener.onOrderCreateEvent(parcelableExtra);

		} else if(Extras.ACTION_EVENT_ORDER_UPDATE.equals(intent.getAction())) {
			final OrderUpdateSocketEvent parcelableExtra = intent.getParcelableExtra(Extras.ACTION_EVENT_ORDER_UPDATE);
			mListener.onOrderUpdateEvent(parcelableExtra);
		}
	}
}
