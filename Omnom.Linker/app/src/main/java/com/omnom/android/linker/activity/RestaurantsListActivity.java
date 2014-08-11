package com.omnom.android.linker.activity;

import android.content.Intent;
import android.os.Parcelable;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseListActivity;
import com.omnom.android.linker.adapter.MockPlacesAdapter;
import com.omnom.android.linker.model.RestaurantsFactory;

import java.util.Arrays;

import butterknife.OnItemClick;

public class RestaurantsListActivity extends BaseListActivity {

	@Override
	public int getLayoutResource() {
		return R.layout.activity_list_restaurants;
	}

	@Override
	public void initUi() {
		setListAdapter(new MockPlacesAdapter(this, Arrays.asList(RestaurantsFactory.createFake("test1"), RestaurantsFactory.createFake("test2"),
		                                                         RestaurantsFactory.createFake("test3"), RestaurantsFactory.createFake("test4"),
		                                                         RestaurantsFactory.createFake("test5"))));
	}

	@OnItemClick(android.R.id.list)
	protected void showPlaceActivity(int position) {
		Intent intent = new Intent(this, RestaurantActivity.class);
		intent.putExtra(RestaurantActivity.EXTRA_SELECTED_RESTAURANT, (Parcelable) getListAdapter().getItem(position));
		startActivity(intent);
	}
}
