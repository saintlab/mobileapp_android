package com.omnom.android;

import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;

import com.omnom.android.activity.SplashActivity;

/**
 * Created by Ch3D on 01.09.2014.
 */
public class BaseScenarioTest extends ActivityInstrumentationTestCase2<SplashActivity> {

	public static final String TOKEN = "WegFejtytmedmeursiphGhighyelheil";

	private SplashActivity mActivity;

	public BaseScenarioTest() {
		super(SplashActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = getActivity();
		mActivity.getPreferences().setAuthToken(mActivity, TOKEN);
	}

	public void testLoginButton() {
		SystemClock.sleep(4000);
		// Espresso.onView(withId(R.id.btn_login)).perform(click());
	}

	/*public void testLoginFail() {
		SystemClock.sleep(4000);
		Espresso.onView(withId(R.id.btn_bind_table)).perform(click());
	}*/
}
