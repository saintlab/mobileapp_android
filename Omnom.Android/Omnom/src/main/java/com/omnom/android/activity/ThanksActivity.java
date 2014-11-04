package com.omnom.android.activity;

import android.content.Intent;
import android.view.View;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.view.HeaderView;

import butterknife.InjectView;

public class ThanksActivity extends BaseOmnomActivity {

	public static void start(BaseOmnomActivity activity, int code) {
		activity.startActivityForResult(new Intent(activity, ThanksActivity.class), code);
	}

	@InjectView(R.id.panel_top)
	protected HeaderView topPanel;

	@Override
	public void initUi() {
		topPanel.setButtonLeft(R.string.ready, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				onBackPressed();
			}
		});
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		super.onBackPressed();
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_thanks;
	}
}
