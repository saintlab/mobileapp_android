package com.omnom.android.interceptors.mixpanel;

import android.content.Context;

import com.omnom.android.auth.Protocol;
import com.omnom.android.auth.WicketAuthenticator;
import com.omnom.android.auth.request.AuthRegisterRequest;
import com.omnom.android.auth.response.AuthRegisterResponse;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.auth.response.UserResponse;
import com.omnom.android.mixpanel.MixPanelHelper;

import java.util.HashMap;

import retrofit.http.Field;
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
	public Observable<AuthResponse> confirm(String phone, String code) {
		mParams.clear();
		mParams.put("phone", phone);
		mParams.put("code", code);
		mMixHelper.track(OMNOM_ANDROID, "auth.confirm ->", mParams);
		return super.confirm(phone, code).doOnNext(new Action1<AuthResponse>() {
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
	public Observable<AuthResponse> logLocation(double longitude, double latitude, String token) {
		mParams.clear();
		mParams.put("longitude", String.valueOf(longitude));
		mParams.put("latitude", String.valueOf(latitude));
		mParams.put("token", token);
		mMixHelper.track(OMNOM_ANDROID, "auth.logLocation ->", mParams);
		return super.logLocation(longitude, latitude, token).doOnNext(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "auth.logLocation <-", response);
			}
		});
	}

	@Override
	public Observable<AuthResponse> authorizePhone(String phone, String code) {
		mParams.clear();
		mParams.put("phone", phone);
		mParams.put("code", code);
		mMixHelper.track(OMNOM_ANDROID, "auth.authorizePhone ->", mParams);
		return super.authorizePhone(phone, code).doOnNext(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "auth.authorizePhone <-", response);
			}
		});
	}

	@Override
	public Observable<AuthResponse> authorizeEmail(String email, String code) {
		mParams.clear();
		mParams.put("email", email);
		mParams.put("code", code);
		mMixHelper.track(OMNOM_ANDROID, "auth.authorizeEmail ->", mParams);
		return super.authorizeEmail(email, code).doOnNext(new Action1<AuthResponse>() {
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
	public Observable<AuthResponse> authenticate(String username, String password) {
		mParams.clear();
		mParams.put("username", username);
		mParams.put("password", password);
		mMixHelper.track(OMNOM_ANDROID, "auth.authenticate ->", mParams);
		return super.authenticate(username, password).doOnNext(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "auth.authenticate <-", response);
			}
		});
	}

	@Override
	public Observable<AuthResponse> remindPassword(String email) {
		mParams.clear();
		mParams.put("email", email);
		mMixHelper.track(OMNOM_ANDROID, "auth.remindPassword ->", mParams);
		return super.remindPassword(email).doOnNext(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "auth.remindPassword <-", response);
			}
		});
	}

	@Override
	public Observable<AuthResponse> changePhone(@Field(Protocol.FIELD_PHONE) String phone) {
		mParams.clear();
		mParams.put("phone", phone);
		mMixHelper.track(OMNOM_ANDROID, "auth.changePhone ->", mParams);
		return super.changePhone(phone).doOnNext(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				mMixHelper.track(OMNOM_ANDROID, "auth.changePhone <-", response);
			}
		});
	}
}
