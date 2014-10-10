package com.omnom.android.restaurateur.api.observable.providers;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omnom.android.restaurateur.api.RestaurateurDataService;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.beacon.BeaconBindRequest;
import com.omnom.android.restaurateur.model.beacon.BeaconBuildRequest;
import com.omnom.android.restaurateur.model.beacon.BeaconDataResponse;
import com.omnom.android.restaurateur.model.beacon.BeaconFindRequest;
import com.omnom.android.restaurateur.model.bill.BillRequest;
import com.omnom.android.restaurateur.model.qrcode.QRCodeBindRequest;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantsResponse;
import com.omnom.android.restaurateur.model.restaurant.RssiThresholdRequest;
import com.omnom.android.restaurateur.model.table.TableDataResponse;

import altbeacon.beacon.Beacon;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Ch3D on 11.08.2014.
 */
public class RestaurateurDataProvider implements RestaurateurObeservableApi {
	public static RestaurateurDataProvider create(final String dataEndPoint, final RequestInterceptor interceptor) {
		// final RestAdapter.LogLevel logLevel = BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;
		final RestAdapter.LogLevel logLevel = RestAdapter.LogLevel.FULL;
		final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
		final GsonConverter converter = new GsonConverter(gson);

		final RestAdapter mRestAdapter = new RestAdapter.Builder().setRequestInterceptor(interceptor).setEndpoint(dataEndPoint)
		                                                          .setLogLevel(
				                                                          logLevel)
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
	public Observable<Restaurant> setRssiThreshold(String restaurantId, int rssi) {
		return mDataService.setRssiThreshold(restaurantId, new RssiThresholdRequest(rssi))
		                   .subscribeOn(Schedulers.io())
		                   .observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<BeaconDataResponse> bindBeacon(String restaurantId, int tableNumber, Beacon beacon, Beacon oldBeacon) {
		final BeaconBindRequest request = new BeaconBindRequest(restaurantId, tableNumber, beacon, oldBeacon);
		return mDataService.bindBeacon(request).subscribeOn(
				Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
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
	public Observable<RestaurantsResponse> getCards() {
		return mDataService.getCards().subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<RestaurantsResponse> deleteCard(String cardId) {
		return mDataService.deleteCard(cardId).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<TableDataResponse> waiterCall(String restaurantId, String tableId) {
		return mDataService.waiterCall(restaurantId, tableId).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<TableDataResponse> waiterCallStop(String restaurantId, String tableId) {
		return mDataService.waiterCallStop(restaurantId, tableId).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Restaurant> getMenu(String restaurantId) {
		return mDataService.getMenu(restaurantId).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Restaurant> getOrders(String restaurantId, String tableId) {
		return mDataService.getOrders(restaurantId, tableId).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<TableDataResponse> bill(BillRequest request) {
		return mDataService.bill(request).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Restaurant> link(long orderId, double amount, double tip) {
		return mDataService.link(orderId, amount, tip).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<Restaurant> getRestaurant(String restaurantId) {
		return mDataService.getRestaurant(restaurantId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<RestaurantsResponse> getRestaurants() {
		return mDataService.getRestaurants().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<BeaconDataResponse> buildBeacon(String restaurantId, int tableNumber, String uuid) {
		return mDataService.buildBeacon(new BeaconBuildRequest(uuid, String.valueOf(tableNumber), restaurantId))
		                   .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}
}
