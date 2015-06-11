package com.omnom.android.socket;

import android.content.IntentFilter;

import com.omnom.android.utils.Extras;

/**
 * Created by Ch3D on 22.05.2015.
 */
public class OrderEventIntentFilter extends IntentFilter {

	public OrderEventIntentFilter() {
		super();
		addAction(Extras.ACTION_EVENT_ORDER_CLOSE);
		addAction(Extras.ACTION_EVENT_ORDER_CREATE);
		addAction(Extras.ACTION_EVENT_ORDER_UPDATE);
	}
}
