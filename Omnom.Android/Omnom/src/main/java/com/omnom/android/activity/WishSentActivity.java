package com.omnom.android.activity;

import android.content.Intent;
import android.widget.ImageView;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.utils.activity.OmnomActivity;

import butterknife.InjectView;
import butterknife.OnClick;

public class WishSentActivity extends BaseOmnomActivity {

	public static void start(OmnomActivity activity, final int code) {
		final Intent intent = new Intent(activity.getActivity(), WishSentActivity.class);
		activity.startForResult(intent, R.anim.slide_in_up, R.anim.nothing_long, code);
	}

	@InjectView(R.id.btn_close)
	protected ImageView imgClose;

	@OnClick(R.id.btn_close)
	public void onClose() {
		finish();
	}

	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
		overridePendingTransition(R.anim.nothing, R.anim.slide_out_down);
	}

	@Override
	public void initUi() {
		// Do nothing
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_wish_sent;
	}
}
