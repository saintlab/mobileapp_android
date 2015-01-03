package com.omnom.android.modules;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.omnom.android.interceptors.mixpanel.RestaurateurMixpanelProxy;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.protocol.BaseRequestInterceptor;
import com.omnom.android.protocol.Protocol;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.utils.AuthTokenProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ch3D on 03.01.2015.
 */
@Module(complete = false, library = true)
public class RestuarateurMixpanelModule {
	public static final String PLATFORM_ANDROID = "Android";

	private static final String sManufacturer = Build.MANUFACTURER;

	private static final String sModel = Build.MODEL;

	private static final String sApiLevel = String.valueOf(Build.VERSION.SDK_INT);

	private AuthTokenProvider tokenProvider;

	private int mEndpointResId;

	private MixPanelHelper mMixPanelHelper;

	private Context mContext;

	public RestuarateurMixpanelModule(final AuthTokenProvider tokenProvider, final int endpointResId, final MixPanelHelper mixPanelHelper) {
		this.tokenProvider = tokenProvider;
		this.mEndpointResId = endpointResId;
		mMixPanelHelper = mixPanelHelper;
		this.mContext = tokenProvider.getContext();
	}

	@Provides
	@Singleton
	RestaurateurObeservableApi providerLinkerApi() {
		return RestaurateurMixpanelProxy.create(
				mContext.getString(mEndpointResId),
				new BaseRequestInterceptor(mContext) {
					@Override
					public void intercept(RequestFacade request) {
						super.intercept(request);
						final String token = tokenProvider.getAuthToken();
						if(!TextUtils.isEmpty(token)) {
							request.addHeader(Protocol.HEADER_AUTH_TOKEN, token);
						}
						request.addHeader(Protocol.HEADER_X_MOBILE_VENDOR, sManufacturer);
						request.addHeader(Protocol.HEADER_X_MOBILE_MODEL, sModel);
						request.addHeader(Protocol.HEADER_X_MOBILE_OS_VERSION, sApiLevel);
						request.addHeader(Protocol.HEADER_X_MOBILE_PLATFORM, PLATFORM_ANDROID);
					}
				}, mMixPanelHelper);
	}
}
