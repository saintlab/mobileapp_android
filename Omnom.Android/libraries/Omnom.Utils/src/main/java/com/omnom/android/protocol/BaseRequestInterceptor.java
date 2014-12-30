package com.omnom.android.protocol;

import android.os.Build;

import retrofit.RequestInterceptor;

/**
 * Created by Ch3D on 30.12.2014.
 */
public class BaseRequestInterceptor implements RequestInterceptor {

	public static final String PLATFORM_ANDROID = "Android";

	private static final String sManufacturer = Build.MANUFACTURER;

	private static final String sModel = Build.MODEL;

	private static final String sApiLevel = String.valueOf(Build.VERSION.SDK_INT);

	public BaseRequestInterceptor() {
	}

	@Override
	public void intercept(final RequestFacade request) {
		request.addHeader(Protocol.HEADER_X_MOBILE_VENDOR, sManufacturer);
		request.addHeader(Protocol.HEADER_X_MOBILE_MODEL, sModel);
		request.addHeader(Protocol.HEADER_X_MOBILE_OS_VERSION, sApiLevel);
		request.addHeader(Protocol.HEADER_X_MOBILE_PLATFORM, PLATFORM_ANDROID);
	}
}
