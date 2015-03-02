package com.omnom.android.notifier.api.observable;

import rx.Observable;

/**
 * Created by Ch3D on 28.02.2015.
 */
public interface NotifierObservableApi {
	public Observable register(String pushtoken);

	public Observable tableIn(String restId, String tableId);

	public Observable tableOut(String restId, String tableId);

	public Observable unregister();
}
