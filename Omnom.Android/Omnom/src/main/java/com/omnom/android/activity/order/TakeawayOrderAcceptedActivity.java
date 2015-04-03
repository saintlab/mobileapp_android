package com.omnom.android.activity.order;

import android.content.Intent;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.holder.TakeawayEntranceData;
import com.omnom.android.utils.utils.ViewUtils;

import butterknife.InjectView;

public class TakeawayOrderAcceptedActivity extends BaseOrderAcceptedActivity {

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
		if (!(entranceData instanceof TakeawayEntranceData)) {
			throw new IllegalArgumentException("Takeaway entrance data is expected");
		}
	}

	@Override
	public void initUi() {
		super.initUi();
		ViewUtils.setVisible(txtOrderTime, true);
		final TakeawayEntranceData takeawayEntranceData = (TakeawayEntranceData) entranceData;
		txtOrderTime.setText(ORDER_TIME_FORMAT.format(takeawayEntranceData.orderTime()));
		txtCheckOrder.setText(getString(R.string.order_will_be_waiting_for_you,
										takeawayEntranceData.takeawayAfter(),
										takeawayEntranceData.takeawayAddress()));
	}

}
