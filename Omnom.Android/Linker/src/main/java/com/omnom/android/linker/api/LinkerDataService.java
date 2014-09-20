package com.omnom.android.linker.api;

import com.omnom.android.linker.model.beacon.BeaconBindRequest;
import com.omnom.android.linker.model.beacon.BeaconBuildRequest;
import com.omnom.android.linker.model.beacon.BeaconDataResponse;
import com.omnom.android.linker.model.beacon.BeaconFindRequest;
import com.omnom.android.linker.model.qrcode.QRCodeBindRequest;
import com.omnom.android.linker.model.restaurant.Restaurant;
import com.omnom.android.linker.model.restaurant.RestaurantsResponse;
import com.omnom.android.linker.model.restaurant.RssiThresholdRequest;
import com.omnom.android.linker.model.table.RestaurantTablesResponse;
import com.omnom.android.linker.model.table.TableDataResponse;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Ch3D on 11.08.2014.
 */
public interface LinkerDataService {
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

	@PUT("/restaurants/{id}")
	Observable<Restaurant> setRssiThreshold(@Path(Protocol.FIELD_ID) String restaurantId, @Body RssiThresholdRequest request);

	@GET("/ibeacons/decode")
	Observable<Integer> checkBeacon(@Query(Protocol.FIELD_BEACON_UUID) String beaconUuid,
	                                @Query(Protocol.FIELD_MAJOR_ID) String majorId,
	                                @Query(Protocol.FIELD_MINOR_ID) String minorId);

}
