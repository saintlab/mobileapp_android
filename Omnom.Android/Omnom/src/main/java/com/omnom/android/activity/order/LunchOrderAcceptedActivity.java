package com.omnom.android.activity.order;

import android.content.Intent;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.holder.DeliveryEntranceData;
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

	@Override
	public int getLayoutResource() {
		return R.layout.activity_order_accepted;
	}

	@Override
	protected void handleIntent(final Intent intent) {
		super.handleIntent(intent);
		if (!(entranceData instanceof DeliveryEntranceData)) {
			throw new IllegalArgumentException("Lunch entrance data is expected");
		}
	}

	@Override
	public void initUi() {
		super.initUi();
		ViewUtils.setVisible(txtOrderTime, true);
		final DeliveryEntranceData deliveryEntranceData = (DeliveryEntranceData) entranceData;
		final Date deliveryTime = deliveryEntranceData.deliveryTime();
		txtOrderTime.setText(ORDER_TIME_FORMAT.format(deliveryEntranceData.orderTime()));
		txtCheckOrder.setText(getString(R.string.order_will_be_delivered,
										deliveryEntranceData.deliveryAddress(),
										DateUtils.getOnDayPreposition(deliveryTime),
										DateUtils.getDayOfWeek(deliveryTime),
										DELIVERY_DATE_FORMAT.format(deliveryTime).toLowerCase(),
										DELIVERY_TIME_FORMAT.format(deliveryTime)));
	}

}
