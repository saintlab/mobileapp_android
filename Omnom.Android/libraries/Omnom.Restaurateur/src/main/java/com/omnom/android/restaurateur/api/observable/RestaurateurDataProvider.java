package com.omnom.android.restaurateur.api.observable;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omnom.android.restaurateur.api.RestaurateurDataService;
import com.omnom.android.restaurateur.model.ResponseBase;
import com.omnom.android.restaurateur.model.SupportInfoResponse;
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
import com.omnom.android.restaurateur.model.decode.BeaconDecodeRequest;
import com.omnom.android.restaurateur.model.decode.HashDecodeRequest;
import com.omnom.android.restaurateur.model.decode.QrDecodeRequest;
import com.omnom.android.restaurateur.model.decode.RestaurantResponse;
import com.omnom.android.restaurateur.model.order.OrderItem;
import com.omnom.android.restaurateur.model.order.OrdersResponse;
import com.omnom.android.restaurateur.model.qrcode.QRCodeBindRequest;
import com.omnom.android.restaurateur.model.restaurant.FileUploadReponse;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantsResponse;
import com.omnom.android.restaurateur.model.restaurant.RssiThresholdRequest;
import com.omnom.android.restaurateur.model.restaurant.WishRequest;
import com.omnom.android.restaurateur.model.restaurant.WishResponse;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.restaurateur.retrofit.RestaurateurRxSupport;
import com.omnom.android.restaurateur.serializer.OrdersResponseSerializer;
import com.omnom.android.utils.generation.AutoParcelAdapterFactory;

import java.util.Collection;

import altbeacon.beacon.Beacon;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedFile;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Ch3D on 11.08.2014.
 */
public class RestaurateurDataProvider implements RestaurateurObservableApi {
	public static RestaurateurDataProvider create(final String dataEndPoint, final RequestInterceptor interceptor) {
		final RestAdapter.LogLevel logLevel = RestAdapter.LogLevel.NONE;
		final Gson gson = new GsonBuilder()
				.registerTypeAdapter(OrdersResponse.class, new OrdersResponseSerializer())
				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
				.registerTypeAdapterFactory(new AutoParcelAdapterFactory())
				.create();
		final GsonConverter converter = new GsonConverter(gson);

		final RestAdapter mRestAdapter = new RestAdapter.Builder().setRequestInterceptor(interceptor).setEndpoint(dataEndPoint)
		                                                          .setRxSupport(new RestaurateurRxSupport(interceptor))
		                                                          .setLogLevel(logLevel)
		                                                          .setConverter(converter).build();
		return new RestaurateurDataProvider(mRestAdapter.create(RestaurateurDataService.class));
	}

	private final RestaurateurDataService mDataService;

	public RestaurateurDataProvider(final RestaurateurDataService dataService) {
		mDataService = dataService;
	}

