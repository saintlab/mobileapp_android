package com.omnom.android.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.google.zxing.client.android.CaptureActivity;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.utils.utils.AnimationUtils;

/**
 * Created by Ch3D on 14.11.2014.
 */
public class OmnomQRCaptureActivity extends CaptureActivity {

	private int tableNumber;
	private String tableId;

	public static void start(final BaseOmnomActivity activity, final int tableNumber,
	                         final String tableId, final int code) {
		final Intent intent = new Intent(activity, OmnomQRCaptureActivity.class);
		intent.putExtra(CaptureActivity.EXTRA_SHOW_BACK, false);
		intent.putExtra(CaptureActivity.EXTRA_TABLE_NUMBER, tableNumber);
		intent.putExtra(CaptureActivity.EXTRA_TABLE_ID, tableId);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			final ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(activity, com.omnom.android.zxing.R.anim.slide_in_right,
					com.omnom.android.zxing.R.anim.slide_out_left);
			activity.startActivityForResult(intent, code, activityOptions.toBundle());
		} else {
			activity.startActivityForResult(intent, code);
		}
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_capture_qr;
	}

	@Override
	protected void initUI() {
		super.initUI();
		final View btnDemo = findViewById(R.id.btn_demo);
		btnDemo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				ValidateActivity.start(OmnomQRCaptureActivity.this,
						R.anim.fake_fade_in_instant,
						R.anim.fake_fade_out_instant,
						EXTRA_LOADER_ANIMATION_SCALE_DOWN, true);
			}
		});
		final View imgProfile = findViewById(R.id.img_profile);
		imgProfile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UserProfileActivity.startSliding(OmnomQRCaptureActivity.this, tableNumber, tableId);
			}
		});
		final View background = findViewById(R.id.background);
		postDelayed(2000, new Runnable() {
			@Override
			public void run() {
				AnimationUtils.animateAlpha(background, false);
			}
		});
	}

	@Override
	protected void handleIntent(Intent intent) {
		super.handleIntent(intent);
		tableNumber = intent.getIntExtra(EXTRA_TABLE_NUMBER, 0);
		tableId = intent.getStringExtra(EXTRA_TABLE_ID);
	}

}
