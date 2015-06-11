package com.omnom.android.interceptors.mixpanel;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.restaurateur.api.RestaurateurDataService;
import com.omnom.android.restaurateur.api.observable.RestaurateurDataProvider;
import com.omnom.android.restaurateur.model.ResponseBase;
import com.omnom.android.restaurateur.model.SupportInfoResponse;
import com.omnom.android.restaurateur.model.bill.BillRequest;
import com.omnom.android.restaurateur.model.bill.BillResponse;
import com.omnom.android.restaurateur.model.cards.CardDeleteResponse;
import com.omnom.android.restaurateur.model.cards.CardsResponse;
import com.omnom.android.restaurateur.model.config.AcquiringData;
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
import com.omnom.android.restaurateur.retrofit.RestaurateurRxSupport;
import com.omnom.android.restaurateur.serializer.MailRuSerializer;
import com.omnom.android.restaurateur.serializer.OrdersResponseSerializer;
import com.omnom.android.utils.generation.AutoParcelAdapterFactory;
import com.omnom.android.utils.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

import altbeacon.beacon.Beacon;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.omnom.android.mixpanel.MixPanelHelper.Project.OMNOM_ANDROID;

/**
 * Created by Ch3D on 03.01.2015.
 */
public class RestaurateurMixpanelProxy extends RestaurateurDataProvider {

	private static final String TAG = RestaurateurMixpanelProxy.class.getSimpleName();

	public static RestaurateurDataProvider create(final String dataEndPoint, final RequestInterceptor interceptor, final MixPanelHelper
			mixPanelHelper) {
		// final RestAdapter.LogLevel logLevel = BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;
		final RestAdapter.LogLevel logLevel = RestAdapter.LogLevel.FULL;
		final Gson gson = new GsonBuilder()
				.registerTypeAdapter(AcquiringData.class, new MailRuSerializer())
				.registerTypeAdapter(OrdersResponse.class, new OrdersResponseSerializer())
				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
				.registerTypeAdapterFactory(new AutoParcelAdapterFactory())
				.create();
		final GsonConverter converter = new GsonConverter(gson);

		final RestAdapter mRestAdapter = new RestAdapter.Builder().setRequestInterceptor(interceptor).setEndpoint(dataEndPoint)
		                                                          .setRxSupport(new RestaurateurRxSupport(interceptor))
		                                                          .setLogLevel(logLevel)
		                                                          .setConverter(converter).build();
		return new RestaurateurMixpanelProxy(mRestAdapter.create(RestaurateurDataService.class), mixPanelHelper);
	}

	private final RestaurateurDataService mDataService;

	private final MixPanelHelper mMixHelper;

	public RestaurateurMixpanelProxy(final RestaurateurDataService dataService, MixPanelHelper mixPanelHelper) {
		super(dataService);
		mDataService = dataService;
		mMixHelper = mixPanelHelper;
	}

	@Override
	public Observable<TableDataResponse> findBeacon(final Beacon beacon) {
		mMixHelper.track(OMNOM_ANDROID, "restarateur.findBeacon ->", beacon);
		return super.findBeacon(beacon).doOnNext(new Action1<TableDataResponse>() {
			@Override
			public void call(TableDataResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.findBeacon <-", response);
			}
		});
	}

	@Override
	public Observable<List<DemoTableData>> getDemoTable() {
		mMixHelper.track(OMNOM_ANDROID, "restarateur.getDemoTable ->", StringUtils.EMPTY_STRING);
		return super.getDemoTable().doOnNext(new Action1<List<DemoTableData>>() {
			@Override
			public void call(List<DemoTableData> response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.getDemoTable <-", response);
			}
		});
	}

	@Override
	public Observable<TableDataResponse> checkQrCode(final String qrData) {
		mMixHelper.track(OMNOM_ANDROID, "restarateur.checkQrCode ->", qrData);
		return super.checkQrCode(qrData).doOnNext(new Action1<TableDataResponse>() {
			@Override
			public void call(TableDataResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.checkQrCode <-", response);
			}
		});
	}

