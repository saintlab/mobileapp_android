package com.omnom.android.push.providers.pushwoosh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.arellomobile.android.push.BasePushMessageReceiver;
import com.arellomobile.android.push.PushManager;
import com.arellomobile.android.push.utils.RegisterBroadcastReceiver;
import com.omnom.android.push.providers.PushNotificationsProviderBase;
import com.omnom.android.push.PushRegistrationListener;

/**
 * Created by Ch3D on 28.02.2015.
 */
public class PushWooshNotificationsProvider extends PushNotificationsProviderBase {

	private static final String TAG = PushWooshNotificationsProvider.class.getSimpleName();

	//Push message receiver
	private BroadcastReceiver mReceiver = new BasePushMessageReceiver() {
		@Override
		protected void onMessageReceive(Intent intent) {
			//JSON_DATA_KEY contains JSON payload of push notification.
			Log.d(TAG, "push message is " + intent.getExtras().getString(JSON_DATA_KEY));
		}
	};

	private final PushManager mPwManager;

	//Registration receiver
	BroadcastReceiver mBroadcastReceiver = new RegisterBroadcastReceiver() {
		@Override
		public void onRegisterActionReceive(Context context, Intent intent) {
			checkMessage(intent);
		}
	};

	private PushRegistrationListener mListener;

	public PushWooshNotificationsProvider(Context context) {
		super(context);
		registerReceivers();
		mPwManager = PushManager.getInstance(context);
		try {
			mPwManager.onStartup(mContext);
		} catch(Exception e) {
			Log.d(TAG, "PushManager.onStartup", e);
		}
	}

	private void checkMessage(Intent intent) {
		if(null != intent) {
			if(intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT)) {
				Log.d(TAG, "checkMessage : push message is " + intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT));
			} else if(intent.hasExtra(PushManager.REGISTER_EVENT)) {
				Log.d(TAG, "checkMessage : register");
				if(mListener != null) {
					mListener.onRegistered();
				}
			} else if(intent.hasExtra(PushManager.UNREGISTER_EVENT)) {
				Log.d(TAG, "checkMessage : unregister");
				if(mListener != null) {
					mListener.onUnregistered();
				}
			} else if(intent.hasExtra(PushManager.REGISTER_ERROR_EVENT)) {
				Log.d(TAG, "checkMessage : register error");
			} else if(intent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT)) {
				Log.d(TAG, "checkMessage : unregister error");
			}
		}
	}

	public void registerReceivers() {
		final String packageName = mContext.getPackageName();
		IntentFilter intentFilter = new IntentFilter(packageName + ".action.PUSH_MESSAGE_RECEIVE");
		mContext.registerReceiver(mReceiver, intentFilter, packageName + ".permission.C2D_MESSAGE", null);
		mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(packageName + "." + PushManager.REGISTER_BROAD_CAST_ACTION));
	}

	public void unregisterReceivers() {
		try {
			mContext.unregisterReceiver(mReceiver);
		} catch(Exception e) {
			Log.d(TAG, "unregisterReceivers", e);
		}

		try {
			mContext.unregisterReceiver(mBroadcastReceiver);
		} catch(Exception e) {
			Log.d(TAG, "unregisterReceivers", e);
		}
	}

	@Override
	public void register() {
		mPwManager.registerForPushNotifications();
	}

	@Override
	public void unregister() {
		mPwManager.unregisterForPushNotifications();
	}

	@Override
	public String getDeviceToken() {
		return PushManager.getPushToken(mContext);
	}

	@Override
	public void onDestroy() {
		unregisterReceivers();
	}

	@Override
	public void setRegistrationListener(final PushRegistrationListener listener) {
		mListener = listener;
	}
}
