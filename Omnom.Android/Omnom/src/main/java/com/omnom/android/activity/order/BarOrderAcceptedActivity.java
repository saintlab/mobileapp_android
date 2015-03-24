package com.omnom.android.activity.order;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.omnom.android.R;
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

	private String mOrderNumber;

	private String mPinCode;

	@Override
	protected void handleIntent(final Intent intent) {
		super.handleIntent(intent);
		mOrderNumber = intent.getStringExtra(EXTRA_ORDER_NUMBER);
		mPinCode = intent.getStringExtra(EXTRA_PIN_CODE);
	}

	@Override
	public void initUi() {
		super.initUi();
		ViewUtils.setVisible(orderNumberContainer, true);
		ViewUtils.setVisible(pinCodeContainer, true);
		txtOrderNumber.setText(String.valueOf(mOrderNumber));
		txtPinCode.setText(String.valueOf(mPinCode));
		AndroidUtils.clickify(txtCheckOrder, getString(R.string.we_will_invite_you_mark),
		                      new ClickSpan.OnClickListener() {
			                      @Override
			                      public void onClick() {
				                      Toast.makeText(getActivity(), "Hey!", Toast.LENGTH_SHORT).show();
			                      }
		                      });
	}

}
