package com.omnom.android.socket;

import android.content.IntentFilter;

import com.omnom.android.utils.Extras;

/**
 * Created by Ch3D on 22.05.2015.
 */
public class PaymentEventIntentFilter extends IntentFilter {

	public PaymentEventIntentFilter(int priority) {
		super(Extras.ACTION_EVENT_PAYMENT);
		setPriority(priority);
	}
}
