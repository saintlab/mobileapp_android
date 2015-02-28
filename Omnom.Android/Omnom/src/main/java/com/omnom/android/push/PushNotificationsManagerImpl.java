package com.omnom.android.push;

import android.content.Context;

import com.omnom.android.push.providers.PushNotificationsProvider;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;

import javax.inject.Inject;

/**
 * Created by Ch3D on 28.02.2015.
 */
public class PushNotificationsManagerImpl implements PushNotificationManager, PushRegistrationListener {
	private final Context mContext;

	private final PushNotificationsProvider mProvider;

	@Inject
	protected RestaurateurObservableApi api;

	public PushNotificationsManagerImpl(final Context context, final PushNotificationsProvider provider) {
		mContext = context;
		mProvider = provider;
		mProvider.setRegistrationListener(this);
	}

	public void register() {
		mProvider.register();
	}

	public void unregister() {
		mProvider.unregister();
	}

	@Override
	public void onRegistered() {
		// TODO: Send to notifier-backend
	}

	@Override
	public void onUnregistered() {
		// TODO: Send to notifier-backend
	}
}