	@Override
	public Observable<CardsResponse> getCards() {
		mMixHelper.track(OMNOM_ANDROID, "restarateur.getCards ->", StringUtils.EMPTY_STRING);
		return super.getCards().doOnNext(new Action1<CardsResponse>() {
			@Override
			public void call(CardsResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.getCards <-", response);
			}
		});
	}

	@Override
	public Observable<CardDeleteResponse> deleteCard(final int cardId) {
		final JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("cardId", cardId);
		} catch(JSONException e) {
			Log.e(TAG, "deleteCard", e);
		}
		mMixHelper.track(OMNOM_ANDROID, "restarateur.deleteCard ->", jsonObject);
		return super.deleteCard(cardId).doOnNext(new Action1<CardDeleteResponse>() {
			@Override
			public void call(CardDeleteResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.deleteCard <-", response);
			}
		});
	}

	@Override
	public Observable<OrdersResponse> getOrders(final String restaurantId, final String tableId) {
		final JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("restaurantId", restaurantId);
			jsonObject.put("tableId", tableId);
		} catch(JSONException e) {
			Log.e(TAG, "getOrders", e);
		}
		mMixHelper.track(OMNOM_ANDROID, "restarateur.getOrders ->", jsonObject);
		return super.getOrders(restaurantId, tableId).doOnNext(new Action1<OrdersResponse>() {
			@Override
			public void call(OrdersResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.getOrders <-", response);
			}
		});
	}

	@Override
	public Observable<ResponseBase> newGuest(final String restaurantId, final String tableId) {
		final JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("restaurantId", restaurantId);
			jsonObject.put("tableId", tableId);
		} catch(JSONException e) {
			Log.e(TAG, "newGuest", e);
		}
		mMixHelper.track(OMNOM_ANDROID, "restarateur.newGuest ->", jsonObject);
		return super.newGuest(restaurantId, tableId).doOnNext(new Action1<ResponseBase>() {
			@Override
			public void call(ResponseBase response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.newGuest <-", response);
			}
		});
	}

	@Override
	public Observable<BillResponse> bill(final BillRequest request) {
		mMixHelper.track(OMNOM_ANDROID, "restarateur.bill ->", request);
		return super.bill(request).doOnNext(new Action1<BillResponse>() {
			@Override
			public void call(BillResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.bill <-", response);
			}
		});
	}

	@Override
	public Observable<Restaurant> link(final long orderId, final double amount, final double tip) {
		final JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("orderId", orderId);
			jsonObject.put("amount", amount);
			jsonObject.put("tip", tip);
		} catch(JSONException e) {
			Log.e(TAG, "link", e);
		}
		mMixHelper.track(OMNOM_ANDROID, "restarateur.link ->", jsonObject);
		return super.link(orderId, amount, tip).doOnNext(new Action1<Restaurant>() {
			@Override
			public void call(Restaurant response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.link <-", response);
			}
		});
	}

	@Override
	public Observable<SupportInfoResponse> getSupportInfo() {
		mMixHelper.track(OMNOM_ANDROID, "restarateur.getSupportInfo ->", StringUtils.EMPTY_STRING);
		return super.getSupportInfo().doOnNext(new Action1<SupportInfoResponse>() {
			@Override
			public void call(SupportInfoResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.getSupportInfo <-", response);
			}
		});
	}

	@Override
	public Observable<Restaurant> getRestaurant(final String restaurantId) {
		mMixHelper.track(OMNOM_ANDROID, "restarateur.getRestaurant ->", restaurantId);
		return super.getRestaurant(restaurantId).doOnNext(new Action1<Restaurant>() {
			@Override
			public void call(Restaurant response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.getRestaurant <-", response);
			}
		});
	}

	@Override
	public Observable<Restaurant> getRestaurant(final String restaurantId, final Func1<Restaurant, Restaurant> funcMap) {
		mMixHelper.track(OMNOM_ANDROID, "restarateur.getRestaurant ->", restaurantId);
		return super.getRestaurant(restaurantId, funcMap).doOnNext(new Action1<Restaurant>() {
			@Override
			public void call(Restaurant response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.getRestaurant <-", response);
			}
		});
	}

	@Override
	public Observable<RestaurantsResponse> getRestaurants(double latitude, double longitude) {
		mMixHelper.track(OMNOM_ANDROID, "restarateur.getRestaurants ->", latitude + " " + longitude);
		return super.getRestaurants(latitude, longitude).doOnNext(new Action1<RestaurantsResponse>() {
			@Override
			public void call(RestaurantsResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.getRestaurants <-", response);
			}
		});
	}

	@Override
	public Observable<RestaurantsResponse> getRestaurants() {
		mMixHelper.track(OMNOM_ANDROID, "restarateur.getRestaurants ->", StringUtils.EMPTY_STRING);
		return super.getRestaurants().doOnNext(new Action1<RestaurantsResponse>() {
			@Override
			public void call(RestaurantsResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.getRestaurants <-", response);
			}
		});
	}

	@Override
	public Observable<RestaurantsResponse> getRestaurantsAll(double latitude, double longitude) {
		mMixHelper.track(OMNOM_ANDROID, "restarateur.getRestaurants ->", latitude + " " + longitude);
		return super.getRestaurantsAll(latitude, longitude).doOnNext(new Action1<RestaurantsResponse>() {
			@Override
			public void call(RestaurantsResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.getRestaurants <-", response);
			}
		});
	}

	@Override
	public Observable<RestaurantsResponse> getRestaurantsAll() {
		mMixHelper.track(OMNOM_ANDROID, "restarateur.getRestaurants ->", StringUtils.EMPTY_STRING);
		return super.getRestaurantsAll().doOnNext(new Action1<RestaurantsResponse>() {
			@Override
			public void call(RestaurantsResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.getRestaurants <-", response);
			}
		});
	}

	@Override
	public Observable<RestaurantResponse> decode(final BeaconDecodeRequest request, final Func1<RestaurantResponse, RestaurantResponse>
			funcMap) {
		mMixHelper.track(OMNOM_ANDROID, "restarateur.decode ->", request);
		return super.decode(request, funcMap).doOnNext(new Action1<RestaurantResponse>() {
			@Override
			public void call(RestaurantResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.decode <-", response);
			}
		});
	}

	@Override
	public Observable<Restaurant> getMenu(final String restaurantId) {
		mMixHelper.track(OMNOM_ANDROID, "restarateur.getMenu ->", restaurantId);
		return super.getMenu(restaurantId).doOnNext(new Action1<Restaurant>() {
			@Override
			public void call(final Restaurant restaurant) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.getMenu <-", restaurant);
			}
		});
	}

	@Override
	public Observable<Collection<OrderItem>> getRecommendations(final String restaurantId) {
		mMixHelper.track(OMNOM_ANDROID, "restarateur.getRecommendations ->", restaurantId);
		return super.getRecommendations(restaurantId).doOnNext(new Action1<Collection<OrderItem>>() {
			@Override
			public void call(final Collection<OrderItem> orderItems) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.getRecommendations <-", orderItems);
			}
		});
	}

	@Override
	public Observable<WishResponse> wishes(final String restId, final WishRequest request) {
		mMixHelper.track(OMNOM_ANDROID, "restarateur.wishes ->", request);
		return super.wishes(restId, request).doOnNext(new Action1<WishResponse>() {
			@Override
			public void call(WishResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.wishes <-", response);
			}
		});
	}

	@Override
	public Observable<RestaurantResponse> decode(final QrDecodeRequest request, final Func1<RestaurantResponse,
			RestaurantResponse> funcMap) {
		mMixHelper.track(OMNOM_ANDROID, "restarateur.decode ->", request);
		return super.decode(request, funcMap).doOnNext(new Action1<RestaurantResponse>() {
			@Override
			public void call(RestaurantResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.decode <-", response);
			}
		});
	}

	@Override
	public Observable<RestaurantResponse> decode(final HashDecodeRequest request, final Func1<RestaurantResponse,
			RestaurantResponse> funcMap) {
		mMixHelper.track(OMNOM_ANDROID, "restarateur.decode ->", request);
		return super.decode(request, funcMap).doOnNext(new Action1<RestaurantResponse>() {
			@Override
			public void call(RestaurantResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "restarateur.decode <-", response);
			}
		});
	}
}
