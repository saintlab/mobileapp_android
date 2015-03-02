package com.omnom.android.notifier;

import android.content.Context;
import android.text.TextUtils;

import com.omnom.android.notifier.api.observable.NotifierDataProvider;
import com.omnom.android.notifier.api.observable.NotifierObservableApi;
import com.omnom.android.protocol.BaseRequestInterceptor;
import com.omnom.android.protocol.Protocol;
import com.omnom.android.utils.AuthTokenProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ch3D on 28.02.2015.
 */
@Module(complete = false, library = true)
public class NotifierModule {
	private final AuthTokenProvider tokenProvider;

	private final int mEndpointResId;

	private final Context mContext;

	public NotifierModule(final AuthTokenProvider tokenProvider, final int endpointResId) {
		this.tokenProvider = tokenProvider;
		this.mEndpointResId = endpointResId;
		this.mContext = tokenProvider.getContext();
	}

	@Provides
	@Singleton
	NotifierObservableApi providerNotifierApi() {
		return NotifierDataProvider.create(mContext.getString(mEndpointResId), new BaseRequestInterceptor(mContext) {
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
}
