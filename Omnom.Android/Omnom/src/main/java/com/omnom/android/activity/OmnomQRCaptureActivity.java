package com.omnom.android.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.PreferencesActivity;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.camera.FrontLightMode;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.fragment.EditHashFragment;
import com.omnom.android.fragment.QrHintFragment;
import com.omnom.android.menu.api.observable.MenuObservableApi;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;
import com.omnom.android.restaurateur.model.decode.RestaurantResponse;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.ClickSpan;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by Ch3D on 14.11.2014.
 */
public class OmnomQRCaptureActivity extends CaptureActivity
									implements QrHintFragment.FragmentCloseListener,
											   EditHashFragment.EnterHashPanelCloseListener,
											   EditHashFragment.TableFoundListener,
											   CameraManager.TorchListener {

	private static final String TAG = OmnomQRCaptureActivity.class.getSimpleName();

	private static final String ENTER_HASH_PANEL = "enter_hash_panel";
	private static final String QR_HINT = "qr_hint";

	public static final int RESULT_RESTAURANT_FOUND = 2;

	private static final int LAUNCH_DELAY = 2000;

	private static final int SCAN_DELAY = 5000;

	private class LaunchAnimationListener implements Animator.AnimatorListener {

		private final View background;

		public LaunchAnimationListener(final View background) {
			this.background = background;
		}

		@Override
		public void onAnimationStart(Animator animation) {

		}

		@Override
		public void onAnimationEnd(Animator animation) {
			ViewUtils.setVisible(background, false);
		}

		@Override
		public void onAnimationCancel(Animator animation) {

		}

		@Override
		public void onAnimationRepeat(Animator animation) {

		}
	}

	public static void start(final BaseOmnomActivity activity, final int code) {
		final Intent intent = getIntent(activity);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			final ActivityOptions activityOptions = ActivityOptions
					.makeCustomAnimation(activity, com.omnom.android.zxing.R.anim.slide_in_right,
					                     com.omnom.android.zxing.R.anim.slide_out_left);
			activity.startActivityForResult(intent, code, activityOptions.toBundle());
		} else {
			activity.startActivityForResult(intent, code);
		}
	}

	private static Intent getIntent(final Context context) {
		Intent intent = new Intent(context, OmnomQRCaptureActivity.class);
		intent.setAction(Intents.Scan.ACTION);
		intent.putExtra(Intents.Scan.FORMATS, BarcodeFormat.QR_CODE.name());
		intent.putExtra(Intents.Scan.SAVE_HISTORY, false);
		return intent;
	}

	public static void start(final BaseOmnomFragmentActivity activity, final int code) {
		final Intent intent = getIntent(activity);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			final ActivityOptions activityOptions = ActivityOptions
					.makeCustomAnimation(activity, com.omnom.android.zxing.R.anim.slide_in_right,
					                     com.omnom.android.zxing.R.anim.slide_out_left);
			activity.startActivityForResult(intent, code, activityOptions.toBundle());
		} else {
			activity.startActivityForResult(intent, code);
		}
	}

	@InjectView(R.id.btn_not_scanning)
	protected View btnNotScanning;

	@InjectView(R.id.scan_frame_container)
	protected View scanFrameContainer;

	@InjectView(R.id.scan_frame)
	protected View scanFrame;

	@InjectView(R.id.background)
	protected View background;

	@InjectView(R.id.animation_background)
	protected View animationBackground;

	@InjectView(R.id.btn_flash_light)
	protected ImageView btnFlashLight;

	@Inject
	protected RestaurateurObservableApi api;

	@Inject
	protected MenuObservableApi menuApi;

	protected Func1<RestaurantResponse, RestaurantResponse> mPreloadBackgroundFunction;

	private Subscription mCheckQrSubscription;

	private boolean isError = false;

	private boolean isBusy = false;

	private boolean isFlashTurnedOn = false;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(PreferencesActivity.KEY_DISABLE_EXPOSURE, true);
		editor.putBoolean(PreferencesActivity.KEY_DISABLE_CONTINUOUS_FOCUS, true);
		editor.putString(PreferencesActivity.KEY_FRONT_LIGHT_MODE, FrontLightMode.OFF.name());
		editor.apply();
		setTorchListener(this);
		animationBackground.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_capture_qr;
	}

	@Override
	protected void initUI() {
		super.initUI();
		playLaunchAnimation();
		final TextView txtHint = (TextView) findViewById(R.id.txt_hint);
		AndroidUtils.clickify(txtHint, false, getString(R.string.navigate_qr_code_mark),
				new ClickSpan.OnClickListener() {
					@Override
					public void onClick() {
						showHint();
					}
				});

		final View activityRootView = ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				AndroidUtils.createKeyboardListener(activityRootView, new AndroidUtils.KeyboardVisibilityListener() {
					@Override
					public void onVisibilityChanged(boolean isVisible) {
						if (!isVisible) {
							closeEnterHashPanel();
						}
					}
				}));
		
	}


	private void showEnterHashPanel() {
		getSupportFragmentManager().beginTransaction()
				.addToBackStack(null)
				.replace(R.id.fragment_container, EditHashFragment.newInstance(), ENTER_HASH_PANEL)
				.commit();
	}

	private void closeEnterHashPanel() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				FragmentManager fragmentManager = getSupportFragmentManager();
				Fragment fragment = fragmentManager.findFragmentByTag(ENTER_HASH_PANEL);
				if(fragment != null) {
					fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
					AndroidUtils.hideKeyboard(getActivity());
					setNotScanningButtonVisible(true);
				}
			}
		});
	}

	private void playLaunchAnimation() {
		final DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
		scanFrameContainer.setTranslationY(-displayMetrics.heightPixels);
		postDelayed(LAUNCH_DELAY, new Runnable() {
			@Override
			public void run() {
				final int duration = getResources().getInteger(R.integer.default_animation_duration_medium);
				scanFrameContainer.animate()
				                  .translationYBy(displayMetrics.heightPixels)
				                  .setDuration(duration)
				                  .start();
				background.animate()
						.translationYBy(displayMetrics.heightPixels)
						.setDuration(duration)
						.start();

				launchScanningDelayHandler();
			}
		});

		ViewTreeObserver viewTreeObserver = btnNotScanning.getViewTreeObserver();
		if (viewTreeObserver.isAlive()) {
			viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					AndroidUtils.removeOnGlobalLayoutListener(btnNotScanning, this);
					btnNotScanning.setTranslationY(btnNotScanning.getHeight());

					final DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
					final int smallestDimension = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
					final int framingRectSize = (smallestDimension + scanFrame.getWidth()) / 2;
					int[] scanFrameCoordinates = new int[2];
					scanFrame.getLocationInWindow(scanFrameCoordinates);
					final int scanFrameHalfSize = scanFrame.getWidth() / 2;
					final int framingRectHalfSize = framingRectSize / 2;
					final int left = scanFrameCoordinates[0] + scanFrameHalfSize - framingRectHalfSize;
					final int top = (displayMetrics.heightPixels + scanFrameCoordinates[1]) + scanFrameHalfSize - framingRectHalfSize;
					setFramingRect(new Rect(left, top, left + framingRectSize, top + framingRectSize));
				}
			});
		}
	}

	private void launchScanningDelayHandler() {
		postDelayed(SCAN_DELAY, new Runnable() {
			@Override
			public void run() {
				setNotScanningButtonVisible(true);
			}
		});
	}

	@OnClick(R.id.btn_not_scanning)
	protected void onNotScanning() {
		turnTheLightOff(true);
		showEnterHashPanel();
	}

	@OnClick(R.id.btn_flash_light)
	protected void onBtnFlash() {
		if (getTorchState()) {
			turnTheLightOff(true);
		} else {
			turnTheLightOn(true);
		}
	}

	private void turnTheLightOn(final boolean isManual) {
		setTorch(true, isManual);
	}

	private void turnTheLightOff(final boolean isManual) {
		setTorch(false, isManual);
	}

	private void showHint() {
		setNotScanningButtonVisible(false);
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.slide_in_up,
                        R.anim.slide_out_down,
                        R.anim.slide_in_up,
                        R.anim.slide_out_down)
                .replace(R.id.fragment_container, QrHintFragment.newInstance(), QR_HINT)
                .commit();
    }

	private void setNotScanningButtonVisible(final boolean isVisible) {
		if (isNotScanningButtonVisible() == isVisible) {
			return;
		}
		final int duration = getResources().getInteger(R.integer.not_scanning_animation_duration);
		btnNotScanning.animate()
				.translationYBy(btnNotScanning.getHeight() * (isVisible ? -1 : 1))
				.setDuration(duration)
				.start();
	}

	private boolean isNotScanningButtonVisible() {
		return btnNotScanning.getTranslationY() == 0;
	}

	@Override
	public void onFragmentClose() {
		setNotScanningButtonVisible(true);
	}

	@Override
	protected void onStop() {
		super.onStop();
		btnFlashLight.setImageResource(R.drawable.ic_flashlight_on);
		closeEnterHashPanel();
	}

	@Override
	public void onTorchStateChange(boolean isTurnedOn) {
		btnFlashLight.setImageResource(isTurnedOn ? R.drawable.ic_flashlight_off :
									                R.drawable.ic_flashlight_on);
	}

	@Override
	public void onEnterHashPanelClose() {
		closeEnterHashPanel();
	}

	@Override
	public void onTableFound(String requestId, Restaurant restaurant, Menu menu) {
		finish(requestId, restaurant, menu);
	}

	private void finish(final String requestId, final Restaurant restaurant) {
		Intent data = new Intent();
		data.putExtra(EXTRA_REQUEST_ID, requestId);
		data.putExtra(EXTRA_RESTAURANT, restaurant);
		data.putExtra(EXTRA_RESTAURANT_MENU, menu);
		setResult(RESULT_RESTAURANT_FOUND, data);
		finish();
	}

}
