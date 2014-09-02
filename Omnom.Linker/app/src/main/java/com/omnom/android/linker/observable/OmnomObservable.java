package com.omnom.android.linker.observable;

import rx.Subscription;

/**
 * Created by Ch3D on 02.09.2014.
 */
public class OmnomObservable {
	public static void unsubscribe(Subscription subscription) {
		if(subscription != null && !subscription.isUnsubscribed()) {
			subscription.unsubscribe();
		}
	}
}
