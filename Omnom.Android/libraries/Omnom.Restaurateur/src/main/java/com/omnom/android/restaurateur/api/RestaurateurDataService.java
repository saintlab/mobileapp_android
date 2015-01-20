package com.omnom.android.restaurateur.api;

import com.omnom.android.protocol.Protocol;
import com.omnom.android.restaurateur.model.ResponseBase;
import com.omnom.android.restaurateur.model.WaiterCallResponse;
import com.omnom.android.restaurateur.model.beacon.BeaconBindRequest;
import com.omnom.android.restaurateur.model.beacon.BeaconBuildRequest;
import com.omnom.android.restaurateur.model.beacon.BeaconDataResponse;
import com.omnom.android.restaurateur.model.beacon.BeaconFindRequest;
import com.omnom.android.restaurateur.model.beacon.BeaconQrBindRequest;
import com.omnom.android.restaurateur.model.bill.BillRequest;
import com.omnom.android.restaurateur.model.bill.BillResponse;
import com.omnom.android.restaurateur.model.cards.CardDeleteResponse;
import com.omnom.android.restaurateur.model.cards.CardsResponse;
import com.omnom.android.restaurateur.model.config.Config;
import com.omnom.android.restaurateur.model.decode.BeaconDecodeRequest;
import com.omnom.android.restaurateur.model.decode.HashDecodeRequest;
import com.omnom.android.restaurateur.model.decode.QrDecodeRequest;
import com.omnom.android.restaurateur.model.decode.RestaurantResponse;
import com.omnom.android.restaurateur.model.order.OrdersResponse;
import com.omnom.android.restaurateur.model.qrcode.QRCodeBindRequest;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantsResponse;
import com.omnom.android.restaurateur.model.restaurant.RssiThresholdRequest;
import com.omnom.android.restaurateur.model.table.DemoTableData;
import com.omnom.android.restaurateur.model.table.RestaurantTablesResponse;
import com.omnom.android.restaurateur.model.table.TableDataResponse;

import java.util.List;

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
	Observable<CardsResponse> getCards();

	@DELETE("/cards/{card_id}")
	Observable<CardDeleteResponse> deleteCard(@Path(Protocol.FIELD_CARD_ID) int cardId);

	@POST("/restaurants/{restaurant_id}/tables/{table_id}/waiter/call")
	Observable<WaiterCallResponse> waiterCall(@Path(Protocol.FIELD_RESTAURANT_ID) String restaurantId,
	                                          @Path(Protocol.FIELD_TABLE_ID) String tableId);

	@POST("/restaurants/{restaurant_id}/tables/{table_id}/waiter/call/stop")
	Observable<WaiterCallResponse> waiterCallStop(@Path(Protocol.FIELD_RESTAURANT_ID) String restaurantId,
	                                              @Path(Protocol.FIELD_TABLE_ID) String tableId);

	@POST("/v2/decode/ibeacons/omnom")
	Observable<RestaurantResponse> decode(@Body BeaconDecodeRequest request);

	@POST("/v2/decode/qr")
	Observable<RestaurantResponse> decode(@Body QrDecodeRequest request);

	@POST("/v2/decode/hash")
	Observable<RestaurantResponse> decode(@Body HashDecodeRequest request);

	@GET("/restaurants")
	Observable<RestaurantsResponse> getRestaurants();

	@GET("/restaurants")
	Observable<RestaurantsResponse> getRestaurants(@Query(Protocol.FIELD_LATITUDE) double latitude,
	                                               @Query(Protocol.FIELD_LONGITUDE) double longitude);

	@GET("/restaurants/{id}")
	Observable<Restaurant> getRestaurant(@Path(Protocol.FIELD_ID) String restaurantId);

	@GET("/restaurants/{id}/menu")
	Observable<Restaurant> getMenu(@Path(Protocol.FIELD_ID) String restaurantId);

	@GET("/restaurants/{restaurant_id}/tables/{table_id}/orders")
	Observable<OrdersResponse> getOrders(@Path(Protocol.FIELD_RESTAURANT_ID) String restaurantId,
	                                     @Path(Protocol.FIELD_TABLE_ID) String tableId);

	@POST("/restaurants/{restaurant_id}/tables/{table_id}/new/guest")
	Observable<ResponseBase> newGuest(@Path(Protocol.FIELD_RESTAURANT_ID) String restaurantId,
	                                  @Path(Protocol.FIELD_TABLE_ID) String tableId);

	@GET("/restaurants/{id}/tables")
	Observable<RestaurantTablesResponse> getRestaurantTables(@Path(Protocol.FIELD_ID) String restaurantId);

	@POST("/bill")
	Observable<BillResponse> bill(@Body BillRequest request);

	@GET("/link/{orderId}/{amount}/{tip}")
	Observable<Restaurant> link(@Path(Protocol.FIELD_ORDER_ID) long orderId,
	                            @Path(Protocol.FIELD_AMOUNT) double amount,
	                            @Path(Protocol.FIELD_TIP) double tip);

	@GET("/mobile/config")
	Observable<Config> getConfig();

	@POST("/qr/bind")
	Observable<TableDataResponse> bindQrCode(@Body QRCodeBindRequest request);

	@POST("/table/bind")
	Observable<TableDataResponse> bind(@Body BeaconQrBindRequest request);

	@GET("/qr/{qr}")
	Observable<TableDataResponse> checkQrCode(@Path(Protocol.FIELD_QR_DATA) String qrData);

	@POST("/ibeacons/build")
	Observable<BeaconDataResponse> buildBeacon(@Body BeaconBuildRequest request);

	@POST("/ibeacons/bind")
	Observable<BeaconDataResponse> bindBeacon(@Body BeaconBindRequest request);

	@POST("/ibeacons/find")
	Observable<TableDataResponse> findBeacon(@Body BeaconFindRequest request);

	@GET("/ibeacons/demo")
	Observable<List<DemoTableData>> getDemoTable();

	@PUT("/restaurants/{id}")
	Observable<Restaurant> setRssiThreshold(@Path(Protocol.FIELD_ID) String restaurantId,
	                                        @Body RssiThresholdRequest request);

	@GET("/ibeacons/decode")
	Observable<Integer> checkBeacon(@Query(Protocol.FIELD_BEACON_UUID) String beaconUuid,
	                                @Query(Protocol.FIELD_MAJOR_ID) String majorId,
	                                @Query(Protocol.FIELD_MINOR_ID) String minorId);
}
