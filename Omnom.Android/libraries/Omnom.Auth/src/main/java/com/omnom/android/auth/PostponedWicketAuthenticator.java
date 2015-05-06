package com.omnom.android.auth;

import android.content.Context;
import android.text.TextUtils;

import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.auth.response.UserResponse;

import rx.Observable;

/**
 * Created by Ch3D on 16.04.2015.
 */
public class PostponedWicketAuthenticator extends WicketAuthenticator {
	public PostponedWicketAuthenticator(final Context context, final String endpoint) {
		super(context, endpoint);
	}

	@Override
	public Observable<UserResponse> getUser(final String token) {
		if(TextUtils.isEmpty(token)) {
			return Observable.just(UserResponse.NULL);
		}
		return super.getUser(token);
	}

	@Override
	public Observable<AuthResponse> logLocation(final double longitude, final double latitude, final String token) {
		if(TextUtils.isEmpty(token)) {
			return Observable.just(AuthResponse.create(AuthResponse.STATUS_SUCCESS, null));
		}
		return super.logLocation(longitude, latitude, token);
	}

	@Override
	public Observable<AuthResponse> logout(final String token) {
		if(TextUtils.isEmpty(token)) {
			return Observable.just(AuthResponse.create(AuthResponse.STATUS_SUCCESS, null));
		}
		return super.logout(token);
	}
}
