package com.omnom.android.debug.test;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.EditText;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.CardsActivity;
import com.omnom.android.activity.ConfirmPhoneActivity;
import com.omnom.android.activity.EnteringActivity;
import com.omnom.android.activity.LoginActivity;
import com.omnom.android.activity.OrdersActivity;
import com.omnom.android.activity.PaymentProcessActivity;
import com.omnom.android.activity.SplashActivity;
import com.omnom.android.activity.ThanksActivity;
import com.omnom.android.activity.ValidateActivityBle;
import com.omnom.android.utils.view.NumberPicker;
import com.robotium.solo.Condition;
import com.robotium.solo.Solo;
import com.robotium.solo.Timeout;

public class BaseTestCase1 extends ActivityInstrumentationTestCase2<SplashActivity> {
	private Solo solo;

	public BaseTestCase1() {
		super(SplashActivity.class);
	}

	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation());
		getActivity();
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void testRun() {
		//Wait for activity: 'com.omnom.android.activity.SplashActivity'
		solo.waitForActivity(com.omnom.android.activity.SplashActivity.class, 2000);

		final Activity currentActivity = solo.getCurrentActivity();
		final String authToken = OmnomApplication.get(currentActivity).getPreferences().getAuthToken(currentActivity);
		if(TextUtils.isEmpty(authToken)) {
			//Wait for activity: 'com.omnom.android.activity.EnteringActivity'
			assertTrue("com.omnom.android.activity.EnteringActivity is not found!", solo.waitForActivity(EnteringActivity.class));

			//Set default small timeout to 60000 seconds
			Timeout.setSmallTimeout(60000);

			//Click on ????
			solo.clickOnView(solo.getView(com.omnom.android.R.id.btn_enter));

			//Wait for activity: 'com.omnom.android.activity.LoginActivity'
			assertTrue("com.omnom.android.activity.LoginActivity is not found!", solo.waitForActivity(LoginActivity.class));

			//Enter the text: '+79133952320'
			final EditText editPhoneNumber = (EditText) solo.getView(R.id.edit);
			if(editPhoneNumber.getText().length() < 10) {
				solo.clearEditText(editPhoneNumber);
				solo.enterText((android.widget.EditText) solo.getView(R.id.edit), "+79133952320");
			}

			//Click on Proceed
			solo.clickOnView(solo.getView(R.id.btn_right));

			//Wait for activity: 'com.omnom.android.activity.ConfirmPhoneActivity'
			assertTrue("com.omnom.android.activity.ConfirmPhoneActivity is not found!", solo.waitForActivity(ConfirmPhoneActivity.class));

			//Enter the text: '1'
			solo.clearEditText((android.widget.EditText) solo.getView(R.id.digit_1));
			solo.enterText((android.widget.EditText) solo.getView(R.id.digit_1), "1");

			//Enter the text: '1'
			solo.clearEditText((android.widget.EditText) solo.getView(R.id.digit_2));
			solo.enterText((android.widget.EditText) solo.getView(R.id.digit_2), "1");

			//Enter the text: '1'
			solo.clearEditText((android.widget.EditText) solo.getView(R.id.digit_3));
			solo.enterText((android.widget.EditText) solo.getView(R.id.digit_3), "1");

			//Enter the text: '1'
			solo.clearEditText((android.widget.EditText) solo.getView(R.id.digit_4));
			solo.enterText((android.widget.EditText) solo.getView(R.id.digit_4), "1");

			//Wait for activity: 'com.omnom.android.activity.SplashActivity'
			assertTrue("com.omnom.android.activity.SplashActivity is not found!", solo.waitForActivity(SplashActivity.class));
		}

		//Wait for activity: 'com.omnom.android.activity.ValidateActivityBle'
		assertTrue("com.omnom.android.activity.ValidateActivityBle is not found!", solo.waitForActivity(ValidateActivityBle.class));

		//Click on Bill
		solo.clickOnView(solo.getView(R.id.btn_bill));
		//Wait for activity: 'com.omnom.android.activity.OrdersActivity'
		assertTrue("com.omnom.android.activity.OrdersActivity is not found!", solo.waitForActivity(OrdersActivity.class));

		final OrdersActivity ordersActivity = (OrdersActivity) solo.getCurrentActivity();
		ViewPager pager = (ViewPager) solo.getView(R.id.pager);
		if(pager.getAdapter().getCount() == 1) {
			// There is only one bill
		} else {
			// Select bill
			solo.clickOnView(solo.getView(R.id.txt_title, 0));
		}

		//Click on amount
		solo.clickOnView(solo.getView(R.id.edit_payment_amount));

		//Clear amount
		for(int i = 0; i < 10; i++) {
			solo.sendKey(Solo.DELETE);
		}

		// Set amount to 0.01
		solo.sendKey(7);
		solo.sendKey(KeyEvent.KEYCODE_PERIOD);
		solo.sendKey(7);
		solo.sendKey(8);

		//Click on Apply
		solo.clickOnView(solo.getView(R.id.btn_apply));

		//Click on Other tips
		solo.clickOnView(solo.getView(R.id.radio_tips_4));

		//Enter the text: '0'
		final NumberPicker view = (NumberPicker) solo.getView(R.id.tips_picker);

		solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				return view.getValue() == 0;
			}
		}, 2000);

		solo.getCurrentActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				view.setValue(0);
			}
		});

		//Click on Apply
		solo.clickOnView(solo.getView(R.id.btn_apply));

		//Click on Pay
		solo.clickOnView(solo.getView(R.id.btn_pay));

		//Wait for activity: 'com.omnom.android.activity.CardsActivity'
		assertTrue("com.omnom.android.activity.CardsActivity is not found!", solo.waitForActivity(CardsActivity.class));

		// select first card
		solo.clickInList(1, 0);

		//Click on Pay
		solo.clickOnView(solo.getView(R.id.btn_pay));

		//Wait for activity: 'com.omnom.android.activity.PaymentProcessActivity'
		assertTrue("com.omnom.android.activity.PaymentProcessActivity is not found!", solo.waitForActivity(PaymentProcessActivity.class));

		Timeout.setSmallTimeout(30000);

		assertTrue("com.omnom.android.activity.ThanksActivity is not found!", solo.waitForActivity(ThanksActivity.class));

		//Click on Done
		solo.clickOnView(solo.getView(R.id.btn_left));

		assertTrue("com.omnom.android.activity.ThanksActivity is not found!", solo.waitForActivity(ValidateActivityBle.class));

		////Click on ???????
		//solo.clickOnView(solo.getView(R.id.btn_left));
	}
}
