package com.omnom.android.socket.listener;

import android.content.Context;

import com.omnom.android.socket.event.OrderCreateSocketEvent;
import com.squareup.otto.Subscribe;

/**
 * Created by michaelpotter on 09/01/15.
 */
public class OrderCreateEventListener extends BaseEventListener {

	public interface OrderCreateListener {
		void onOrderCreateEvent(OrderCreateSocketEvent event);
	}

	private OrderCreateListener mListener;

	public OrderCreateEventListener(final Context context, final OrderCreateListener listener) {
		super(context);
		mListener = listener;
	}

	@Subscribe
	public void onOrderCreateEvent(final OrderCreateSocketEvent event) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mListener.onOrderCreateEvent(event);
			}
		});
	}

}
