package com.omnom.android.interceptors.mixpanel;

import android.content.Context;

import com.omnom.android.auth.request.UserAuthorizeByPhoneRequest;
import com.omnom.android.auth.request.UserAuthorizeEmailByRequest;
import com.omnom.android.auth.request.UserConfirmPhoneRequest;
import com.omnom.android.auth.request.UserLogLocationRequest;
import com.omnom.android.auth.request.UserAuthLoginPassRequest;
import com.omnom.android.auth.request.UserRemindEmailRequest;
import com.omnom.android.auth.WicketAuthenticator;
import com.omnom.android.auth.request.AuthRegisterRequest;
import com.omnom.android.auth.request.UserRecoverPhoneRequest;
import com.omnom.android.auth.response.AuthRegisterResponse;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.auth.response.UserResponse;
import com.omnom.android.mixpanel.MixPanelHelper;

import java.util.HashMap;

import rx.Observable;
import rx.functions.Action1;

import static com.omnom.android.mixpanel.MixPanelHelper.Project.OMNOM_ANDROID;

/**
 * Created by Ch3D on 03.10.2014.
 */
public class WicketMixpanelAuthenticator extends WicketAuthenticator {
	private final HashMap<String, String> mParams;
	private final MixPanelHelper mMixHelper;

	public WicketMixpanelAuthenticator(Context context, String endpoint, MixPanelHelper helper) {
		super(context, endpoint);
		mParams = new HashMap<String, String>();
		mMixHelper = helper;
	}

	@Override
	public Observable<AuthRegisterResponse> register(final AuthRegisterRequest request) {
		mMixHelper.track(OMNOM_ANDROID, "auth.register ->", request);
		return super.register(request).doOnNext(new Action1<AuthRegisterResponse>() {
			@Override
			public void call(AuthRegisterResponse authRegisterResponse) {
				mMixHelper.track(OMNOM_ANDROID, "auth.register <-", authRegisterResponse);
			}
		});
	}

	@Override
	public Observable<AuthResponse> confirm(final UserConfirmPhoneRequest userConfirmPhoneRequest) {
		mParams.clear();
		mParams.put("phone", userConfirmPhoneRequest.getPhone());
		mParams.put("code", userConfirmPhoneRequest.getCode());
		mMixHelper.track(OMNOM_ANDROID, "auth.confirm ->", mParams);
		return super.confirm(userConfirmPhoneRequest).doOnNext(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "auth.confirm <-", response);
			}
		});
	}

	@Override
	public Observable<UserResponse> getUser(String token) {
		mParams.clear();
		mParams.put("token", token);
		mMixHelper.track(OMNOM_ANDROID, "auth.getUser ->", mParams);
		return super.getUser(token).doOnNext(new Action1<UserResponse>() {
			@Override
			public void call(UserResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "auth.getUser <-", response);
			}
		});
	}

	@Override
	public Observable<AuthResponse> logLocation(final UserLogLocationRequest userLogLocationRequest) {
		mParams.clear();
		mParams.put("longitude", String.valueOf(userLogLocationRequest.getLongitude()));
		mParams.put("latitude", String.valueOf(userLogLocationRequest.getLatitude()));
		mParams.put("token", userLogLocationRequest.getToken());
		mMixHelper.track(OMNOM_ANDROID, "auth.logLocation ->", mParams);
		return super.logLocation(
				userLogLocationRequest).doOnNext(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "auth.logLocation <-", response);
			}
		});
	}

	@Override
	public Observable<AuthResponse> authorizePhone(final UserAuthorizeByPhoneRequest userAuthorizeByPhoneRequest) {
		mParams.clear();
		mParams.put("phone", userAuthorizeByPhoneRequest.getPhone());
		mParams.put("code", userAuthorizeByPhoneRequest.getCode());
		mMixHelper.track(OMNOM_ANDROID, "auth.authorizePhone ->", mParams);
		return super.authorizePhone(userAuthorizeByPhoneRequest).doOnNext(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "auth.authorizePhone <-", response);
			}
		});
	}

	@Override
	public Observable<AuthResponse> authorizeEmail(final UserAuthorizeEmailByRequest userAuthorizeEmailByRequest) {
		mParams.clear();
		mParams.put("email", userAuthorizeEmailByRequest.getEmail());
		mParams.put("code", userAuthorizeEmailByRequest.getCode());
		mMixHelper.track(OMNOM_ANDROID, "auth.authorizeEmail ->", mParams);
		return super.authorizeEmail(userAuthorizeEmailByRequest).doOnNext(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "auth.authorizeEmail <-", response);
			}
		});
	}

	@Override
	public Observable<AuthResponse> logout(String token) {
		mParams.clear();
		mParams.put("token", token);
		mMixHelper.track(OMNOM_ANDROID, "auth.logout ->", mParams);
		return super.logout(token).doOnNext(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "auth.logout <-", response);
			}
		});
	}

	@Override
	public Observable<AuthResponse> authenticate(final UserAuthLoginPassRequest userAuthLoginPassRequest) {
		mParams.clear();
		mParams.put("login", userAuthLoginPassRequest.getLogin());
		mParams.put("password", userAuthLoginPassRequest.getPassword());
		mMixHelper.track(OMNOM_ANDROID, "auth.authenticate ->", mParams);
		return super.authenticate(userAuthLoginPassRequest).doOnNext(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "auth.authenticate <-", response);
			}
		});
	}

	@Override
	public Observable<AuthResponse> remindPassword(final UserRemindEmailRequest userRemindEmailRequest) {
		mParams.clear();
		mParams.put("email", userRemindEmailRequest.getEmail());
		mMixHelper.track(OMNOM_ANDROID, "auth.remindPassword ->", mParams);
		return super.remindPassword(userRemindEmailRequest).doOnNext(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "auth.remindPassword <-", response);
			}
		});
	}

	@Override
	public Observable<AuthResponse> changePhone(final UserRecoverPhoneRequest userRecoverPhoneRequest) {
		mParams.clear();
		mParams.put("phone", userRecoverPhoneRequest.getPhone());
		mMixHelper.track(OMNOM_ANDROID, "auth.changePhone ->", mParams);
		return super.changePhone(userRecoverPhoneRequest).doOnNext(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "auth.changePhone <-", response);
			}
		});
	}
}
