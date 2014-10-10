package com.omnom.android.restaurateur.api;

import com.omnom.android.restaurateur.model.beacon.BeaconBindRequest;
import com.omnom.android.restaurateur.model.beacon.BeaconBuildRequest;
import com.omnom.android.restaurateur.model.beacon.BeaconDataResponse;
import com.omnom.android.restaurateur.model.beacon.BeaconFindRequest;
import com.omnom.android.restaurateur.model.bill.BillRequest;
import com.omnom.android.restaurateur.model.qrcode.QRCodeBindRequest;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantsResponse;
import com.omnom.android.restaurateur.model.restaurant.RssiThresholdRequest;
import com.omnom.android.restaurateur.model.table.RestaurantTablesResponse;
import com.omnom.android.restaurateur.model.table.TableDataResponse;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Ch3D on 11.08.2014.
 */
public interface RestaurateurDataService {
	@GET("/cards")
	Observable<RestaurantsResponse> getCards();

	@DELETE("/cards/{card_id}")
	Observable<RestaurantsResponse> deleteCard(@Path(Protocol.FIELD_CARD_ID) String cardId);

	@POST("/restaurants/{restaurant_id}/tables/{table_id}/waiter/call")
	Observable<TableDataResponse> waiterCall(@Path(Protocol.FIELD_RESTAURANT_ID) String restaurantId,
	                                         @Path(Protocol.FIELD_TABLE_ID) String tableId);

	@POST("/restaurants/{restaurant_id}/tables/{table_id}/waiter/call/stop")
	Observable<TableDataResponse> waiterCallStop(@Path(Protocol.FIELD_RESTAURANT_ID) String restaurantId,
	                                             @Path(Protocol.FIELD_TABLE_ID) String tableId);

	@GET("/restaurants")
	Observable<RestaurantsResponse> getRestaurants();

	@GET("/restaurants/{id}")
	Observable<Restaurant> getRestaurant(@Path(Protocol.FIELD_ID) String restaurantId);

	@GET("/restaurants/{id}/menu")
	Observable<Restaurant> getMenu(@Path(Protocol.FIELD_ID) String restaurantId);

	@GET("/restaurants/{restaurant_id}/tables/{table_id}/orders")
	Observable<Restaurant> getOrders(@Path(Protocol.FIELD_ID) String restaurantId,
	                                 @Path(Protocol.FIELD_TABLE_ID) String tableId);

	@GET("/restaurants/{id}/tables")
	Observable<RestaurantTablesResponse> getRestaurantTables(@Path(Protocol.FIELD_ID) String restaurantId);

	@POST("/bill")
	Observable<TableDataResponse> bill(@Body BillRequest request);

	@GET("/link/{orderId}/{amount}/{tip}")
	Observable<Restaurant> link(@Path(Protocol.FIELD_ORDER_ID) long orderId,
	                            @Path(Protocol.FIELD_AMOUNT) double amount,
	                            @Path(Protocol.FIELD_TIP) double tip);

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
	Observable<Restaurant> setRssiThreshold(@Path(Protocol.FIELD_ID) String restaurantId,
	                                        @Body RssiThresholdRequest request);

	@GET("/ibeacons/decode")
	Observable<Integer> checkBeacon(@Query(Protocol.FIELD_BEACON_UUID) String beaconUuid,
	                                @Query(Protocol.FIELD_MAJOR_ID) String majorId,
	                                @Query(Protocol.FIELD_MINOR_ID) String minorId);

}
