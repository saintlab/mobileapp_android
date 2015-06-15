package com.omnom.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.activity.helper.OmnomActivityHelper;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.socket.ActivityPaymentBroadcastReceiver;
import com.omnom.android.socket.PaymentEventIntentFilter;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.view.HeaderView;

import butterknife.InjectView;

public class ThanksActivity extends BaseOmnomActivity {

	public static void start(Activity activity, Order order, int code, final int color) {
		final Intent intent = new Intent(activity, ThanksActivity.class);
		intent.putExtra(EXTRA_ACCENT_COLOR, color);
		intent.putExtra(EXTRA_ORDER, order);
		activity.startActivityForResult(intent, code);
	}

	@InjectView(R.id.panel_top)
	protected HeaderView topPanel;

	private int mAccentColor;

	@Nullable
	private Order mOrder;

	private ActivityPaymentBroadcastReceiver mPaymentReceiver;

	private PaymentEventIntentFilter mPaymentFilter;

	@Override
	public int getLayoutResource() {
		return R.layout.activity_thanks;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPaymentReceiver = new ActivityPaymentBroadcastReceiver(this);
		mPaymentFilter = new PaymentEventIntentFilter(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
	}

	@Override
	protected void handleIntent(final Intent intent) {
		mAccentColor = intent.getIntExtra(EXTRA_ACCENT_COLOR, 0);
		mOrder = intent.getParcelableExtra(Extras.EXTRA_ORDER);
	}

	@Override
	public void initUi() {
		AndroidUtils.setAccentColor(getWindow(), mAccentColor);
		topPanel.setTitleBig(R.string.bill)
		        .setButtonLeft(R.string.ready, new View.OnClickListener() {
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
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mPaymentReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mPaymentReceiver, mPaymentFilter);
		OmnomActivityHelper.processPaymentEvents(getActivity());
	}
}
