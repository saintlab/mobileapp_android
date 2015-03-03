package com.omnom.android.service.bluetooth;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.arellomobile.android.push.DeviceRegistrar;
import com.arellomobile.android.push.PushGCMIntentService;
import com.omnom.android.R;
import com.omnom.android.activity.WebActivity;

/**
 * Created by Ch3D on 28.02.2015.
 */
public class PushWooshService extends PushGCMIntentService {

	private static final String TAG = PushWooshService.class.getSimpleName();

	@Override
	protected void onRegistered(final Context context, final String registrationId) {
		Log.d(TAG, "onRegistered : " + registrationId);
		DeviceRegistrar.registerWithServer(context, registrationId);
	}

	@Override
	protected void onUnregistered(final Context context, final String registrationId) {
		Log.d(TAG, "onUnregistered : " + registrationId);
		DeviceRegistrar.unregisterWithServer(context, registrationId);
	}

	@Override
	protected void onMessage(final Context context, final Intent intent) {
		Log.d(TAG, "onMessage : " + intent);
		showWebNotification(context, intent);
	}

	private void showNotification(final Context context, final Intent intent) {
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		final Notification notification = builder.setAutoCancel(true)
		                                         .setContentTitle("TITLE")
		                                         .setContentInfo("INFO")
		                                         .setSmallIcon(R.drawable.ic_app)
		                                         .setDefaults(Notification.DEFAULT_ALL)
		                                         .setTicker("TICKER")
		                                         .build();
		nm.notify(0, notification);
	}

	private void showWebNotification(final Context context, final Intent data) {
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

		final PendingIntent activityIntent = PendingIntent.getActivity(context,
		                                                               0,
		                                                               WebActivity.createIntent(context, "http://www.android.com/"),
		                                                               0);
		final Notification notification = builder.setAutoCancel(true)
		                                         .setSmallIcon(R.drawable.ic_app)
		                                         .setDefaults(Notification.DEFAULT_ALL)
		                                         .setTicker("Ваш заказ готов")
		                                         .setContentTitle("Ваш заказ готов")
		                                         .setContentIntent(activityIntent)
		                                         .setContentInfo("Нажмите, чтобы посмотреть детали")
		                                         .build();
		nm.notify(0, notification);
	}

	@Override
	protected void onDeletedMessages(final Context context, final String s) {
		Log.d(TAG, "onDeletedMessages : " + s);
	}

	@Override
	protected void onError(final Context context, final String s) {
		Log.d(TAG, "onError : " + s);
	}

	@Override
	protected boolean onRecoverableError(final Context context, final String s) {
		Log.d(TAG, "onRecoverableError : " + s);
		return super.onRecoverableError(context, s);
	}
}
