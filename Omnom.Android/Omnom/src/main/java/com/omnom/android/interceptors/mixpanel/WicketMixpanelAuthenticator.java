package com.omnom.android.interceptors.mixpanel;

import android.content.Context;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.omnom.android.auth.WicketAuthenticator;
import com.omnom.android.auth.request.AuthRegisterRequest;
import com.omnom.android.auth.response.AuthRegisterResponse;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.auth.response.UserResponse;

import java.util.HashMap;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Ch3D on 03.10.2014.
 */
public class WicketMixpanelAuthenticator extends WicketAuthenticator {
	private final HashMap<String, String> mParams;
	private final MixPanelHelper mMixHelper;

	public WicketMixpanelAuthenticator(Context context, String endpoint, MixpanelAPI mixpanelApi) {
		super(context, endpoint);
		mParams = new HashMap<String, String>();
		mMixHelper = new MixPanelHelper(mixpanelApi);
	}

	@Override
	public Observable<AuthRegisterResponse> register(final AuthRegisterRequest request) {
		mMixHelper.track("auth.register ->", request);
		return super.register(request).doOnNext(new Action1<AuthRegisterResponse>() {
			@Override
			public void call(AuthRegisterResponse authRegisterResponse) {
				mMixHelper.track("auth.register <-", authRegisterResponse);
			}
		});
	}

	@Override
	public Observable<AuthResponse> confirm(String phone, String code) {
		mParams.clear();
		mParams.put("phone", phone);
		mParams.put("code", code);
		mMixHelper.track("auth.confirm ->", mParams);
		return super.confirm(phone, code).doOnNext(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				mMixHelper.track("auth.confirm <-", response);
			}
		});
	}

	@Override
	public Observable<UserResponse> getUser(String token) {
		mParams.clear();
		mParams.put("token", token);
		mMixHelper.track("auth.getUser ->", mParams);
		return super.getUser(token).doOnNext(new Action1<UserResponse>() {
			@Override
			public void call(UserResponse response) {
				mMixHelper.track("auth.getUser <-", response);
			}
		});
	}

	@Override
	public Observable<AuthResponse> authorizePhone(String phone, String code) {
		mParams.clear();
		mParams.put("phone", phone);
		mParams.put("code", code);
		mMixHelper.track("auth.authorizePhone ->", mParams);
		return super.authorizePhone(phone, code).doOnNext(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				mMixHelper.track("auth.authorizePhone <-", response);
			}
		});
	}

	@Override
	public Observable<AuthResponse> authorizeEmail(String email, String code) {
		mParams.clear();
		mParams.put("email", email);
		mParams.put("code", code);
		mMixHelper.track("auth.authorizeEmail ->", mParams);
		return super.authorizeEmail(email, code).doOnNext(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				mMixHelper.track("auth.authorizeEmail <-", response);
			}
		});
	}

	@Override
	public Observable<AuthResponse> logout(String token) {
		mParams.clear();
		mParams.put("token", token);
		mMixHelper.track("auth.logout ->", mParams);
		return super.logout(token).doOnNext(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				mMixHelper.track("auth.logout <-", response);
			}
		});
	}

	@Override
	public Observable<AuthResponse> authenticate(String username, String password) {
		mParams.clear();
		mParams.put("username", username);
		mParams.put("password", password);
		mMixHelper.track("auth.authenticate ->", mParams);
		return super.authenticate(username, password).doOnNext(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				mMixHelper.track("auth.authenticate <-", response);
			}
		});
	}

	@Override
	public Observable<AuthResponse> remindPassword(String email) {
		mParams.clear();
		mParams.put("email", email);
		mMixHelper.track("auth.remindPassword ->", mParams);
		return super.remindPassword(email).doOnNext(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				mMixHelper.track("auth.remindPassword <-", response);
			}
		});
	}
}
