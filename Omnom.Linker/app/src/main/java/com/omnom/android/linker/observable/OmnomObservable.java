package com.omnom.android.linker.observable;


import rx.Observable;
import rx.Subscription;

/**
 * Created by Ch3D on 02.09.2014.
 */
public class OmnomObservable {
	public static void unsubscribe(Subscription subscription) {
		if (subscription != null && !subscription.isUnsubscribed()) {
			subscription.unsubscribe();
		}
	}

	public class ChainObservable<T> extends Observable<T> {
		protected ChainObservable(OnSubscribe<T> f) {
			super(f);
		}
	}

	public static void test() {

	}
}
