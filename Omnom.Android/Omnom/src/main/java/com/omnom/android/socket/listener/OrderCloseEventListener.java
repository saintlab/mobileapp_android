package com.omnom.android.socket.listener;

import android.content.Context;

import com.omnom.android.socket.event.OrderCloseSocketEvent;
import com.squareup.otto.Subscribe;

/**
 * Created by michaelpotter on 09/01/15.
 */
public class OrderCloseEventListener extends BaseEventListener {

	public interface OrderCloseListener {
		void onOrderCloseEvent(OrderCloseSocketEvent event);
	}

	private OrderCloseListener mListener;

	public OrderCloseEventListener(final Context context, final OrderCloseListener listener) {
		super(context);
		mListener = listener;
	}

	@Subscribe
	public void onOrderCloseEvent(final OrderCloseSocketEvent event) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mListener.onOrderCloseEvent(event);
			}
		});
	}

}
