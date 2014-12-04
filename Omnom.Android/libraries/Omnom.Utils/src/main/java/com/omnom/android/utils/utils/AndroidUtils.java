package com.omnom.android.utils.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.omnom.android.utils.R;
import com.omnom.android.utils.view.ErrorEdit;

import java.lang.reflect.Method;

import static butterknife.ButterKnife.findById;

/**
 * Created by Ch3D on 29.07.2014.
 */
public class AndroidUtils {

	public interface KeyboardVisibilityListener {
		public void onVisibilityChanged(boolean isVisible);
	}

	public static final int MAX_ANIMATION_INCEREMENT = 20;

	public static void showKeyboard(EditText view) {
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		view.requestFocus();
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}

	public static void hideKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static void hideKeyboard(EditText view, ResultReceiver resultReceiver) {
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0, resultReceiver);
	}

	public static void hideKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(activity.getWindow().peekDecorView().getWindowToken(), 0);
	}

	public static void scrollEnd(final ListView list) {
		list.setSelection(list.getCount() - 1);
	}

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

	public static boolean hasConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	}

	public static boolean startLocationSettings(Context context) {
		final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		if(intent.resolveActivity(context.getPackageManager()) != null) {
			context.startActivity(intent);
			return true;
		}
		return false;
	}

	public static boolean isLocationEnabled(Context context) {
		final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	public static void showToast(View view, int resId) {
		showToast(view.getContext(), resId);
	}

	public static void showToast(Context context, int resId) {
		Toast toast = Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT);
		View view = LayoutInflater.from(context).inflate(R.layout.transient_notification, null);
		TextView tv = findById(view, android.R.id.message);
		tv.setText(resId);
		toast.setView(view);
		toast.show();
	}

	public static void showToast(Context context, String msg) {
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		View view = LayoutInflater.from(context).inflate(R.layout.transient_notification, null);
		TextView tv = findById(view, android.R.id.message);
		tv.setText(msg);
		toast.setView(view);
		toast.show();
	}

	public static void showToastLong(Context context, int resId) {
		Toast toast = Toast.makeText(context, context.getString(resId), Toast.LENGTH_LONG);
		View view = LayoutInflater.from(context).inflate(R.layout.transient_notification, null);
		TextView tv = findById(view, android.R.id.message);
		tv.setText(resId);
		toast.setView(view);
		toast.show();
	}

	public static void showToastLong(View view, int resId) {
		showToastLong(view.getContext(), resId);
	}

	public static AlertDialog showDialog(Context context, int msg, int okResId, DialogInterface.OnClickListener okListener,
	                                     int cancelResId, DialogInterface.OnClickListener cancelListener) {
		return showDialog(context, context.getString(msg), okResId, okListener, cancelResId, cancelListener);
	}

	public static AlertDialog showDialog(Context context, String msg, int okResId, DialogInterface.OnClickListener okListener,
	                                     int cancelResId, DialogInterface.OnClickListener cancelListener) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context).setMessage(msg).setPositiveButton(okResId,
		                                                                                                   okListener)
		                                                                .setNegativeButton(cancelResId, cancelListener).create();
		alertDialog.setCancelable(false);
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();
		return alertDialog;
	}

	public static String getAppVersion(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch(PackageManager.NameNotFoundException e) {
			return "0.0";
		}
	}

	public static String getInstallId(Context context) {
		return getAndroidId(context) + getDeviceId(context) + getSerialNumber();
	}

	public static String getAndroidId(Context context) {
		return Settings.Secure.getString(context.getContentResolver(),
		                                 Settings.Secure.ANDROID_ID);
	}

	public static String getDeviceId(Context context) {
		final TelephonyManager systemService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return systemService.getDeviceId();
	}

	public static String getSerialNumber() {
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class, String.class);
			return (String) (get.invoke(c, "ro.serialno", "unknown"));
		} catch(Exception ignored) {
			return StringUtils.EMPTY_STRING;
		}
	}

	public static boolean isJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static void moveCursorEnd(final EditText editText) {
		editText.setSelection(editText.getText().length());
	}

	public static void moveCursorEnd(final ErrorEdit edit) {
		final EditText editText = edit.getEditText();
		editText.setSelection(edit.getText().length());
	}

	public static boolean isValidEmail(CharSequence target) {
		return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	}

	public static String getDevicePhoneNumber(final Context context, final int defaultResId) {
		final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		final String mPhoneNumber = telephonyManager.getLine1Number();
		return TextUtils.isEmpty(mPhoneNumber) ? context.getString(defaultResId) : mPhoneNumber;
	}

	public static void setAccentColor(Window window, int color) {
		if(window == null || window.getDecorView() == null) {
			return;
		}
		if(window.getDecorView().getBackground() != null) {
			final Drawable background = window.getDecorView().getBackground();
			background.mutate();
			background.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
			background.invalidateSelf();
		}
	}

	public static void clearAccentColor(Window window) {
		final Drawable background = window.getDecorView().getBackground();
		background.mutate();
		background.setColorFilter(null);
		background.invalidateSelf();
	}

	public static boolean isJellyBeanMR2() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
	}

	public static boolean isKitKat() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	}

	public static boolean hasSelectedItems(SparseBooleanArray array, int keyLimit) {
		final int size = array.size();
		for(int i = 0; i < size; i++) {
			int key = array.keyAt(i);
			if(array.get(key) && key < keyLimit) {
				return true;
			}
		}
		return false;
	}
}