	@Override
	public Observable<TableDataResponse> findBeacon(Beacon beacon) {
		return mDataService.findBeacon(new BeaconFindRequest(beacon)).subscribeOn(
				Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<RestaurantResponse> getDemoTable() {
		return mDataService.getDemoTable().subscribeOn(
				Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Restaurant> setRssiThreshold(String restaurantId, int rssi) {
		return mDataService.setRssiThreshold(restaurantId, new RssiThresholdRequest(rssi))
		                   .subscribeOn(Schedulers.io())
		                   .observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<BeaconDataResponse> bindBeacon(String restaurantId, int tableNumber, Beacon beacon, Beacon oldBeacon) {
		final BeaconBindRequest request = new BeaconBindRequest(restaurantId, tableNumber, beacon, oldBeacon);
		return mDataService.bindBeacon(request).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<TableDataResponse> bind(final String restaurantId, final int tableNumber, final String qrData,
	                                          final Beacon beacon,
	                                          final Beacon oldBeacon) {
		final BeaconQrBindRequest request = new BeaconQrBindRequest(restaurantId, tableNumber, qrData, beacon, oldBeacon);
		return mDataService.bind(request).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<TableDataResponse> bind(final String restaurantId,
	                                          final int tableNumber,
	                                          final String qrData,
	                                          final BeaconDataResponse beaconData,
	                                          final Beacon oldBeacon) {
		final BeaconQrBindRequest request = new BeaconQrBindRequest(restaurantId, tableNumber, qrData, beaconData, oldBeacon);
		return mDataService.bind(request).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<BeaconDataResponse> bindBeacon(String restaurantId, int tableNumber,
	                                                 BeaconDataResponse beaconData, Beacon oldBeacon) {
		final BeaconBindRequest request = new BeaconBindRequest(restaurantId, tableNumber, beaconData, oldBeacon);
		return mDataService.bindBeacon(request).subscribeOn(
				Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<TableDataResponse> checkQrCode(String qrData) {

		return mDataService.checkQrCode(qrData).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<TableDataResponse> bindQrCode(String restaurantId, int tableNumber, String qrData) {
		return mDataService.bindQrCode(new QRCodeBindRequest(restaurantId, tableNumber, qrData)).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<CardsResponse> getCards() {
		return mDataService.getCards().subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<CardDeleteResponse> deleteCard(int cardId) {
		return mDataService.deleteCard(cardId).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<WaiterCallResponse> waiterCall(String restaurantId, String tableId) {
		return mDataService.waiterCall(restaurantId, tableId).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<WaiterCallResponse> waiterCallStop(String restaurantId, String tableId) {
		return mDataService.waiterCallStop(restaurantId, tableId).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Restaurant> getMenu(String restaurantId) {
		return mDataService.getMenu(restaurantId).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<OrdersResponse> getOrders(String restaurantId, String tableId) {
		return mDataService.getOrders(restaurantId, tableId).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Collection<OrderItem>> getRecommendations(final String restaurantId) {
		return mDataService.getRecommendations(restaurantId).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<ResponseBase> newGuest(String restaurantId, String tableId) {
		return mDataService.newGuest(restaurantId, tableId).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<BillResponse> bill(BillRequest request) {
		return mDataService.bill(request).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Restaurant> link(long orderId, double amount, double tip) {
		return mDataService.link(orderId, amount, tip).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<SupportInfoResponse> getSupportInfo() {
		return mDataService.getSupportInfo().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<WishResponse> wishes(final String restId, final WishRequest request) {
		return mDataService.wishes(restId, request).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<WishResponse> getWish(final String wishId) {
		return mDataService.getWish(wishId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<FileUploadReponse> updateAvatar(final TypedFile file) {
		return mDataService.updateAvatar(file).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Restaurant> getRestaurant(String restaurantId) {
		return mDataService.getRestaurant(restaurantId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Restaurant> getRestaurant(String restaurantId, Func1<Restaurant, Restaurant> funcMap) {
		return mDataService.getRestaurant(restaurantId).map(funcMap).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread
				());
	}

	@Override
	public Observable<RestaurantsResponse> getRestaurants() {
		return mDataService.getRestaurants().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<RestaurantsResponse> getRestaurants(double latitude, double longitude) {
		return mDataService.getRestaurants(latitude, longitude).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<RestaurantsResponse> getRestaurantsAll() {
		return mDataService.getRestaurantsAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<RestaurantsResponse> getRestaurantsAll(double latitude, double longitude) {
		return mDataService.getRestaurantsAll(latitude, longitude).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<BeaconDataResponse> buildBeacon(String restaurantId, int tableNumber, String uuid) {
		return mDataService.buildBeacon(new BeaconBuildRequest(uuid, String.valueOf(tableNumber), restaurantId))
		                   .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<RestaurantResponse> decode(final BeaconDecodeRequest request, Func1<RestaurantResponse,
			RestaurantResponse> funcMap) {
		return mDataService.decode(request).map(funcMap).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<RestaurantResponse> decode(final QrDecodeRequest request, Func1<RestaurantResponse, RestaurantResponse> funcMap) {
		return mDataService.decode(request).map(funcMap).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<RestaurantResponse> decode(final HashDecodeRequest request, Func1<RestaurantResponse, RestaurantResponse> funcMap) {
		return mDataService.decode(request).map(funcMap).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

}
