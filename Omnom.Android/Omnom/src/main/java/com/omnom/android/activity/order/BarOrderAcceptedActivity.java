package com.omnom.android.activity.order;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.WebActivity;
import com.omnom.android.entrance.BarEntranceData;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.ClickSpan;
import com.omnom.android.utils.utils.ViewUtils;

import butterknife.InjectView;

public class BarOrderAcceptedActivity extends BaseOrderAcceptedActivity {

	@InjectView(R.id.order_number_continer)
	protected View orderNumberContainer;

	@InjectView(R.id.txt_order_number)
	protected TextView txtOrderNumber;

	@InjectView(R.id.pin_code_container)
	protected View pinCodeContainer;

	@InjectView(R.id.txt_pin_code)
	protected TextView txtPinCode;

	@InjectView(R.id.txt_check_order)
	protected TextView txtCheckOrder;

	@Override
	protected void handleIntent(final Intent intent) {
		super.handleIntent(intent);
		if(!(mEntranceData instanceof BarEntranceData)) {
			throw new IllegalArgumentException("Bar entrance data is expected");
		}
	}

	@Override
	public void initUi() {
		super.initUi();
		ViewUtils.setVisibleGone(orderNumberContainer, true);
		ViewUtils.setVisibleGone(pinCodeContainer, true);
		final BarEntranceData barEntranceData = (BarEntranceData) mEntranceData;
		txtOrderNumber.setText(String.valueOf(barEntranceData.orderNumber()));
		txtPinCode.setText(String.valueOf(barEntranceData.pinCode()));
		AndroidUtils.clickify(txtCheckOrder, getString(R.string.we_will_invite_you_mark),
		                      new ClickSpan.OnClickListener() {
			                      @Override
			                      public void onClick() {
				                      WebActivity.start(BarOrderAcceptedActivity.this, RestaurantHelper.getBarUri(mRestaurant));
			                      }
		                      });
	}

}
