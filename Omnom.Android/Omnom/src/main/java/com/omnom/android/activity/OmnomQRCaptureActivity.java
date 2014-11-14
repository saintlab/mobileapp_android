package com.omnom.android.activity;

import android.view.View;
import android.widget.Button;

import com.google.zxing.client.android.CaptureActivity;
import com.omnom.android.R;

/**
 * Created by Ch3D on 14.11.2014.
 */
public class OmnomQRCaptureActivity extends CaptureActivity {

	private Button mBtnDemo;

	@Override
	public int getLayoutResource() {
		return R.layout.activity_capture_qr;
	}

	@Override
	protected void initUI() {
		super.initUI();
		mBtnDemo = (Button) findViewById(R.id.btn_demo);
		mBtnDemo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				ValidateActivity.start(OmnomQRCaptureActivity.this,
				                       R.anim.fake_fade_in_short,
				                       R.anim.fake_fade_out_short,
				                       EXTRA_LOADER_ANIMATION_SCALE_DOWN, true);
			}
		});
	}
}
