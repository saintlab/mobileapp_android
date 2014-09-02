package com.omnom.android.linker.api.observable.providers;

import android.text.TextUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omnom.android.linker.BuildConfig;
import com.omnom.android.linker.api.AuthService;
import com.omnom.android.linker.api.LinkerDataService;
import com.omnom.android.linker.api.Protocol;
import com.omnom.android.linker.api.observable.LinkerObeservableApi;
import com.omnom.android.linker.model.UserProfile;
import com.omnom.android.linker.model.beacon.BeaconBindRequest;
import com.omnom.android.linker.model.beacon.BeaconBuildRequest;
import com.omnom.android.linker.model.beacon.BeaconDataResponse;
import com.omnom.android.linker.model.beacon.BeaconFindRequest;
import com.omnom.android.linker.model.qrcode.QRCodeBindRequest;
import com.omnom.android.linker.model.restaurant.Restaurant;
import com.omnom.android.linker.model.restaurant.RestaurantsResponse;
import com.omnom.android.linker.model.table.TableDataResponse;

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
public class LinkerDataProvider implements LinkerObeservableApi, RequestInterceptor {
	private final RestAdapter mRestAdapter;
	private final RestAdapter mAuthAdapter;

	private final LinkerDataService mDataService;
	private final AuthService mAuthService;

	private String mAuthToken;

	public LinkerDataProvider(final String endPoint) {
		final RestAdapter.LogLevel logLevel = BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;
		final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
		final GsonConverter converter = new GsonConverter(gson);

		mRestAdapter = new RestAdapter.Builder().setRequestInterceptor(this).setEndpoint(endPoint).setLogLevel(logLevel)
		                                        .setConverter(converter).build();
		mDataService = mRestAdapter.create(LinkerDataService.class);

		mAuthAdapter = new RestAdapter.Builder().setRequestInterceptor(this).setEndpoint("https://wicket.saintlab.com").setLogLevel(logLevel)
		                                        .setConverter(converter).build();
		mAuthService = mRestAdapter.create(AuthService.class);
		
		// TODO: Remove
		setAuthToken("stub_auth_token");
	}

	@Override
	public Observable<UserProfile> getUserProfile(String authToken) {
		return mAuthService.getUserProfile(authToken).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<String> authenticate(String username, String password) {
		return mAuthService.authenticate(username, password).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<String> remindPassword(String username) {
		return mAuthService.remindPassword(username).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<TableDataResponse> findBeacon(Beacon beacon) {
		return mDataService.findBeacon(new BeaconFindRequest(beacon)).subscribeOn(
				Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<BeaconDataResponse> bindBeacon(String restaurantId, int tableNumber, Beacon beacon) {
		final BeaconBindRequest request = new BeaconBindRequest(restaurantId, tableNumber, beacon);
		return mDataService.bindBeacon(request).subscribeOn(
				Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<BeaconDataResponse> bindBeacon(String restaurantId, int tableNumber, BeaconDataResponse beaconData) {
		final BeaconBindRequest request = new BeaconBindRequest(restaurantId, tableNumber, beaconData);
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
	public Observable<Restaurant> getRestaurant(String restaurantId) {
		return mDataService.getRestaurant(restaurantId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<RestaurantsResponse> getRestaurants() {
		return mDataService.getRestaurants().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public void setAuthToken(final String token) {
		mAuthToken = token;
	}

	@Override
	public Observable<BeaconDataResponse> buildBeacon(String restaurantId, int tableNumber, String uuid) {
//		Response response = new Response("", HttpStatus.SC_UNAUTHORIZED, "", Collections.EMPTY_LIST, new TypedInput() {
//			@Override
//			public String mimeType() {
//				return null;
//			}
//
//			@Override
//			public long length() {
//				return 0;
//			}
//
//			@Override
//			public InputStream in() throws IOException {
//				return null;
//			}
//		});
//		return Observable.error(RetrofitError.httpError("", response, null, null));
		return mDataService.buildBeacon(new BeaconBuildRequest(uuid, String.valueOf(tableNumber), restaurantId))
		                   .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public void intercept(RequestFacade request) {
		if(!TextUtils.isEmpty(mAuthToken)) {
			request.addHeader(Protocol.HEADER_AUTH_TOKEN, mAuthToken);
		}
	}
}
