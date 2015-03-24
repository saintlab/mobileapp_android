package com.omnom.android.activity.order;

import android.content.Intent;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.utils.utils.ViewUtils;

import java.util.Date;

import butterknife.InjectView;

public class TakeawayOrderAcceptedActivity extends BaseOrderAcceptedActivity {

	@InjectView(R.id.txt_order_time)
	protected TextView txtOrderTime;

	@InjectView(R.id.txt_check_order)
	protected TextView txtCheckOrder;

	protected Date orderTime;

	protected String takeawayAddress;

	protected String takeawayAfter;

	@Override
	public int getLayoutResource() {
		return R.layout.activity_order_accepted;
	}

	@Override
	protected void handleIntent(final Intent intent) {
		super.handleIntent(intent);
		orderTime = new Date(intent.getLongExtra(EXTRA_ORDER_TIME, 0));
		takeawayAddress = intent.getStringExtra(EXTRA_TAKEAWAY_ADDRESS);
		takeawayAfter = intent.getStringExtra(EXTRA_TAKEAWAY_AFTER);
	}

	@Override
	public void initUi() {
		super.initUi();
		ViewUtils.setVisible(txtOrderTime, true);
		txtOrderTime.setText(ORDER_TIME_FORMAT.format(orderTime));
		txtCheckOrder.setText(getString(R.string.order_will_be_waiting_for_you, takeawayAfter, takeawayAddress));
	}

}
