package com.omnom.android.menu.api.observable;

import com.omnom.android.menu.model.MenuResponse;

import rx.Observable;

/**
 * Created by Ch3D on 26.01.2015.
 */
public interface MenuObservableApi {
	public Observable<MenuResponse> getMenu(String restId);
}
