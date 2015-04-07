package com.omnom.android.restaurateur.api.observable;

import com.omnom.android.restaurateur.model.ResponseBase;
import com.omnom.android.restaurateur.model.SupportInfoResponse;
import com.omnom.android.restaurateur.model.WaiterCallResponse;
import com.omnom.android.restaurateur.model.beacon.BeaconDataResponse;
import com.omnom.android.restaurateur.model.bill.BillRequest;
import com.omnom.android.restaurateur.model.bill.BillResponse;
import com.omnom.android.restaurateur.model.cards.CardDeleteResponse;
import com.omnom.android.restaurateur.model.cards.CardsResponse;
import com.omnom.android.restaurateur.model.config.Config;
import com.omnom.android.restaurateur.model.decode.BeaconDecodeRequest;
import com.omnom.android.restaurateur.model.decode.HashDecodeRequest;
import com.omnom.android.restaurateur.model.decode.QrDecodeRequest;
import com.omnom.android.restaurateur.model.decode.RestaurantResponse;
import com.omnom.android.restaurateur.model.order.OrderItem;
import com.omnom.android.restaurateur.model.order.OrdersResponse;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantsResponse;
import com.omnom.android.restaurateur.model.restaurant.WishRequest;
import com.omnom.android.restaurateur.model.restaurant.WishResponse;
import com.omnom.android.restaurateur.model.table.DemoTableData;
import com.omnom.android.restaurateur.model.table.TableDataResponse;

import java.util.Collection;
import java.util.List;

import altbeacon.beacon.Beacon;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Ch3D on 11.08.2014.
 */
public interface RestaurateurObservableApi {
	public Observable<Restaurant> getRestaurant(String restaurantId);

	Observable<Restaurant> getRestaurant(String restaurantId, Func1<Restaurant, Restaurant> funcMap);

	public Observable<RestaurantsResponse> getRestaurants();

	public Observable<RestaurantsResponse> getRestaurants(double latitude, double longitude);

	public Observable<RestaurantsResponse> getRestaurantsAll();

	public Observable<RestaurantsResponse> getRestaurantsAll(double latitude, double longitude);

	public Observable<BeaconDataResponse> buildBeacon(String restaurantId, int tableNumber, String uuid);

	public Observable<RestaurantResponse> decode(BeaconDecodeRequest request, Func1<RestaurantResponse, RestaurantResponse> funcMap);

	public Observable<RestaurantResponse> decode(QrDecodeRequest request, Func1<RestaurantResponse, RestaurantResponse> funcMap);

	public Observable<RestaurantResponse> decode(HashDecodeRequest request, Func1<RestaurantResponse, RestaurantResponse> funcMap);

	public Observable<TableDataResponse> bind(String restaurantId,
	                                          int tableNumber,
	                                          String qrData,
	                                          Beacon beacon,
	                                          Beacon oldBeacon);

	public Observable<TableDataResponse> bind(String id,
	                                          int tableNumber,
	                                          String qrData,
	                                          BeaconDataResponse beaconData,
	                                          Beacon beacon);

	public Observable<BeaconDataResponse> bindBeacon(String restaurantId,
	                                                 int tableNumber,
	                                                 Beacon beacon,
	                                                 Beacon oldBeacon);

	public Observable<BeaconDataResponse> bindBeacon(String restaurantId,
	                                                 int tableNumber,
	                                                 BeaconDataResponse beaconData,
	                                                 Beacon oldBeacon);

	public Observable<TableDataResponse> findBeacon(Beacon beacon);

	public Observable<List<DemoTableData>> getDemoTable();

	public Observable<Restaurant> setRssiThreshold(String restaurantId, int rssi);

	public Observable<TableDataResponse> checkQrCode(String qrData);

	public Observable<TableDataResponse> bindQrCode(String restaurantId, int tableNumber, String qrData);

	public Observable<CardsResponse> getCards();

	public Observable<CardDeleteResponse> deleteCard(int cardId);

	public Observable<WaiterCallResponse> waiterCall(String restaurantId, String tableId);

	public Observable<WaiterCallResponse> waiterCallStop(String restaurantId, String tableId);

	public Observable<Restaurant> getMenu(String restaurantId);

	public Observable<OrdersResponse> getOrders(String restaurantId, String tableId);

	public Observable<Collection<OrderItem>> getRecommendations(String restaurantId);

	public Observable<ResponseBase> newGuest(String restaurantId, String tableId);

	public Observable<BillResponse> bill(BillRequest request);

	public Observable<Restaurant> link(long orderId, double amount, double tip);

	public Observable<Config> getConfig();

	public Observable<SupportInfoResponse> getSupportInfo();

	public Observable<WishResponse> wishes(String restId, WishRequest request);
}
