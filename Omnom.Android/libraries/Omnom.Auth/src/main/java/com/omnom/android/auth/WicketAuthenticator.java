package com.omnom.android.auth;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omnom.android.auth.request.AuthRegisterRequest;
import com.omnom.android.auth.response.AuthRegisterResponse;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.auth.response.UserResponse;
import com.omnom.android.auth.retrofit.AuthRxSupport;
import com.omnom.android.protocol.BaseRequestInterceptor;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.Field;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Ch3D on 25.09.2014.
 */
public class WicketAuthenticator implements AuthService {
	private final AuthService authService;

	private Context mContext;

	public WicketAuthenticator(final Context context, final String endpoint) {
		mContext = context;

		// final RestAdapter.LogLevel logLevel = BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;
		final RestAdapter.LogLevel logLevel = RestAdapter.LogLevel.FULL;
		final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
		final RestAdapter mRestAdapter = new RestAdapter.Builder()
				.setRxSupport(new AuthRxSupport())
				.setEndpoint(endpoint)
				.setRequestInterceptor(new BaseRequestInterceptor(mContext))
				.setLogLevel(logLevel).setConverter(new GsonConverter(gson)).build();
		authService = mRestAdapter.create(AuthService.class);
	}

	@Override
	public Observable<AuthRegisterResponse> register(final AuthRegisterRequest request) {
		return authService.register(request).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<AuthResponse> confirm(final String phone, final String code) {
		return authService.confirm(phone, code).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<AuthResponse> confirmResend(final String phone) {
		return authService.confirmResend(phone).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<UserResponse> getUser(String token) {
		if(TextUtils.isEmpty(token)) {
			return Observable.just(UserResponse.NULL);
		}
		return authService.getUser(token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<AuthResponse> logLocation(double longitude, double latitude, String token) {
		return authService.logLocation(longitude, latitude, token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<AuthResponse> authorizePhone(String phone, String code) {
		return authService.authorizePhone(phone, code).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<AuthResponse> authorizeEmail(String email, String code) {
		return authService.authorizePhone(email, code).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<AuthResponse> logout(String token) {
		return authService.logout(token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<AuthResponse> authenticate(String username, String password) {
		return authService.authenticate(username, password).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<AuthResponse> remindPassword(String email) {
		return authService.remindPassword(email).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<AuthResponse> changePhone(@Field(Protocol.FIELD_PHONE) String phone) {
		return authService.changePhone(phone).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}
}
