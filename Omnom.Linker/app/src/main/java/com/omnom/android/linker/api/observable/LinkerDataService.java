package com.omnom.android.linker.api.observable;

import com.omnom.android.linker.api.ApiProtocol;
import com.omnom.android.linker.api.ServerResponse;
import com.omnom.android.linker.model.Restaurant;
import com.omnom.android.linker.model.RestaurantsResult;

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
	Observable<Restaurant> getRestaurant(@Path(ApiProtocol.FIELD_ID) String restaurantId);

	@FormUrlEncoded
	@POST("/authenticate")
	Observable<String> authenticate(@Field(ApiProtocol.FIELD_USERNAME) String username, @Field(ApiProtocol.FIELD_PASSWORD) String
			password);

	@FormUrlEncoded
	@POST("/remind_password")
	Observable<String> remindPassword(@Field(ApiProtocol.FIELD_USERNAME) String username);

	@GET("/checkBeacon")
	Observable<Integer> checkBeacon(
			@Query(ApiProtocol.FIELD_RESTAURANT_ID) String restaurantId,
			@Query(ApiProtocol.FIELD_BEACON_UUID) String beaconUuid,
			@Query(ApiProtocol.FIELD_MAJOR_ID) String majorId, @Query(ApiProtocol.FIELD_MINOR_ID) String minorId);

	@POST("/ibeacons/bind")
	Observable<Integer> bindBeacon(
			@Field(ApiProtocol.FIELD_RESTAURANT_ID) String restaurantId,
			@Field(ApiProtocol.FIELD_TABLE_NUMBER) int tableNumber,
			@Field(ApiProtocol.FIELD_BEACON_UUID) String beaconUuid,
			@Field(ApiProtocol.FIELD_MAJOR_ID) String majorId,
			@Field(ApiProtocol.FIELD_MINOR_ID) String minorId);

	@POST("/commitBeacon")
	Observable<Integer> commitBeacon(
			@Field(ApiProtocol.FIELD_RESTAURANT_ID) String restaurantId,
			@Field(ApiProtocol.FIELD_BEACON_UUID) String beaconUuid,
			@Field(ApiProtocol.FIELD_MAJOR_ID) String majorId, @Field(ApiProtocol.FIELD_MINOR_ID) String minorId);

	@GET("/checkQrCode")
	Observable<Integer> checkQrCode(
			@Query(ApiProtocol.FIELD_RESTAURANT_ID) String restaurantId, @Query(ApiProtocol.FIELD_QR_DATA) String qrData);

	@POST("/bindQrCode")
	Observable<Integer> bindQrCode(
			@Field(ApiProtocol.FIELD_RESTAURANT_ID) String restaurantId, @Field(ApiProtocol.FIELD_QR_DATA) String qrData);

	@POST("/ibeacons/build")
	Observable<ServerResponse> build(@Field(ApiProtocol.FIELD_RESTAURANT_ID) String restaurantId,
	                                 @Field(ApiProtocol.FIELD_TABLE_NUMBER) int tableNumber,
	                                 @Field(ApiProtocol.FIELD_BEACON_UUID) String uuid);
}
