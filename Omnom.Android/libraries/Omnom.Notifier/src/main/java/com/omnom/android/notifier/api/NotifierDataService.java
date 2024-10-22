package com.omnom.android.notifier.api;

import com.omnom.android.notifier.model.RegisterRequest;
import com.omnom.android.protocol.Protocol;

import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by Ch3D on 28.02.2015.
 */
public interface NotifierDataService {

	@POST("/notifier/register")
	public Observable<Object> register(@Body RegisterRequest request);

	@POST("/restaurants/{restaurant_id}/tables/{table_id}/in")
	public Observable<Object> tableIn(@Path(Protocol.FIELD_RESTAURANT_ID) String restId, @Path(Protocol.FIELD_TABLE_ID) String tableId);

	@POST("/restaurants/{restaurant_id}/tables/{table_id}/out")
	public Observable<Object> tableOut(@Path(Protocol.FIELD_RESTAURANT_ID) String restId, @Path(Protocol.FIELD_TABLE_ID) String tableId);

	@POST("/notifier/unregister")
	public Observable<Object> unregister();
}
