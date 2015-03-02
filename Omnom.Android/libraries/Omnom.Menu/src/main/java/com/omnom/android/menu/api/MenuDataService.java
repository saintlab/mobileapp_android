package com.omnom.android.menu.api;

import com.omnom.android.menu.model.MenuResponse;
import com.omnom.android.protocol.Protocol;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by Ch3D on 26.01.2015.
 */
public interface MenuDataService {
	@GET("/menu/{id}")
	public Observable<MenuResponse> getMenu(@Path(Protocol.FIELD_ID) String id);
}
