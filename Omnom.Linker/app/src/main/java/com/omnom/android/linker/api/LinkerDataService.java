package com.omnom.android.linker.api;

import com.omnom.android.linker.model.Restaurant;
import com.omnom.android.linker.model.RestaurantsResult;
import com.omnom.android.linker.model.ibeacon.BeaconBindRequest;
import com.omnom.android.linker.model.ibeacon.BeaconBuildRequest;
import com.omnom.android.linker.model.ibeacon.BeaconDataResponse;

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

	@GET("/data/restaurants")
	Observable<RestaurantsResult> getRestaurants();

	@GET("/restaurant/{id}")
	Observable<Restaurant> getRestaurant(@Path(Protocol.FIELD_ID) String restaurantId);

	@FormUrlEncoded
	@POST("/authenticate")
	Observable<String> authenticate(@Field(Protocol.FIELD_USERNAME) String username,
	                                @Field(Protocol.FIELD_PASSWORD) String password);

	@FormUrlEncoded
	@POST("/remind_password")
	Observable<String> remindPassword(@Field(Protocol.FIELD_USERNAME) String username);

	@GET("/checkBeacon")
	Observable<Integer> checkBeacon(@Query(Protocol.FIELD_RESTAURANT_ID) String restaurantId,
	                                @Query(Protocol.FIELD_BEACON_UUID) String beaconUuid,
	                                @Query(Protocol.FIELD_MAJOR_ID) String majorId,
	                                @Query(Protocol.FIELD_MINOR_ID) String minorId);

	@FormUrlEncoded
	@POST("/commitBeacon")
	Observable<Integer> commitBeacon(@Field(Protocol.FIELD_RESTAURANT_ID) String restaurantId,
	                                 @Field(Protocol.FIELD_BEACON_UUID) String beaconUuid,
	                                 @Field(Protocol.FIELD_MAJOR_ID) String majorId,
	                                 @Field(Protocol.FIELD_MINOR_ID) String minorId);

	@GET("/checkQrCode")
	Observable<Integer> checkQrCode(@Query(Protocol.FIELD_RESTAURANT_ID) String restaurantId,
	                                @Query(Protocol.FIELD_QR_DATA) String qrData);

	@FormUrlEncoded
	@POST("/bindQrCode")
	Observable<Integer> bindQrCode(@Field(Protocol.FIELD_RESTAURANT_ID) String restaurantId,
	                               @Field(Protocol.FIELD_TABLE_NUMBER) int tableNumber,
	                               @Field(Protocol.FIELD_QR_DATA) String qrData);

	@POST("/ibeacons/buildBeacon")
	Observable<BeaconDataResponse> build(@Body BeaconBuildRequest request);

	@POST("/ibeacons/bind")
	Observable<BeaconDataResponse> bindBeacon(@Body BeaconBindRequest request);
}
