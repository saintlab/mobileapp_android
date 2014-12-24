package com.omnom.android.mixpanel;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.mixpanel.android.mpmetrics.GCMReceiver;
import com.mixpanel.android.mpmetrics.MPConfig;
import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.utils.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ch3D on 24.12.2014.
 */
public class MixpanelGCMReceiver extends GCMReceiver {
	@SuppressWarnings("unused")
	private static final String LOGTAG = "MixpanelAPI.GCMReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		if("com.google.android.c2dm.intent.REGISTRATION".equals(action)) {
			handleRegistrationIntent(context, intent);
		} else if("com.google.android.c2dm.intent.RECEIVE".equals(action)) {
			handleNotificationIntent(context, intent);
		}
	}

	private void handleRegistrationIntent(final Context context, Intent intent) {
		final String registration = intent.getStringExtra("registration_id");
		if(intent.getStringExtra("error") != null) {
			Log.e(LOGTAG, "Error when registering for GCM: " + intent.getStringExtra("error"));
		} else if(registration != null) {
			if(MPConfig.DEBUG) {
				Log.d(LOGTAG, "Registering GCM ID: " + registration);
			}
			OmnomApplication.getMixPanel(context).getPeople().setPushRegistrationId(registration);
		} else if(intent.getStringExtra("unregistered") != null) {
			if(MPConfig.DEBUG) {
				Log.d(LOGTAG, "Unregistering from GCM");
			}
			OmnomApplication.getMixPanel(context).getPeople().clearPushRegistrationId();
		}
	}

	private void handleNotificationIntent(Context context, Intent intent) {
		final String message = intent.getStringExtra("mp_message");
		final String url = intent.getStringExtra("open_url");
		final String apsString = intent.getStringExtra("aps");
		String soundName = StringUtils.EMPTY_STRING;

		if(!TextUtils.isEmpty(apsString)) {
			try {
				final JSONObject aps = new JSONObject(apsString);
				soundName = aps.optString("sound", StringUtils.EMPTY_STRING);
				if(!TextUtils.isEmpty(soundName)) {
					final int end = soundName.indexOf(".");
					if(end > 0) {
						soundName = soundName.substring(0, end);
					}
				}
			} catch(JSONException e) {
				Log.d(LOGTAG, "Unable to parse json: " + apsString);
			}
		}

		if(message == null) {
			return;
		}
		if(MPConfig.DEBUG) {
			Log.d(LOGTAG, "MP GCM notification received: " + message);
		}

		final Intent appIntent = new Intent(Intent.ACTION_VIEW);
		appIntent.setData(Uri.parse(url));

		CharSequence notificationTitle = context.getString(R.string.app_name);
		final PendingIntent contentIntent = PendingIntent.getActivity(
				context.getApplicationContext(),
				0,
				appIntent, // add this pass null to intent
				PendingIntent.FLAG_UPDATE_CURRENT);

		final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		final Notification notification;
		if(Build.VERSION.SDK_INT >= 16) {
			notification = makeNotificationSDK16OrHigher(context, contentIntent, R.drawable.ic_app, notificationTitle, message);
		} else if(Build.VERSION.SDK_INT >= 11) {
			notification = makeNotificationSDK11OrHigher(context, contentIntent, R.drawable.ic_app, notificationTitle, message);
		} else {
			notification = makeNotificationSDKLessThan11(context, contentIntent, R.drawable.ic_app, notificationTitle, message);
		}
		notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + soundName);
		notificationManager.notify(0, notification);
	}

	@SuppressWarnings("deprecation")
	@TargetApi(8)
	private Notification makeNotificationSDKLessThan11(Context context, PendingIntent intent, int notificationIcon, CharSequence title,
	                                                   CharSequence message) {
		final Notification n = new Notification(notificationIcon, message, System.currentTimeMillis());
		n.flags |= Notification.FLAG_AUTO_CANCEL;
		return n;
	}

	@SuppressWarnings("deprecation")
	@TargetApi(11)
	private Notification makeNotificationSDK11OrHigher(Context context, PendingIntent intent, int notificationIcon, CharSequence title,
	                                                   CharSequence message) {
		final Notification.Builder builder = new Notification.Builder(context).
				                                                                      setSmallIcon(notificationIcon).
				                                                                      setTicker(message).
				                                                                      setWhen(System.currentTimeMillis()).
				                                                                      setContentTitle(title).
				                                                                      setContentText(message).
				                                                                      setContentIntent(intent);
		final Notification n = builder.getNotification();
		n.flags |= Notification.FLAG_AUTO_CANCEL;
		return n;
	}

	@SuppressLint("NewApi")
	@TargetApi(16)
	private Notification makeNotificationSDK16OrHigher(Context context, PendingIntent intent, int notificationIcon, CharSequence title,
	                                                   CharSequence message) {
		final Notification.Builder builder = new Notification.Builder(context).
				                                                                      setSmallIcon(notificationIcon).
				                                                                      setTicker(message).
				                                                                      setWhen(System.currentTimeMillis()).
				                                                                      setContentTitle(title).
				                                                                      setContentText(message).
				                                                                      setContentIntent(intent).
				                                                                      setStyle(new Notification.BigTextStyle().bigText(
						                                                                      message));
		final Notification n = builder.build();
		n.flags |= Notification.FLAG_AUTO_CANCEL;
		return n;
	}
}
