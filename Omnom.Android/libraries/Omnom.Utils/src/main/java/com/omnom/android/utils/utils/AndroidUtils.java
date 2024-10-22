package com.omnom.android.utils.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.SparseBooleanArray;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.omnom.android.utils.OmnomFont;
import com.omnom.android.utils.R;
import com.omnom.android.utils.view.ErrorEdit;

import java.lang.reflect.Method;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

import static butterknife.ButterKnife.findById;

/**
 * Created by Ch3D on 29.07.2014.
 */
public class AndroidUtils {

    public static void clickify(TextView view, final String clickableText,
                                final ClickSpan.OnClickListener listener) {
        clickify(view, true, clickableText, listener);
    }
	
	public interface KeyboardVisibilityListener {
		public void onVisibilityChanged(boolean isVisible);
	}

	public static final int MAX_ANIMATION_INCEREMENT = 20;

	public static final Locale russianLocale = new Locale("ru", "RU");

	public static void clickify(TextView view, final boolean isUnderlined, final String clickableText,
	                            final ClickSpan.OnClickListener listener) {

		CharSequence text = view.getText();
		String string = text.toString();
		ClickSpan span = new ClickSpan(isUnderlined, listener);

		int start = string.indexOf(clickableText);
		int end = start + clickableText.length();
		if(start == -1) {
			return;
		}

		if(text instanceof Spannable) {
			((Spannable) text).setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		} else {
			SpannableString s = SpannableString.valueOf(text);
			s.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			view.setText(s);
		}

		MovementMethod m = view.getMovementMethod();
		if((m == null) || !(m instanceof LinkMovementMethod)) {
			view.setMovementMethod(LinkMovementMethod.getInstance());
		}
		view.setHighlightColor(view.getResources().getColor(android.R.color.transparent));
	}

	public static void sendFeedbackEmail(final Context context, @StringRes int resId, @StringRes int subjectResId) {
		Intent intent = new Intent(Intent.ACTION_SENDTO);
		String email = "team@omnom.menu";
		intent.setData(Uri.parse("mailto:" + email));
		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
		intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(subjectResId));
		context.startActivity(Intent.createChooser(intent, context.getString(resId)));
	}

	public static void showKeyboard(EditText view) {
		view.requestFocus();
		showKeyboard(view.getContext());
	}

	public static void showKeyboard(Context context) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
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

	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if(resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	public static boolean hasNavigationBar(final Context context) {
		boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
		boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
		boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

		return !hasMenuKey;
	}

	public static int getNavigationBarHeight(final Context context) {
		Resources resources = context.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
		if(resourceId > 0) {
			return resources.getDimensionPixelSize(resourceId);
		}
		return 0;
	}

	public static void scrollEnd(final ListView list) {
		list.post(new Runnable() {
			@Override
			public void run() {
				list.setSelection(list.getCount() - 1);
			}
		});
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

	public static String getAppVersion(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch(PackageManager.NameNotFoundException e) {
			return "0.0";
		}
	}

	public static int getAppVersionCode(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch(PackageManager.NameNotFoundException e) {
			return 0;
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

	public static boolean isLollipop() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
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

	public static void applyFont(final Context context, final ViewGroup view, final OmnomFont font) {
		for(int i = 0; i < view.getChildCount(); i++) {
			final View childAt = view.getChildAt(i);
			if(childAt instanceof ViewGroup) {
				applyFont(context, (ViewGroup) childAt, font);
			}
			if(childAt instanceof TextView) {
				CalligraphyUtils.applyFontToTextView(context, (TextView) childAt, font.getPath());
			}
		}
	}

	public static void applyFont(final Context context, final TextView view, final OmnomFont font) {
		CalligraphyUtils.applyFontToTextView(context, view, font.getPath());
	}

	public static void openDialer(final Context context, final String phone) {
		final Uri parse = Uri.parse("tel:" + StringUtils.removeWhitespaces(phone));
		final Intent intent = new Intent(Intent.ACTION_DIAL, parse);
		context.startActivity(intent);
	}

	/**
	 * @param factor from 0 to 1, means 0% to 100%
	 */
	public static int adjustAlpha(int color, float factor) {
		int alpha = Math.round(Color.alpha(color) * factor);
		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);
		return Color.argb(alpha, red, green, blue);
	}

	public static ColorStateList createSelectableColor(int color) {
		int[][] states = new int[][]{
				new int[]{android.R.attr.state_pressed},  // pressed
				new int[]{android.R.attr.state_enabled},  // pressed
				new int[0] // default
		};

		int[] colors = new int[]{
				adjustAlpha(color, 0.35f),
				color,
				color
		};

		return new ColorStateList(states, colors);

	}

	@SuppressWarnings("deprecation")
	public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
		} else {
			v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
		}
	}

	@SuppressWarnings("deprecation")
	public static void setBackground(View v, Drawable drawable) {
		if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			v.setBackgroundDrawable(drawable);
		} else {
			v.setBackground(drawable);
		}
	}

	public static Locale getCurrentLocale(final Context context) {
		return context.getResources().getConfiguration().locale;
	}

	public static boolean isRecyclerItemAnimationSupported() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static int getScreenHeightPixels(final Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	public static void openBrowser(Context context, final String url, final String errMsg) {
		final Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		try {
			context.startActivity(i);
		} catch(ActivityNotFoundException e) {
			Toast.makeText(context, errMsg, Toast.LENGTH_LONG).show();
		}
	}
}
