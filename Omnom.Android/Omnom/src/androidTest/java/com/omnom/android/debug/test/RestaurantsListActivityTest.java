package com.omnom.android.debug.test;

import android.util.Log;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.omnom.android.R;
import com.omnom.android.activity.RestaurantActivity;
import com.omnom.android.activity.RestaurantsListActivity;
import com.omnom.android.activity.ValidateActivityBle18;
import com.omnom.android.activity.ValidateActivityBle21;
import com.omnom.android.adapter.RestaurantsAdapter;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.BluetoothUtils;
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
					final ListView listView = (ListView) solo.getView(android.R.id.list);
					Assert.assertTrue(getRestaurantsAdapter(listView).getCount() > 0);
				}
			});
		} catch(Throwable throwable) {
			Assert.fail(throwable.getMessage());
			Log.e(TAG, "testLoadRestaurantsListData", throwable);
		}
	}

	public void testRestaurantBarLanding() {
		try {
			passThroughActivityCreateLifecycle();

			waitNetworkData();

			runTestOnUiThread(new Runnable() {
				@Override
				public void run() {
					final ListView listView = (ListView) solo.getView(android.R.id.list);
					final RestaurantsAdapter adapter = getRestaurantsAdapter(listView);

					Restaurant restaurant = null;
					int position = -1;
					for(int i = 0; i < adapter.getCount(); i++) {
						final Restaurant item = (Restaurant) adapter.getItem(i);
						if(item.isBar()) {
							restaurant = item;
							position = i;
							break;
						}
					}

					if(restaurant == null) {
						// skip - there is no suitable restaurant
						return;
					}
					listView.performItemClick(listView, position, listView.getItemIdAtPosition(position));
				}
			});

			sleepTransition();

			if(BluetoothUtils.hasBleSupport(getActivity()) && BluetoothUtils.isBluetoothEnabled(getActivity())) {
				if(AndroidUtils.isLollipop()) {
					assertCurrentActivity(ValidateActivityBle21.class);
				} else {
					assertCurrentActivity(ValidateActivityBle18.class);
				}
			}
		} catch(Throwable throwable) {
			Assert.fail(throwable.getMessage());
			Log.e(TAG, "testRestaurantDetailsOpening", throwable);
		}
	}

	private RestaurantsAdapter getRestaurantsAdapter(final ListView listView) {
		final HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) listView.getAdapter();
		final SwingBottomInAnimationAdapter wrappedAdapter = (SwingBottomInAnimationAdapter) headerViewListAdapter.getWrappedAdapter();
		return (RestaurantsAdapter) wrappedAdapter.getDecoratedBaseAdapter();
	}

	public void testRestaurantDetailsOpening() {
		try {
			passThroughActivityCreateLifecycle();

			waitNetworkData();

			runTestOnUiThread(new Runnable() {
				@Override
				public void run() {
					final ListView listView = (ListView) solo.getView(android.R.id.list);
					final RestaurantsAdapter adapter = getRestaurantsAdapter(listView);

					Restaurant restaurant = null;
					int position = -1;
					for(int i = 0; i < adapter.getCount(); i++) {
						final Restaurant item = (Restaurant) adapter.getItem(i);
						if(!item.isBar() && !item.isLunch() && !item.isTakeAway()) {
							restaurant = item;
							position = i;
							break;
						}
					}

					if(restaurant == null) {
						// skip - there is no suitable restaurant
						return;
					}
					listView.performItemClick(listView, position, listView.getItemIdAtPosition(position));
				}
			});

			sleepTransition();

			assertCurrentActivity(RestaurantActivity.class);

			back();

			sleepTransition();

			assertCurrentActivity(RestaurantsListActivity.class);
		} catch(Throwable throwable) {
			Assert.fail(throwable.getMessage());
			Log.e(TAG, "testRestaurantDetailsOpening", throwable);
		}
	}
}
