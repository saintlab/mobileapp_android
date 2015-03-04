package com.omnom.android.notifier.api.observable;

import rx.Observable;

/**
 * Created by Ch3D on 28.02.2015.
 */
public interface NotifierObservableApi {
	public Observable<Object> register(String pushtoken);

	public Observable<Object> tableIn(String restId, String tableId);

	public Observable<Object> tableOut(String restId, String tableId);

	public Observable<Object> tableOut();

	public Observable<Object> unregister();
}
