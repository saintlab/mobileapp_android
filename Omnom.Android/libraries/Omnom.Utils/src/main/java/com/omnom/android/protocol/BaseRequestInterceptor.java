package com.omnom.android.protocol;

import android.content.Context;
import android.os.Build;

import com.omnom.android.utils.utils.AndroidUtils;

import retrofit.RequestInterceptor;

/**
 * Created by Ch3D on 30.12.2014.
 */
public class BaseRequestInterceptor implements RequestInterceptor {

	public static final String PLATFORM_ANDROID = "Android";

	private static final String sManufacturer = Build.MANUFACTURER;

	private static final String sModel = Build.MODEL;

	private static final String sApiLevel = String.valueOf(Build.VERSION.SDK_INT);

	private final String mAppVersion;

	private final int mAppVersionCode;

	private Context mContext;

	public BaseRequestInterceptor(Context context) {
		mContext = context;
		mAppVersion = AndroidUtils.getAppVersion(mContext);
		mAppVersionCode = AndroidUtils.getAppVersionCode(mContext);
	}

	@Override
	public void intercept(final RequestFacade request) {
		request.addHeader(Protocol.HEADER_X_CURRENT_APP_VERSION, mAppVersion);
		request.addHeader(Protocol.HEADER_X_CURRENT_APP_BUILD, String.valueOf(mAppVersionCode));

		request.addHeader(Protocol.HEADER_X_MOBILE_VENDOR, sManufacturer);
		request.addHeader(Protocol.HEADER_X_MOBILE_MODEL, sModel);
		request.addHeader(Protocol.HEADER_X_MOBILE_OS_VERSION, sApiLevel);
		request.addHeader(Protocol.HEADER_X_MOBILE_PLATFORM, PLATFORM_ANDROID);
	}
}
