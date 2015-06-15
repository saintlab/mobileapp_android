package com.omnom.android.socket.listener;

import android.content.Context;
import android.content.Intent;

import com.omnom.android.socket.event.OrderCloseSocketEvent;
import com.omnom.android.utils.Extras;
import com.squareup.otto.Subscribe;

/**
 * Created by michaelpotter on 09/01/15.
 */
public class OrderCloseEventListener extends BaseEventListener {

	public OrderCloseEventListener(final Context context) {
		super(context);
	}

	@Subscribe
	public void onOrderCloseEvent(final OrderCloseSocketEvent event) {
		final Intent intent = new Intent(Extras.ACTION_EVENT_ORDER_CLOSE);
		intent.putExtra(Extras.ACTION_EVENT_ORDER_CLOSE, event);
		mContext.sendOrderedBroadcast(intent, null);
	}

}
