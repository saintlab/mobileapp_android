package com.omnom.android.menu;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.omnom.android.menu.api.observable.MenuDataProvider;
import com.omnom.android.menu.api.observable.MenuObservableApi;
import com.omnom.android.protocol.BaseRequestInterceptor;
import com.omnom.android.protocol.Protocol;
import com.omnom.android.utils.AuthTokenProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ch3D on 26.01.2015.
 */
@Module(complete = false, library = true)
public class MenuModule {

	public static final String PLATFORM_ANDROID = "Android";

	private static final String sManufacturer = Build.MANUFACTURER;

	private static final String sModel = Build.MODEL;

	private static final String sApiLevel = String.valueOf(Build.VERSION.SDK_INT);

	private final AuthTokenProvider tokenProvider;

	private final int mEndpointResId;

	private final Context mContext;

	public MenuModule(final AuthTokenProvider tokenProvider, final int endpointResId) {
		this.tokenProvider = tokenProvider;
		this.mEndpointResId = endpointResId;
		this.mContext = tokenProvider.getContext();
	}

	@Provides
	@Singleton
	MenuObservableApi providerLinkerApi() {
		return MenuDataProvider.create(mContext.getString(mEndpointResId), new BaseRequestInterceptor(mContext) {
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
