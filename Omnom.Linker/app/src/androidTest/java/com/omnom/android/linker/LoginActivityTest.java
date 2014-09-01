package com.omnom.android.linker;

import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.apps.common.testing.ui.espresso.Espresso;
import com.omnom.android.linker.activity.LoginActivity;

import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Ch3D on 01.09.2014.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

	private LoginActivity mActivity;

	public LoginActivityTest() {
		super(LoginActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = getActivity();
	}

	public void testLoginButton() {
		SystemClock.sleep(4000);
		Espresso.onView(withId(R.id.btn_login)).perform(click());
	}

	public void testLoginFail() {
		SystemClock.sleep(4000);
		Espresso.onView(withId(R.id.btn_bind_table)).perform(click());
	}
}
