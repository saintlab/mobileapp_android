package com.omnom.android.service.bluetooth;

import com.arellomobile.android.push.DeviceRegistrar;
import com.arellomobile.android.push.PushGCMIntentService;
import com.omnom.android.BuildConfig;
import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.EnteringActivity;
import com.omnom.android.activity.WebActivity;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.utils.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Ch3D on 28.02.2015.
 */
public class PushWooshService extends PushGCMIntentService {

    private static boolean LOG_ENABLED = BuildConfig.DEBUG;

    public static final String ARG_ANDROID_PAYLOAD = "aps";

    public static final String ARG_HASH = "hash";

    public static final String ARG_ALERT = "alert";

    public static final String ARG_BODY = "body";

    public static final String EVENT_PUSH_RECEIVED = "PUSH_RECEIVED";

    private static final String TAG = PushWooshService.class.getSimpleName();

    @Override
    protected void onRegistered(final Context context, final String registrationId) {
        if (LOG_ENABLED) {
            Log.d(TAG, "onRegistered : " + registrationId);
        }
        DeviceRegistrar.registerWithServer(context, registrationId);
    }

    @Override
    protected void onUnregistered(final Context context, final String registrationId) {
        if (LOG_ENABLED) {
            Log.d(TAG, "onUnregistered : " + registrationId);
        }
        DeviceRegistrar.unregisterWithServer(context, registrationId);
    }

    @Override
    protected void onMessage(final Context context, final Intent intent) {
        if (LOG_ENABLED) {
            Log.d(TAG, "onMessage : " + intent);
        }
        if (Boolean.TRUE.toString().equals(intent.getStringExtra("show_table_orders"))) {
            showNotification(context, intent);
        } else {
            showWishNotification(context, intent);
        }
    }

    private void showWishNotification(final Context context, final Intent intent) {
        String info = getString(R.string.notification_tap_to_open_omnom);

        try {
            final JSONObject json = new JSONObject(intent.getStringExtra(ARG_ANDROID_PAYLOAD));
            final JSONObject objAlert = json.getJSONObject(ARG_ALERT);
            info = objAlert.optString(ARG_BODY, info);
        } catch (JSONException e) {
            Log.e(TAG, "showWishNotification", e);
        }

        notify(context, getString(R.string.app_name), info, getEnteringIntent(context));
    }

    private void notify(Context context, String title, String info, Intent intent) {
        reportPushReceived(context, intent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        final Notification notification = builder.setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(info)
                .setSmallIcon(R.drawable.ic_app)
                .setDefaults(Notification.DEFAULT_ALL)
                .setTicker(title)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, 0))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(info))
                .build();

        NotificationManager nm = (NotificationManager) context
                .getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0, notification);
    }

    private void showNotification(final Context context, final Intent intent) {
        String title = getString(R.string.app_name);
        String info = getString(R.string.notification_tap_to_open_omnom);

        try {
            final JSONObject json = new JSONObject(intent.getStringExtra(ARG_ANDROID_PAYLOAD));
            title = json.optString(ARG_ALERT);
            final String[] split = title.split(StringUtils.NEXT_STRING);
            if (split.length > 0) {
                title = split[0];
            }
            if (split.length > 1) {
                info = split[1];
            }
        } catch (JSONException e) {
            Log.e(TAG, "showNotification", e);
        }

        final String hash = intent.getStringExtra(ARG_HASH);
        notify(context, title, info, getEnteringIntent(context).setData(Uri.parse(hash)));
    }

    private Intent getEnteringIntent(Context context) {
        final Intent hashIntent = new Intent(Intent.ACTION_MAIN);
        hashIntent.setClass(context, EnteringActivity.class);
        hashIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        hashIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        return hashIntent;
    }

    @Deprecated
    private void showWebNotification(final Context context, final Intent data) {
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
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
        if (intent != null) {
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
        if (LOG_ENABLED) {
            Log.d(TAG, "onDeletedMessages : " + s);
        }
    }

    @Override
    protected void onError(final Context context, final String s) {
        Log.d(TAG, "onError : " + s);
    }

    @Override
    protected boolean onRecoverableError(final Context context, final String s) {
        if (LOG_ENABLED) {
            Log.d(TAG, "onRecoverableError : " + s);
        }
        return super.onRecoverableError(context, s);
    }
}