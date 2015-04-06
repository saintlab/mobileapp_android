package com.omnom.android.push.providers;

import com.omnom.android.push.PushRegistrationListener;

/**
 * Created by Ch3D on 28.02.2015.
 */
public interface PushNotificationsProvider {
	void register();

	void unregister();

	String getDeviceToken();

	void onDestroy();

	void setRegistrationListener(PushRegistrationListener listener);
}
