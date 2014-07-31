package com.omnom.android.linker.activity;

import android.content.Intent;
import android.os.Parcelable;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseListActivity;
import com.omnom.android.linker.adapter.MockPlacesAdapter;
import com.omnom.android.linker.model.Place;
import com.omnom.android.linker.model.PlaceFactory;

import java.util.ArrayList;

import butterknife.OnItemClick;

public class PlacesListActivity extends BaseListActivity {
	@Override
	public int getLayoutResource() {
		return R.layout.activity_places_list;
	}

	@Override
	public void initUi() {
		ArrayList<Place> places = new ArrayList<Place>();
		places.add(PlaceFactory.createFake("test1"));
		places.add(PlaceFactory.createFake("test2"));
		places.add(PlaceFactory.createFake("test3"));
		places.add(PlaceFactory.createFake("test4"));
		places.add(PlaceFactory.createFake("test5"));

		setListAdapter(new MockPlacesAdapter(this, places));
	}

	@OnItemClick(android.R.id.list)
	public void showPlaceActivity(int position) {
		Intent intent = new Intent(this, PlaceActivity.class);
		intent.putExtra(PlaceActivity.EXTRA_SELECTED_PLACE, (Parcelable)getListAdapter().getItem(position));
		startActivity(intent);
	}
}
