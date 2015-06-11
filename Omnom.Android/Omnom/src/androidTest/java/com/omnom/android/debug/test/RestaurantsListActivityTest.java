package com.omnom.android.debug.test;

import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.omnom.android.R;
import com.omnom.android.activity.RestaurantActivity;
import com.omnom.android.activity.RestaurantsListActivity;
import com.omnom.android.adapter.RestaurantsAdapter;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
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

	public void testCheckViewsAppearance() throws Exception {
		final View imgProfile = getActivity().findViewById(R.id.img_profile);
		final View txtScanQr = getActivity().findViewById(R.id.scan_qr);
		final View btnDemo = getActivity().findViewById(R.id.btn_demo);

		Assert.assertTrue(mList != null);
		Assert.assertTrue(imgProfile != null);
		Assert.assertTrue(txtScanQr != null);
		Assert.assertTrue(btnDemo != null);

		Assert.assertTrue(ViewUtils.isVisible(mList));
		Assert.assertTrue(ViewUtils.isVisible(imgProfile));
		Assert.assertTrue(ViewUtils.isVisible(txtScanQr));
		Assert.assertTrue(ViewUtils.isVisible(btnDemo));
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

	public void testRestaurantDetailsOpening() {
		try {
			passThroughActivityCreateLifecycle();

			waitNetworkData();

			final boolean[] bar = new boolean[1];

			runTestOnUiThread(new Runnable() {
				@Override
				public void run() {
					final ListView listView = (ListView) solo.getView(android.R.id.list);
					final RestaurantsAdapter adapter = (RestaurantsAdapter) listView.getAdapter();

					final int position = 1;
					final Restaurant item = (Restaurant) adapter.getItem(position);
					bar[0] = item.isBar();
					listView.performItemClick(listView, position, listView.getItemIdAtPosition(position));
				}
			});

			sleepTransition();

			assertCurrentActivity(RestaurantActivity.class);

			back();

			sleepTransition();

			assertCurrentActivity(RestaurantsListActivity.class);
		} catch(Throwable throwable) {
			Log.e(TAG, "testRestaurantDetailsOpening", throwable);
		}
	}
}
