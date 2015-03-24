package com.omnom.android.activity.order;

import android.content.Intent;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.DateUtils;
import com.omnom.android.utils.utils.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.InjectView;

public class LunchOrderAcceptedActivity extends BaseOrderAcceptedActivity {

	private final SimpleDateFormat DELIVERY_DATE_FORMAT = new SimpleDateFormat("dd MMMM", AndroidUtils.russianLocale);
	private final SimpleDateFormat DELIVERY_TIME_FORMAT = new SimpleDateFormat("HH:mm", AndroidUtils.russianLocale);

	@InjectView(R.id.txt_order_time)
	protected TextView txtOrderTime;

	@InjectView(R.id.txt_check_order)
	protected TextView txtCheckOrder;

	protected Date orderTime;

	protected String deliveryAddress;

	protected Date deliveryTime;

	@Override
	public int getLayoutResource() {
		return R.layout.activity_order_accepted;
	}

	@Override
	protected void handleIntent(final Intent intent) {
		super.handleIntent(intent);
		orderTime = new Date(intent.getLongExtra(EXTRA_ORDER_TIME, 0));
		deliveryAddress = intent.getStringExtra(EXTRA_DELIVERY_ADDRESS);
		deliveryTime = new Date(intent.getLongExtra(EXTRA_DELIVERY_TIME, 0));
	}

	@Override
	public void initUi() {
		super.initUi();
		ViewUtils.setVisible(txtOrderTime, true);
		txtOrderTime.setText(ORDER_TIME_FORMAT.format(orderTime));
		txtCheckOrder.setText(getString(R.string.order_will_be_delivered,
										deliveryAddress,
										DateUtils.getOnDayPreposition(deliveryTime),
										DateUtils.getDayOfWeek(deliveryTime),
										DELIVERY_DATE_FORMAT.format(deliveryTime).toLowerCase(),
										DELIVERY_TIME_FORMAT.format(deliveryTime)));
	}

}
