package com.omnom.android.restaurateur.api.observable;

import com.omnom.android.restaurateur.model.ResponseBase;
import com.omnom.android.restaurateur.model.SupportInfoResponse;
import com.omnom.android.restaurateur.model.WaiterCallResponse;
import com.omnom.android.restaurateur.model.beacon.BeaconDataResponse;
import com.omnom.android.restaurateur.model.bill.BillRequest;
import com.omnom.android.restaurateur.model.bill.BillResponse;
import com.omnom.android.restaurateur.model.cards.CardDeleteResponse;
import com.omnom.android.restaurateur.model.cards.CardsResponse;
import com.omnom.android.restaurateur.model.decode.BeaconDecodeRequest;
import com.omnom.android.restaurateur.model.decode.HashDecodeRequest;
import com.omnom.android.restaurateur.model.decode.QrDecodeRequest;
import com.omnom.android.restaurateur.model.decode.RestaurantResponse;
import com.omnom.android.restaurateur.model.order.OrderItem;
import com.omnom.android.restaurateur.model.order.OrdersResponse;
import com.omnom.android.restaurateur.model.restaurant.FileUploadReponse;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantsResponse;
import com.omnom.android.restaurateur.model.restaurant.WishRequest;
import com.omnom.android.restaurateur.model.restaurant.WishResponse;
import com.omnom.android.restaurateur.model.table.DemoTableData;
import com.omnom.android.restaurateur.model.table.TableDataResponse;

import java.util.Collection;
import java.util.List;

import altbeacon.beacon.Beacon;
import retrofit.mime.TypedFile;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Ch3D on 11.08.2014.
 */
public interface RestaurateurObservableApi {
	Observable<Restaurant> getRestaurant(String restaurantId);

	Observable<Restaurant> getRestaurant(String restaurantId, Func1<Restaurant, Restaurant> funcMap);

	Observable<RestaurantsResponse> getRestaurants();

	Observable<RestaurantsResponse> getRestaurants(double latitude, double longitude);

	Observable<RestaurantsResponse> getRestaurantsAll();

	Observable<RestaurantsResponse> getRestaurantsAll(double latitude, double longitude);

	Observable<BeaconDataResponse> buildBeacon(String restaurantId, int tableNumber, String uuid);

	Observable<RestaurantResponse> decode(BeaconDecodeRequest request, Func1<RestaurantResponse, RestaurantResponse> funcMap);

	Observable<RestaurantResponse> decode(QrDecodeRequest request, Func1<RestaurantResponse, RestaurantResponse> funcMap);

	Observable<RestaurantResponse> decode(HashDecodeRequest request, Func1<RestaurantResponse, RestaurantResponse> funcMap);

	Observable<TableDataResponse> bind(String restaurantId,
	                                   int tableNumber,
	                                   String qrData,
	                                   Beacon beacon,
	                                   Beacon oldBeacon);

	Observable<TableDataResponse> bind(String id,
	                                   int tableNumber,
	                                   String qrData,
	                                   BeaconDataResponse beaconData,
	                                   Beacon beacon);

	Observable<BeaconDataResponse> bindBeacon(String restaurantId,
	                                          int tableNumber,
	                                          Beacon beacon,
	                                          Beacon oldBeacon);

	Observable<BeaconDataResponse> bindBeacon(String restaurantId,
	                                          int tableNumber,
	                                          BeaconDataResponse beaconData,
	                                          Beacon beacon);

	Observable<TableDataResponse> findBeacon(Beacon beacon);

	Observable<List<DemoTableData>> getDemoTable();

	Observable<Restaurant> setRssiThreshold(String restaurantId, int rssi);

	Observable<TableDataResponse> checkQrCode(String qrData);

	Observable<TableDataResponse> bindQrCode(String restaurantId, int tableNumber, String qrData);

	Observable<CardsResponse> getCards();

	Observable<CardDeleteResponse> deleteCard(int cardId);

	Observable<WaiterCallResponse> waiterCall(String restaurantId, String tableId);

	Observable<WaiterCallResponse> waiterCallStop(String restaurantId, String tableId);

	Observable<Restaurant> getMenu(String restaurantId);

	Observable<OrdersResponse> getOrders(String restaurantId, String tableId);

	Observable<Collection<OrderItem>> getRecommendations(String restaurantId);

	Observable<ResponseBase> newGuest(String restaurantId, String tableId);

	Observable<BillResponse> bill(BillRequest request);

	Observable<Restaurant> link(long orderId, double amount, double tip);

	Observable<SupportInfoResponse> getSupportInfo();

	Observable<WishResponse> wishes(String restId, WishRequest request);

	Observable<WishResponse> getWish(String wishId);

	Observable<FileUploadReponse> updateAvatar(TypedFile image);
}
