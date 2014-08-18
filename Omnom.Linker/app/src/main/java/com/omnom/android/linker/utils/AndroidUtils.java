package com.omnom.android.linker.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.omnom.android.linker.R;
import com.omnom.android.linker.widget.loader.LoaderView;

import hugo.weaving.DebugLog;

import static butterknife.ButterKnife.findById;

/**
 * Created by Ch3D on 29.07.2014.
 */
public class AndroidUtils {

	public interface KeyboardVisibilityListener {
		public void onVisibilityChanged(boolean isVisible);
	}

	@DebugLog
	public static void showKeyboard(EditText view) {
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		view.requestFocus();
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}

	@DebugLog
	public static void hideKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	@DebugLog
	public static ViewTreeObserver.OnGlobalLayoutListener createKeyboardListener(final View view,
	                                                                             final KeyboardVisibilityListener listener) {
		return new ViewTreeObserver.OnGlobalLayoutListener() {

			private final Rect r = new Rect();
			private boolean wasOpened;

			@Override
			public void onGlobalLayout() {
				view.getWindowVisibleDisplayFrame(r);

				int heightDiff = view.getRootView().getHeight() - (r.bottom - r.top);
				boolean isOpen = heightDiff > 100;
				if(isOpen == wasOpened) {
					return;
				}
				wasOpened = isOpen;
				listener.onVisibilityChanged(isOpen);
			}
		};
	}

	public static CountDownTimer createTimer(final LoaderView loader, final Runnable finishCallback, final int duration) {
		final Context context = loader.getContext();
		final int progressMax = context.getResources().getInteger(R.integer.loader_progress_max);
		final int timeMax = duration;
		final int tick = context.getResources().getInteger(R.integer.loader_tick_interval);
		final int ticksCount = timeMax / tick;
		final int magic = progressMax / ticksCount;
		loader.updateProgress(0);

		return new CountDownTimer(timeMax, tick) {
			@Override
			public void onTick(long millisUntilFinished) {
				loader.addProgress(magic * 2);
			}

			@Override
			public void onFinish() {
				loader.updateProgress(progressMax);
				if(finishCallback != null) {
					finishCallback.run();
				}
			}
		};
	}

	public static CountDownTimer createTimer(final LoaderView loader, final Runnable finishCallback) {
		return createTimer(loader, finishCallback, loader.getContext().getResources().getInteger(R.integer.loader_time_max));
	}

	@DebugLog
	public static boolean hasConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	}

	@DebugLog
	public static boolean startLocationSettings(Context context) {
		final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		if(intent.resolveActivity(context.getPackageManager()) != null) {
			context.startActivity(intent);
			return true;
		}
		return false;
	}

	@DebugLog
	public static boolean isLocationEnabled(Context context) {
		final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	@DebugLog
	public static void showToast(Context context, int resId) {
		Toast toast = Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT);
		View view = LayoutInflater.from(context).inflate(R.layout.transient_notification, null);
		TextView tv = findById(view, android.R.id.message);
		tv.setText(resId);
		toast.setView(view);
		toast.show();
	}

	@DebugLog
	public static void showToastLong(Context context, int resId) {
		Toast toast = Toast.makeText(context, context.getString(resId), Toast.LENGTH_LONG);
		toast.setView(LayoutInflater.from(context).inflate(R.layout.transient_notification, null));
		toast.show();
	}

	@DebugLog
	public static AlertDialog showDialog(Context context, int msg, int okResId, DialogInterface.OnClickListener okListener,
	                                     int cancelResId, DialogInterface.OnClickListener cancelListener) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context).setMessage(msg).setPositiveButton(okResId, okListener)
				.setNegativeButton(cancelResId, cancelListener).create();
		alertDialog.setCancelable(false);
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();
		return alertDialog;
	}
}
