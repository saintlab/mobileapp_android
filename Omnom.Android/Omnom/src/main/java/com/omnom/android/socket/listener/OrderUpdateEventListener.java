package com.omnom.android.socket.listener;

import android.content.Context;

import com.omnom.android.socket.event.OrderUpdateSocketEvent;
import com.squareup.otto.Subscribe;

/**
 * Created by michaelpotter on 09/01/15.
 */
public class OrderUpdateEventListener extends BaseEventListener {

	public interface OrderUpdateListener {
		void onOrderUpdateEvent(OrderUpdateSocketEvent event);
	}

	private OrderUpdateListener mListener;

	public OrderUpdateEventListener(final Context context, final OrderUpdateListener listener) {
		super(context);
		mListener = listener;
	}

	@Subscribe
	public void onOrderUpdateEvent(final OrderUpdateSocketEvent event) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mListener.onOrderUpdateEvent(event);
			}
		});
	}

}
