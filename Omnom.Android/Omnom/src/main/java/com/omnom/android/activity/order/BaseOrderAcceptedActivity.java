package com.omnom.android.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomModeSupportActivity;
import com.omnom.android.activity.holder.BarEntranceData;
import com.omnom.android.activity.holder.EntranceData;
import com.omnom.android.activity.holder.DeliveryEntranceData;
import com.omnom.android.activity.holder.TakeawayEntranceData;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.view.HeaderView;

import java.text.SimpleDateFormat;

import butterknife.InjectView;

public abstract class BaseOrderAcceptedActivity extends BaseOmnomModeSupportActivity {

	protected final SimpleDateFormat ORDER_TIME_FORMAT = new SimpleDateFormat("dd MMMM в HH:mm", AndroidUtils.russianLocale);

	public static void start(Activity activity, EntranceData entranceData, int requestCode,
	                         final int accentColor) {
		final Intent intent = new Intent(activity, getOrderAcceptedActivity(entranceData));
		intent.putExtra(EXTRA_ENTRANCE_DATA, entranceData);
		intent.putExtra(EXTRA_ACCENT_COLOR, accentColor);
		activity.startActivityForResult(intent, requestCode);
	}

	private static Class getOrderAcceptedActivity(final EntranceData entranceData) {
		if (entranceData == null) {
			throw new IllegalArgumentException("entranceData should not be null");
		}
		if (entranceData instanceof BarEntranceData) {
			return BarOrderAcceptedActivity.class;
		} else if (entranceData instanceof DeliveryEntranceData) {
			return LunchOrderAcceptedActivity.class;
		} else if (entranceData instanceof TakeawayEntranceData) {
			return TakeawayOrderAcceptedActivity.class;
		} else {
			throw new IllegalArgumentException("Unknown entrance data type");
		}
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
		super.handleIntent(intent);
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
