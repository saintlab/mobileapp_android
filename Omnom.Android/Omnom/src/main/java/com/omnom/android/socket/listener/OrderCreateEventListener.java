package com.omnom.android.socket.listener;

import android.content.Context;
import android.content.Intent;

import com.omnom.android.socket.event.OrderCreateSocketEvent;
import com.omnom.android.utils.Extras;
import com.squareup.otto.Subscribe;

/**
 * Created by michaelpotter on 09/01/15.
 */
public class OrderCreateEventListener extends BaseEventListener {

	public OrderCreateEventListener(final Context context) {
		super(context);
	}

	@Subscribe
	public void onOrderCreateEvent(final OrderCreateSocketEvent event) {
		final Intent intent = new Intent(Extras.ACTION_EVENT_ORDER_CREATE);
		intent.putExtra(Extras.ACTION_EVENT_ORDER_CREATE, event);
		mContext.sendOrderedBroadcast(intent, null);
	}

}
