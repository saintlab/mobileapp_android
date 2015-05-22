package com.omnom.android.socket.listener;

import android.content.Context;
import android.content.Intent;

import com.omnom.android.socket.event.OrderUpdateSocketEvent;
import com.omnom.android.utils.Extras;
import com.squareup.otto.Subscribe;

/**
 * Created by michaelpotter on 09/01/15.
 */
public class OrderUpdateEventListener extends BaseEventListener {

	public OrderUpdateEventListener(final Context context) {
		super(context);
	}

	@Subscribe
	public void onOrderUpdateEvent(final OrderUpdateSocketEvent event) {
		final Intent intent = new Intent(Extras.ACTION_EVENT_ORDER_UPDATE);
		intent.putExtra(Extras.ACTION_EVENT_ORDER_UPDATE, event);
		mContext.sendOrderedBroadcast(intent, null);
	}

}
