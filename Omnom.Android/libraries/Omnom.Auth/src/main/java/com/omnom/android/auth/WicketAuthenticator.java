package com.omnom.android.auth;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omnom.android.auth.request.AuthRegisterRequest;
import com.omnom.android.auth.request.UserAuthLoginPassRequest;
import com.omnom.android.auth.request.UserAuthorizeByPhoneRequest;
import com.omnom.android.auth.request.UserAuthorizeEmailByRequest;
import com.omnom.android.auth.request.UserConfirmPhoneRequest;
import com.omnom.android.auth.request.UserLogLocationRequest;
import com.omnom.android.auth.request.UserPhoneConfirmResendRequest;
import com.omnom.android.auth.request.UserRecoverPhoneRequest;
import com.omnom.android.auth.request.UserRemindEmailRequest;
import com.omnom.android.auth.request.UserUpdateRequest;
import com.omnom.android.auth.response.AuthRegisterResponse;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.auth.response.UserResponse;
import com.omnom.android.auth.retrofit.AuthRxSupport;
import com.omnom.android.protocol.BaseRequestInterceptor;

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
	public Observable<AuthResponse> confirm(final UserConfirmPhoneRequest userConfirmPhoneRequest) {
		return authService.confirm(userConfirmPhoneRequest).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<AuthResponse> confirmResend(final UserPhoneConfirmResendRequest userPhoneConfirmResendRequest) {
		return authService.confirmResend(userPhoneConfirmResendRequest).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<UserResponse> getUser(String token) {
		if(TextUtils.isEmpty(token)) {
			return Observable.just(UserResponse.NULL);
		}
		return authService.getUser(token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<AuthResponse> logLocation(final UserLogLocationRequest userLogLocationRequest) {
		return authService.logLocation(userLogLocationRequest).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<AuthResponse> authorizePhone(final UserAuthorizeByPhoneRequest userAuthorizeByPhoneRequest) {
		return authService.authorizePhone(userAuthorizeByPhoneRequest).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<AuthResponse> authorizeEmail(final UserAuthorizeEmailByRequest userAuthorizeEmailByRequest) {
		return authService.authorizeEmail(userAuthorizeEmailByRequest).subscribeOn(Schedulers.io()).observeOn(
				AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<AuthResponse> logout(String token) {
		return authService.logout(token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<AuthResponse> authenticate(final UserAuthLoginPassRequest userAuthLoginPassRequest) {
		return authService.authenticate(userAuthLoginPassRequest).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<AuthResponse> remindPassword(final UserRemindEmailRequest userRemindEmailRequest) {
		return authService.remindPassword(userRemindEmailRequest).subscribeOn(Schedulers.io())
		                  .observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<AuthResponse> changePhone(final UserRecoverPhoneRequest userRecoverPhoneRequest) {
		return authService.changePhone(userRecoverPhoneRequest).subscribeOn(Schedulers.io())
		                  .observeOn(AndroidSchedulers.mainThread());
	}

	@Override
	public Observable<UserResponse> updateUser(final UserUpdateRequest userUpdateRequest) {
		return authService.updateUser(userUpdateRequest).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}
}
