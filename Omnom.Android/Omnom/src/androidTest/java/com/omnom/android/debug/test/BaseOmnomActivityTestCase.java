package com.omnom.android.debug.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

/**
 * Created by Ch3D on 08.06.2015.
 */
public abstract class BaseOmnomActivityTestCase<T extends Activity> extends ActivityInstrumentationTestCase2<T> {

	public static final int DEFAULT_NETWORK_TIMEOUT_FAST = 5000;

	public static final int DEFAULT_NETWORK_TIMEOUT = 10000;

	protected Solo solo;

	public BaseOmnomActivityTestCase(final Class<T> activityClass) {
		super(activityClass);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		solo.finishOpenedActivities();
	}

	protected void passThroughActivityCreateLifecycle() throws Throwable {
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				getInstrumentation().callActivityOnCreate(getActivity(), null);
				getInstrumentation().callActivityOnStart(getActivity());
				getInstrumentation().callActivityOnResume(getActivity());
			}
		});
	}

	protected final void waitNetworkData() {
		solo.sleep(DEFAULT_NETWORK_TIMEOUT_FAST);
	}

	protected void sleepTransition() {
		solo.sleep(5000);
	}

	protected void assertCurrentActivity(final Class<?> clazz) {
		solo.assertCurrentActivity("current activity must be " + clazz.getSimpleName(), clazz);
	}

	protected void back() {solo.goBack();}
}
