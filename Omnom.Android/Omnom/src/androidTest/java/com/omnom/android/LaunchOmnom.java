package com.omnom.android;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * Created by Ch3D on 15.12.2014.
 */
public class LaunchOmnom extends UiAutomatorTestCase {

	public void testLaunch() throws UiObjectNotFoundException {
		getUiDevice().pressHome();

		UiObject allAppsButton = new UiObject(new UiSelector().description("Apps"));

		allAppsButton.clickAndWaitForNewWindow();

		UiObject appsTab = new UiObject(new UiSelector()
				                                .text("Apps"));

		appsTab.click();

		UiScrollable appViews = new UiScrollable(new UiSelector()
				                                         .scrollable(true));

		appViews.setAsHorizontalList();

		UiObject settingsApp = appViews.getChildByText(new UiSelector().className(
				android.widget.TextView.class.getName()), "Omnom");
		settingsApp.clickAndWaitForNewWindow();
	}
}
