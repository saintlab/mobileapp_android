package com.omnom.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.ClickSpan;
import com.omnom.android.view.HeaderView;

import butterknife.InjectView;

public class OrderAcceptedActivity extends BaseOmnomActivity {

	public static void start(Activity activity, String orderNumber, String pinCode,
	                         int requestCode, final int accentColor) {
		final Intent intent = new Intent(activity, OrderAcceptedActivity.class);
		intent.putExtra(EXTRA_ORDER_NUMBER, orderNumber);
		intent.putExtra(EXTRA_PIN_CODE, pinCode);
		intent.putExtra(EXTRA_ACCENT_COLOR, accentColor);
		activity.startActivityForResult(intent, requestCode);
	}

	@InjectView(R.id.panel_top)
	protected HeaderView topPanel;

	@InjectView(R.id.txt_order_number)
	protected TextView txtOrderNumber;

	@InjectView(R.id.txt_pin_code)
	protected TextView txtPinCode;

	@InjectView(R.id.txt_check_order)
	protected TextView txtCheckOrder;

	private String mOrderNumber;

	private String mPinCode;

	private int mAccentColor;

	@Override
	public int getLayoutResource() {
		return R.layout.activity_order_accepted;
	}

	@Override
	protected void handleIntent(final Intent intent) {
		mOrderNumber = intent.getStringExtra(EXTRA_ORDER_NUMBER);
		mPinCode = intent.getStringExtra(EXTRA_PIN_CODE);
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

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

}
