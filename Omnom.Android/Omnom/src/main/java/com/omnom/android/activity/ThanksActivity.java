package com.omnom.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.socket.event.PaymentSocketEvent;
import com.omnom.android.socket.listener.PaymentEventListener;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.view.HeaderView;

import butterknife.InjectView;

public class ThanksActivity extends BaseOmnomActivity {

	public static void start(Activity activity, Order order, final PaymentSocketEvent paymentEvent, int code, final int color) {
		final Intent intent = new Intent(activity, ThanksActivity.class);
		intent.putExtra(EXTRA_ACCENT_COLOR, color);
		intent.putExtra(EXTRA_ORDER, order);
		if(paymentEvent != null) {
			intent.putExtra(EXTRA_PAYMENT_EVENT, paymentEvent);
		}
		activity.startActivityForResult(intent, code);
	}

	@InjectView(R.id.panel_top)
	protected HeaderView topPanel;

	protected PaymentEventListener mPaymentListener;

	private int mAccentColor;

	@Nullable
	private Order mOrder;

	@Nullable
	private PaymentSocketEvent mPaymentEvent;

	@Override
	public int getLayoutResource() {
		return R.layout.activity_thanks;
	}

	@Override
	protected void handleIntent(final Intent intent) {
		mAccentColor = intent.getIntExtra(EXTRA_ACCENT_COLOR, 0);
		mOrder = intent.getParcelableExtra(Extras.EXTRA_ORDER);
		mPaymentEvent = intent.getParcelableExtra(Extras.EXTRA_PAYMENT_EVENT);
	}

	@Override
	public void initUi() {
		mPaymentListener = new PaymentEventListener(this);
		AndroidUtils.setAccentColor(getWindow(), mAccentColor);
		topPanel.setTitleBig(R.string.bill);
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
	protected void onDestroy() {
		super.onDestroy();
		mPaymentListener.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mPaymentListener.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mPaymentEvent != null) {
			postDelayed(getResources().getInteger(R.integer.default_animation_duration_medium), new Runnable() {
				@Override
				public void run() {
					mPaymentListener.onPaymentEvent(mPaymentEvent);
					mPaymentEvent = null;
				}
			});
		}
		if(mOrder != null) {
			mPaymentListener.initTableSocket(mOrder.getTableId());
		}
	}
}
