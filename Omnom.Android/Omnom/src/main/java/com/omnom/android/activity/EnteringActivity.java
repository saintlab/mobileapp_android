package com.omnom.android.activity;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.fragment.EnteringFragment;
import com.omnom.android.fragment.SplashFragment;
import com.omnom.android.service.bluetooth.BackgroundBleService;
import com.omnom.android.utils.activity.BaseActivity;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;

import java.util.Arrays;

import butterknife.InjectView;
import butterknife.OnClick;

public class EnteringActivity extends BaseOmnomFragmentActivity implements SplashFragment.LaunchListener {

	private static final String TAG = EnteringActivity.class.getSimpleName();

	public static void start(BaseActivity context, int enterAnim, int exitAnim, int durationSplash, final int type) {
		final Intent intent = createIntent(context, durationSplash, false);
		intent.putExtra(EXTRA_CONFIRM_TYPE, type);
		context.start(intent, enterAnim, exitAnim, true);
	}

	public static void start(BaseActivity context, boolean slipSplash) {
		final Intent intent = createIntent(context, 0, slipSplash);
		context.start(intent, true);
	}

	private static Intent createIntent(BaseActivity context, int durationSplash, boolean skipSplash) {
		final Intent intent = new Intent(context, EnteringActivity.class);
		intent.putExtra(EXTRA_DURATION_SPLASH, durationSplash);
		intent.putExtra(EXTRA_SKIP_SPLASH, skipSplash);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		return intent;
	}

	@InjectView(R.id.panel_bottom)
	protected View mPanelBottom;

	private int durationSplash;

	private boolean skipSplash;

	private SplashFragment splashFragment;

	private Fragment enteringFragment;

	/**
	 * User confirmation type
	 */
	private int mType = ValidateActivity.TYPE_DEFAULT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// workaround for https://code.google.com/p/android/issues/detail?id=2373
		// omnom issue: https://github.com/saintlab/mobileapp_android/issues/224
		if(!isTaskRoot()) {
			Intent intent = getIntent();
			String action = intent.getAction();
			if(intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action != null && action.equals(Intent.ACTION_MAIN)) {
				finish();
				return;
			}
		}

		if (savedInstanceState == null) {
			enteringFragment = EnteringFragment.newInstance();
			if(skipSplash) {
				getSupportFragmentManager().beginTransaction()
				                           .replace(R.id.fragment_container, enteringFragment)
				                           .commit();
				showPanelBottom(false);
			} else {
				// During initial setup, plug in the details fragment.
				splashFragment = SplashFragment.newInstance(durationSplash);
				getSupportFragmentManager().beginTransaction()
				                           .replace(R.id.fragment_container, splashFragment)
				                           .commit();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		boolean hasToken = !TextUtils.isEmpty(getPreferences().getAuthToken(getActivity()));
		if(splashFragment != null) {
			if(hasToken) {
				splashFragment.animateValidation();
			} else {
				splashFragment.animateLogin();
			}
		} else {
			Log.w(TAG, "Splash fragment is null");
		}
	}

	@Override
	public void initUi() {
		if(AndroidUtils.isKitKat()) {
			startBleServiceKK();
		} else if(AndroidUtils.isJellyBeanMR2()) {
			startBleServiceJB();
		}
	}

	@Override
	protected void handleIntent(Intent intent) {
		durationSplash = intent.getIntExtra(EXTRA_DURATION_SPLASH, getResources().getInteger(R.integer.splash_screen_timeout));
		skipSplash = intent.getBooleanExtra(EXTRA_SKIP_SPLASH, false);
		mType = intent.getIntExtra(EXTRA_CONFIRM_TYPE, ValidateActivity.TYPE_DEFAULT);
	}

	@OnClick(R.id.btn_register)
	public void doRegister() {
		final Intent intent = new Intent(this, UserRegisterActivity.class);
		start(intent, R.anim.slide_in_up, R.anim.fake_fade_out_long, false);
	}

	@OnClick(R.id.btn_enter)
	public void doEnter() {
		final Intent intent = new Intent(this, LoginActivity.class);
		start(intent, R.anim.slide_in_up, R.anim.fake_fade_out_long, false);
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void startBleServiceKK() {
		final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		final Intent intent = new Intent(this, BackgroundBleService.class);
		final PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent, 0);
		final long triggerMillis = SystemClock.elapsedRealtime() + (AlarmManager.INTERVAL_FIFTEEN_MINUTES / 15);
		alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerMillis, alarmIntent);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void startBleServiceJB() {
		final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		final Intent intent = new Intent(this, BackgroundBleService.class);
		final PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent, 0);
		final long triggerMillis = SystemClock.elapsedRealtime() + (AlarmManager.INTERVAL_FIFTEEN_MINUTES / 15);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerMillis, alarmIntent);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_splash;
	}

	@Override
	public void launchEnteringScreen() {
		getSupportFragmentManager().beginTransaction()
		                           .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
		                                                R.anim.slide_in_right, R.anim.slide_out_left)
		                           .replace(R.id.fragment_container, enteringFragment)
		                           .commit();
		showPanelBottom(true);
	}

	private void showPanelBottom(boolean isAnimated) {
		if(isAnimated) {
			AnimationUtils.translateUp(getActivity(), Arrays.asList(mPanelBottom),
			                           (int) getResources().getDimension(R.dimen.view_size_default), null,
			                           getResources().getInteger(R.integer.default_animation_duration_medium));
		} else {
			mPanelBottom.setTranslationY(-(int) getResources().getDimension(R.dimen.view_size_default));
		}
	}

	private void hidePanelBottom() {
		mPanelBottom.setTranslationY((int) getResources().getDimension(R.dimen.view_size_default));
	}

	/**
	 * User entering type
	 */
	public int getType() {
		return mType;
	}
}