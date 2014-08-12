package com.omnom.android.linker.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseListActivity;
import com.omnom.android.linker.adapter.MockPlacesAdapter;
import com.omnom.android.linker.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnItemClick;

public class RestaurantsListActivity extends BaseListActivity {

	public static final String EXTRA_RESTAURANTS = "com.omnom.android.linker.restaurants";

	public static void start(final Context context, List<Restaurant> restaurants) {
		final Intent intent = new Intent(context, RestaurantsListActivity.class);
		intent.putParcelableArrayListExtra(EXTRA_RESTAURANTS, new ArrayList<Parcelable>(restaurants));
		context.startActivity(intent, ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, R.anim.fake_fade_out).toBundle());
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_list_restaurants;
	}

	@Override
	public void initUi() {
		if(getIntent().hasExtra(EXTRA_RESTAURANTS)) {
			ArrayList<Restaurant> restaurants = getIntent().getParcelableArrayListExtra(EXTRA_RESTAURANTS);
			setListAdapter(new MockPlacesAdapter(this, restaurants));
		}
	}

	@OnItemClick(android.R.id.list)
	protected void showPlaceActivity(int position) {
		ValidationActivity.start(this, (Restaurant) getListAdapter().getItem(position));
	}
}
