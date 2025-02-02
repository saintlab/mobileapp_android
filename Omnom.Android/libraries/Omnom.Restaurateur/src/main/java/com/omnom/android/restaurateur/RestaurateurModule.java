package com.omnom.android.restaurateur;

import android.content.Context;
import android.text.TextUtils;

import com.omnom.android.protocol.BaseRequestInterceptor;
import com.omnom.android.protocol.Protocol;
import com.omnom.android.restaurateur.api.ConfigDataService;
import com.omnom.android.restaurateur.api.observable.ConfigDataProvider;
import com.omnom.android.restaurateur.api.observable.RestaurateurDataProvider;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;
import com.omnom.android.utils.AuthTokenProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ch3D on 11.08.2014.
 */
@Module(complete = false, library = true)
public class RestaurateurModule {
	private AuthTokenProvider tokenProvider;

	private int mEndpointResId;

	private Context mContext;

	public RestaurateurModule(final AuthTokenProvider tokenProvider, final int endpointResId) {
		this.tokenProvider = tokenProvider;
		this.mEndpointResId = endpointResId;
		this.mContext = tokenProvider.getContext();
	}

	@Provides
	@Singleton
	RestaurateurObservableApi providerRestaurantApi() {
		return RestaurateurDataProvider.create(
				mContext.getString(mEndpointResId),
				new BaseRequestInterceptor(mContext) {
					@Override
					public void intercept(RequestFacade request) {
						super.intercept(request);
						final String token = tokenProvider.getAuthToken();
						if(!TextUtils.isEmpty(token)) {
							request.addHeader(Protocol.HEADER_AUTH_TOKEN, token);
						}
					}
				});
	}

	@Provides
	@Singleton
	ConfigDataService providerConfigApi() {
		return ConfigDataProvider.create(mContext.getString(mEndpointResId), new BaseRequestInterceptor(mContext));
	}
}
