package com.omnom.android.activity;

import android.view.View;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.view.HeaderView;

import butterknife.InjectView;

public class ChangePhoneSuccessActivity extends BaseOmnomActivity {

	public static final int ERROR_AUTH_UNKNOWN_USER = 101;

	private static final String TAG = ChangePhoneSuccessActivity.class.getSimpleName();

	@InjectView(R.id.panel_top)
	protected HeaderView topPanel;

	@Override
	public void initUi() {
		topPanel.setTitleBig(R.string.change_phone_title);
		topPanel.setButtonRight(R.string.ready, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();
			}
		});
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_change_phone_success;
	}
}
