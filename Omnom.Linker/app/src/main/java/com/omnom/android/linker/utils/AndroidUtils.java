package com.omnom.android.linker.utils;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.omnom.android.linker.R;

import static butterknife.ButterKnife.findById;

/**
 * Created by Ch3D on 29.07.2014.
 */
public class AndroidUtils {

	public static boolean hasConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	}

	public static boolean startLocationSettings(Context context) {
		final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		if (intent.resolveActivity(context.getPackageManager()) != null) {
			context.startActivity(intent);
			return true;
		}
		return false;
	}

	public static boolean isLocationEnabled(Context context) {
		final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	public static void showToast(Context context, int resId) {
		Toast toast = Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT);
		View view = LayoutInflater.from(context).inflate(R.layout.transient_notification, null);
		TextView tv = findById(view, android.R.id.message);
		tv.setText(resId);
		toast.setView(view);
		toast.show();
	}

	public static void showToastLong(Context context, int resId) {
		Toast toast = Toast.makeText(context, context.getString(resId), Toast.LENGTH_LONG);
		toast.setView(LayoutInflater.from(context).inflate(R.layout.transient_notification, null));
		toast.show();
	}
}
