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
import com.google.gson.Gson;
import com.omnom.android.R;
import com.omnom.android.utils.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ch3D on 28.02.2015.
 */
public class PushWooshService extends PushGCMIntentService {

	private static final String TAG = PushWooshService.class.getSimpleName();

	private Gson mGson;

	@Override
	public void onCreate() {
		super.onCreate();
		mGson = new Gson();
	}

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
		showNotification(context, intent);
	}

	private void showNotification(final Context context, final Intent intent) {
		String title = StringUtils.EMPTY_STRING;
		try {
			final JSONObject json = new JSONObject(intent.getStringExtra("aps"));
			title = json.optString("alert", "Приятного аппетита!");
		} catch(JSONException e) {
			e.printStackTrace();
		}
		final String hash = intent.getStringExtra("hash");
		final String type = intent.getStringExtra("type");

		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		final Intent hashIntent = new Intent(Intent.ACTION_VIEW);
		hashIntent.addCategory(Intent.CATEGORY_DEFAULT);
		hashIntent.addCategory(Intent.CATEGORY_BROWSABLE);
		hashIntent.setData(Uri.parse(hash));

		final Notification notification = builder.setAutoCancel(true)
		                                         .setContentTitle(title)
		                                         .setSmallIcon(R.drawable.ic_app)
		                                         .setDefaults(Notification.DEFAULT_ALL)
		                                         .setTicker(title)
		                                         .setContentIntent(PendingIntent.getActivity(context, 0, hashIntent, 0))
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
