package com.omnom.android.push;

import android.content.Context;
import android.util.Log;

import com.omnom.android.OmnomApplication;
import com.omnom.android.notifier.api.observable.NotifierObservableApi;
import com.omnom.android.push.providers.PushNotificationsProvider;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;

import javax.inject.Inject;

import rx.functions.Action1;

/**
 * Created by Ch3D on 28.02.2015.
 */
public class PushNotificationsManagerImpl implements PushNotificationManager, PushRegistrationListener {

	private static final String TAG = PushNotificationsManagerImpl.class.getSimpleName();

	private final Context mContext;

	private final PushNotificationsProvider mProvider;

	@Inject
	protected RestaurateurObservableApi api;

	@Inject
	protected NotifierObservableApi notifierApi;

	public PushNotificationsManagerImpl(final Context context, final PushNotificationsProvider provider) {
		mContext = context;
		mProvider = provider;
		mProvider.setRegistrationListener(this);
		OmnomApplication.get(mContext).inject(this);
	}

	@Override
	public void register() {
		mProvider.register();
	}

	@Override
	public void unregister() {
		mProvider.unregister();
	}

	@Override
	public void onRegistered() {
		notifierApi.register(mProvider.getDeviceToken()).subscribe(new Action1() {
			@Override
			public void call(final Object o) {
				Log.d(TAG, "onRegistered : " + o);
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(final Throwable throwable) {
				Log.d(TAG, "onRegistered", throwable);
			}
		});
	}

	@Override
	public void onUnregistered() {
		Log.d(TAG, "onUnregistered");
	}
}
