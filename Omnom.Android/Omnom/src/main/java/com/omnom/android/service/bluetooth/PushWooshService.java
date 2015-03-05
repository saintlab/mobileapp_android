package com.omnom.android.service.bluetooth;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.arellomobile.android.push.DeviceRegistrar;
import com.arellomobile.android.push.PushGCMIntentService;
import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.EnteringActivity;
import com.omnom.android.activity.WebActivity;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.utils.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ch3D on 28.02.2015.
 */
public class PushWooshService extends PushGCMIntentService {

	public static final String ARG_ANDROID_PAYLOAD = "aps";

	public static final String ARG_HASH = "hash";

	public static final String ARG_ALERT = "alert";

	public static final String EVENT_PUSH_RECEIVED = "PUSH_RECEIVED";

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

		String title = getString(R.string.app_name);
		String info = getString(R.string.notification_tap_to_open_omnom);

		try {
			final JSONObject json = new JSONObject(intent.getStringExtra(ARG_ANDROID_PAYLOAD));
			title = json.optString(ARG_ALERT);
			final String[] split = title.split(StringUtils.NEXT_STRING);
			if(split.length > 0) {
				title = split[0];
			}
			if(split.length > 1) {
				info = split[1];
			}
		} catch(JSONException e) {
			Log.e(TAG, "showNotification", e);
		}

		reportPushReceived(context, intent);

		final String hash = intent.getStringExtra(ARG_HASH);

		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

		final Intent hashIntent = new Intent(Intent.ACTION_MAIN);
		hashIntent.setClass(context, EnteringActivity.class);
		hashIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		hashIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		hashIntent.setData(Uri.parse(hash));

		final Notification notification = builder.setAutoCancel(true)
		                                         .setContentTitle(title)
		                                         .setContentText(info)
		                                         .setSmallIcon(R.drawable.ic_app)
		                                         .setDefaults(Notification.DEFAULT_ALL)
		                                         .setTicker(title)
		                                         .setContentIntent(PendingIntent.getActivity(context, 0, hashIntent, 0))
		                                         .setStyle(new NotificationCompat.BigTextStyle().bigText(info))
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

	private void reportPushReceived(final Context context, final Intent intent) {
		if(intent != null) {
			OmnomApplication.getMixPanelHelper(context).track(MixPanelHelper.Project.OMNOM_ANDROID,
			                                                  EVENT_PUSH_RECEIVED,
			                                                  new Object[]{
					                                                  intent.getStringExtra(ARG_ANDROID_PAYLOAD),
					                                                  intent.getStringExtra(ARG_HASH)
			                                                  });
		} else {
			OmnomApplication.getMixPanelHelper(context).track(MixPanelHelper.Project.OMNOM_ANDROID,
			                                                  EVENT_PUSH_RECEIVED,
			                                                  "Intent is empty");
		}
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
