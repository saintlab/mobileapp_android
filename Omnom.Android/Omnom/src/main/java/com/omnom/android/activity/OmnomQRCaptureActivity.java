package com.omnom.android.activity;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.PreferencesActivity;
import com.google.zxing.client.android.camera.FrontLightMode;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.fragment.QrHintFragment;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.ClickSpan;
import com.omnom.android.utils.utils.ViewUtils;

/**
 * Created by Ch3D on 14.11.2014.
 */
public class OmnomQRCaptureActivity extends CaptureActivity {

	private static final int LAUNCH_DELAY = 2000;

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

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PreferencesActivity.KEY_FRONT_LIGHT_MODE, FrontLightMode.OFF.name());
		editor.apply();
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_capture_qr;
	}

	@Override
	protected void initUI() {
		super.initUI();
        final TextView txtHint = (TextView) findViewById(R.id.txt_hint);
        final View background = findViewById(R.id.background);
        final View camera = findViewById(R.id.img_camera);
        AndroidUtils.clickify(txtHint, getString(R.string.navigate_qr_code_mark),
                new ClickSpan.OnClickListener() {
                    @Override
                    public void onClick() {
                        showHint();
                    }
                });
		final View scanFrame = findViewById(R.id.scan_frame);
		final DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
		scanFrame.setTranslationY(-displayMetrics.heightPixels);
		postDelayed(LAUNCH_DELAY, new Runnable() {
			@Override
			public void run() {
				final int duration = getResources().getInteger(R.integer.default_animation_duration_medium);
				scanFrame.animate()
						.translationYBy(displayMetrics.heightPixels)
						.setDuration(duration)
						.start();
				background.animate()
						.translationYBy(displayMetrics.heightPixels)
						.setDuration(duration)
						.setListener(new LaunchAnimationListener(camera, background))
						.start();
			}
		});

        ViewTreeObserver viewTreeObserver = scanFrame.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    AndroidUtils.removeOnGlobalLayoutListener(scanFrame, this);
	                final DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
	                setFramingRect(new Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels));
                }
            });
        }
	}

    private void showHint() {
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.slide_in_up,
                        R.anim.slide_out_down,
                        R.anim.slide_in_up,
                        R.anim.slide_out_down)
                .replace(R.id.fragment_container, QrHintFragment.newInstance())
                .commit();
    }

	private class LaunchAnimationListener implements Animator.AnimatorListener {

		private final View camera;
		private final View background;

		public LaunchAnimationListener(final View camera, final View background) {
			this.camera = camera;
			this.background = background;
		}

		@Override
		public void onAnimationStart(Animator animation) {

		}

		@Override
		public void onAnimationEnd(Animator animation) {
			ViewUtils.setVisible(background, false);
			AnimationUtils.animateBlinking(camera);
		}

		@Override
		public void onAnimationCancel(Animator animation) {

		}

		@Override
		public void onAnimationRepeat(Animator animation) {

		}
	}

}
