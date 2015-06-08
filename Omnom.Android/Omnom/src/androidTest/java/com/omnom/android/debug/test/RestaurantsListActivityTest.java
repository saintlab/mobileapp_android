package com.omnom.android.debug.test;

import android.util.Log;
import android.widget.ListView;

import com.omnom.android.R;
import com.omnom.android.activity.RestaurantActivity;
import com.omnom.android.activity.RestaurantsListActivity;
import com.omnom.android.utils.utils.ViewUtils;

import junit.framework.Assert;

/**
 * Created by Ch3D on 08.06.2015.
 */
public class RestaurantsListActivityTest extends BaseOmnomActivityTestCase<RestaurantsListActivity> {

	private static final String TAG = RestaurantsListActivityTest.class.getSimpleName();

	private ListView mList;

	public RestaurantsListActivityTest() {
		super(RestaurantsListActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		mList = (ListView) getActivity().findViewById(android.R.id.list);
	}

	public void testListView() throws Exception {
		Assert.assertTrue(ViewUtils.isVisible(mList));
		Assert.assertTrue(ViewUtils.isVisible(getActivity().findViewById(R.id.img_profile)));
		Assert.assertTrue(ViewUtils.isVisible(getActivity().findViewById(R.id.scan_qr)));
	}

	public void testLoadRestaurantsListData() {
		try {
			passThroughActivityCreateLifecycle();
			waitNetworkData();

			runTestOnUiThread(new Runnable() {
				@Override
				public void run() {
					Assert.assertTrue(mList.getAdapter().getCount() > 0);
				}
			});
		} catch(Throwable throwable) {
			Log.e(TAG, "testLoadRestaurantsListData", throwable);
		}
	}

	public void testRestaurantDetails() {
		try {
			passThroughActivityCreateLifecycle();
			
			waitNetworkData();

			runTestOnUiThread(new Runnable() {
				@Override
				public void run() {
					final ListView listView = (ListView) solo.getView(android.R.id.list);
					listView.performItemClick(listView, 1, listView.getItemIdAtPosition(1));
				}
			});

			sleepTransition();

			assertCurrentActivity(RestaurantActivity.class);

			back();

			sleepTransition();

			assertCurrentActivity(RestaurantsListActivity.class);
		} catch(Throwable throwable) {
			Log.e(TAG, "testRestaurantDetails", throwable);
		}
	}
}
