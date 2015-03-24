package com.omnom.android.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.view.HeaderView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.InjectView;

public abstract class BaseOrderAcceptedActivity extends BaseOmnomActivity {

	protected final SimpleDateFormat ORDER_TIME_FORMAT = new SimpleDateFormat("dd MMMM в HH:mm", AndroidUtils.russianLocale);

	public static void startBar(Activity activity, String orderNumber, String pinCode,
	                            int requestCode, final int accentColor) {
		final Intent intent = new Intent(activity, BarOrderAcceptedActivity.class);
		intent.putExtra(EXTRA_ORDER_NUMBER, orderNumber);
		intent.putExtra(EXTRA_PIN_CODE, pinCode);
		intent.putExtra(EXTRA_ACCENT_COLOR, accentColor);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void startLunch(Activity activity, Date orderTime, String deliveryAddress,
	                              Date deliveryTime, int requestCode, final int accentColor) {
		final Intent intent = new Intent(activity, LunchOrderAcceptedActivity.class);
		intent.putExtra(EXTRA_ORDER_TIME, orderTime.getTime());
		intent.putExtra(EXTRA_DELIVERY_ADDRESS, deliveryAddress);
		intent.putExtra(EXTRA_DELIVERY_TIME, deliveryTime.getTime());
		intent.putExtra(EXTRA_ACCENT_COLOR, accentColor);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void startTakeaway(Activity activity, Date orderTime, String takeawayAddress,
	                                 String takeawayAfter, int requestCode, final int accentColor) {
		final Intent intent = new Intent(activity, TakeawayOrderAcceptedActivity.class);
		intent.putExtra(EXTRA_ORDER_TIME, orderTime.getTime());
		intent.putExtra(EXTRA_TAKEAWAY_ADDRESS, takeawayAddress);
		intent.putExtra(EXTRA_TAKEAWAY_AFTER, takeawayAfter);
		intent.putExtra(EXTRA_ACCENT_COLOR, accentColor);
		activity.startActivityForResult(intent, requestCode);
	}

	@InjectView(R.id.panel_top)
	protected HeaderView topPanel;

	private int mAccentColor;

	@Override
	public int getLayoutResource() {
		return R.layout.activity_order_accepted;
	}

	@Override
	protected void handleIntent(final Intent intent) {
		mAccentColor = intent.getIntExtra(EXTRA_ACCENT_COLOR, 0);
	}

	@Override
	public void initUi() {
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

}
