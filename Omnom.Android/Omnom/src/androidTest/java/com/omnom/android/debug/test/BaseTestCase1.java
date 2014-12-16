package com.omnom.android.debug.test;

import android.test.ActivityInstrumentationTestCase2;

import com.omnom.android.R;
import com.omnom.android.activity.SplashActivity;
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
        //Wait for activity: 'com.omnom.android.activity.EnteringActivity'
		assertTrue("com.omnom.android.activity.EnteringActivity is not found!", solo.waitForActivity(com.omnom.android.activity.EnteringActivity.class));
        //Set default small timeout to 65072 milliseconds
		Timeout.setSmallTimeout(65072);
        //Click on ????
		solo.clickOnView(solo.getView(com.omnom.android.R.id.btn_enter));
        //Wait for activity: 'com.omnom.android.activity.LoginActivity'
		/*assertTrue("com.omnom.android.activity.LoginActivity is not found!", solo.waitForActivity(com.omnom.android.activity
				                                                                                           .LoginActivity.class));
        //Enter the text: '+79133952320'
		solo.clearEditText((android.widget.EditText) solo.getView(R.id.edit));
		solo.enterText((android.widget.EditText) solo.getView(R.id.edit), "+79133952320");
        //Click on Empty Text View
		solo.clickOnView(solo.getView(R.id.btn_clear));*/

        /*//Enter the text: '+79133952320'
		solo.clearEditText((android.widget.EditText) solo.getView(R.id.edit));
		solo.enterText((android.widget.EditText) solo.getView(R.id.edit), "+79133952320");*/

        //Click on ?????
		solo.clickOnView(solo.getView(R.id.btn_right));
        //Wait for activity: 'com.omnom.android.activity.ConfirmPhoneActivity'
		assertTrue("com.omnom.android.activity.ConfirmPhoneActivity is not found!", solo.waitForActivity(com.omnom.android.activity.ConfirmPhoneActivity.class));
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
		assertTrue("com.omnom.android.activity.SplashActivity is not found!", solo.waitForActivity(com.omnom.android.activity.SplashActivity.class));
        //Wait for activity: 'com.omnom.android.activity.ValidateActivityBle'
		assertTrue("com.omnom.android.activity.ValidateActivityBle is not found!", solo.waitForActivity(com.omnom.android.activity.ValidateActivityBle.class));
        //Click on ????
		solo.clickOnView(solo.getView(R.id.btn_bill));
        //Wait for activity: 'com.omnom.android.activity.OrdersActivity'
		assertTrue("com.omnom.android.activity.OrdersActivity is not found!", solo.waitForActivity(com.omnom.android.activity.OrdersActivity.class));

        //Click on 260 ????? ?????? ?????
		solo.clickOnView(solo.getView(R.id.txt_title, 3));

        //Click on amount
		solo.clickOnView(solo.getView(R.id.edit_payment_amount));

        //Enter the text: '0.01 '
		solo.clearEditText((android.widget.EditText) solo.getView(R.id.edit_payment_amount));
		solo.enterText((android.widget.EditText) solo.getView(R.id.edit_payment_amount), "0.01?");

        //Click on Apply
		solo.clickOnView(solo.getView(R.id.btn_apply));

        /*//Enter the text: '0.01 '
		solo.clearEditText((android.widget.EditText) solo.getView(R.id.edit_payment_amount));
		solo.enterText((android.widget.EditText) solo.getView(R.id.edit_payment_amount), "0.01?");*/

        //Click on Other tips
		solo.clickOnView(solo.getView(R.id.radio_tips_4));
        //Enter the text: '0'
		solo.clearEditText((android.widget.EditText) solo.getView(R.id.np__numberpicker_input));
		solo.enterText((android.widget.EditText) solo.getView(R.id.np__numberpicker_input), "0");

        //Click on Apply
		solo.clickOnView(solo.getView(R.id.btn_apply));

        //Click on Pay
		solo.clickOnView(solo.getView(R.id.btn_pay));

        //Wait for activity: 'com.omnom.android.activity.CardsActivity'
		assertTrue("com.omnom.android.activity.CardsActivity is not found!", solo.waitForActivity(com.omnom.android.activity.CardsActivity.class));
        //Click on 4245 .... .... 5928 visa
		solo.clickInList(1, 0);
        //Click on ???????? 0.01
		solo.clickOnView(solo.getView(R.id.btn_pay));
        //Wait for activity: 'com.omnom.android.activity.PaymentProcessActivity'
		assertTrue("com.omnom.android.activity.PaymentProcessActivity is not found!", solo.waitForActivity(com.omnom.android.activity.PaymentProcessActivity.class));
        //Click on ??
		solo.clickOnView(solo.getView(R.id.btn_bottom));
        //Click on ??????
		solo.clickOnView(solo.getView(R.id.btn_left));
        //Click on ???????
		solo.clickOnView(solo.getView(R.id.btn_left));
	}
}
