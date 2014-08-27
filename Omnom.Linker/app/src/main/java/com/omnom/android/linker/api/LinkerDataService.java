package com.omnom.android.linker.api;

import com.omnom.android.linker.model.ibeacon.BeaconBindRequest;
import com.omnom.android.linker.model.ibeacon.BeaconBuildRequest;
import com.omnom.android.linker.model.ibeacon.BeaconDataResponse;
import com.omnom.android.linker.model.ibeacon.BeaconFindRequest;
import com.omnom.android.linker.model.qrcode.QRCodeBindRequest;
import com.omnom.android.linker.model.restaurant.Restaurant;
import com.omnom.android.linker.model.restaurant.RestaurantsResponse;
import com.omnom.android.linker.model.table.RestaurantTablesResponse;
import com.omnom.android.linker.model.table.TableDataResponse;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Ch3D on 11.08.2014.
 */
public interface LinkerDataService {

	@FormUrlEncoded
	@POST("/authenticate")
	Observable<String> authenticate(@Field(Protocol.FIELD_USERNAME) String username,
	                                @Field(Protocol.FIELD_PASSWORD) String password);

	@FormUrlEncoded
	@POST("/remind_password")
	Observable<String> remindPassword(@Field(Protocol.FIELD_USERNAME) String username);

	// migration done
	@GET("/restaurants")
	Observable<RestaurantsResponse> getRestaurants();

	@GET("/restaurants/{id}")
	Observable<Restaurant> getRestaurant(@Path(Protocol.FIELD_ID) String restaurantId);

	@GET("/restaurants/{id}/tables")
	Observable<RestaurantTablesResponse> getRestaurantTables(@Path(Protocol.FIELD_ID) String restaurantId);

	@POST("/qr/bind")
	Observable<TableDataResponse> bindQrCode(@Body QRCodeBindRequest request);

	@GET("/qr/{qr}")
	Observable<TableDataResponse> checkQrCode(@Path(Protocol.FIELD_QR_DATA) String qrData);

	@POST("/ibeacons/build")
	Observable<BeaconDataResponse> buildBeacon(@Body BeaconBuildRequest request);

	@POST("/ibeacons/bind")
	Observable<BeaconDataResponse> bindBeacon(@Body BeaconBindRequest request);

	@POST("/ibeacons/find")
	Observable<TableDataResponse> findBeacon(@Body BeaconFindRequest request);

	@GET("/ibeacons/decode")
	Observable<Integer> checkBeacon(@Query(Protocol.FIELD_BEACON_UUID) String beaconUuid,
	                                @Query(Protocol.FIELD_MAJOR_ID) String majorId,
	                                @Query(Protocol.FIELD_MINOR_ID) String minorId);

}
