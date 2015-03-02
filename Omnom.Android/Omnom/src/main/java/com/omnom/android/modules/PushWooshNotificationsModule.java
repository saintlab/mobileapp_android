package com.omnom.android.modules;

import android.content.Context;

import com.omnom.android.push.PushNotificationManager;
import com.omnom.android.push.PushNotificationsManagerImpl;
import com.omnom.android.push.providers.pushwoosh.PushWooshNotificationsProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ch3D on 28.02.2015.
 */
@Module(complete = false, library = true)
public class PushWooshNotificationsModule {
	private Context mContext;

	public PushWooshNotificationsModule(final Context context) {
		mContext = context;
	}

	@Provides
	@Singleton
	public PushNotificationManager provideManager() {
		return new PushNotificationsManagerImpl(mContext, new PushWooshNotificationsProvider(mContext));
	}
}
