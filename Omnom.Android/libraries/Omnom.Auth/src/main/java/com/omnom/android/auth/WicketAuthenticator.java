package com.omnom.android.auth;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omnom.android.auth.request.AuthRegisterRequest;
import com.omnom.android.auth.response.AuthRegisterResponse;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.auth.response.UserResponse;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
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

		final RestAdapter.LogLevel logLevel = BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;
		final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
		final RestAdapter mRestAdapter = new RestAdapter.Builder()
				.setEndpoint(endpoint)
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
	public Observable<UserResponse> getUser(String token) {
		return authService.getUser(token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
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
}