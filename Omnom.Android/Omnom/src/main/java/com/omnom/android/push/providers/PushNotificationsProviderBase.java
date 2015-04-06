package com.omnom.android.push.providers;

import android.content.Context;

import com.omnom.android.push.providers.PushNotificationsProvider;

/**
 * Created by Ch3D on 28.02.2015.
 */
public abstract class PushNotificationsProviderBase implements PushNotificationsProvider {

	protected Context mContext;

	public PushNotificationsProviderBase(Context context) {
		mContext = context;
	}

}
