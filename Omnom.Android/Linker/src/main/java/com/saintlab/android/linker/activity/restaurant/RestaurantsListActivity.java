package com.saintlab.android.linker.activity.restaurant;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.utils.activity.BaseListActivity;
import com.saintlab.android.linker.BuildConfig;
import com.saintlab.android.linker.R;
import com.saintlab.android.linker.activity.UserProfileActivity;
import com.saintlab.android.linker.activity.bind.BindActivity;
import com.saintlab.android.linker.adapter.MockPlacesAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import butterknife.OnItemClick;

public class RestaurantsListActivity extends BaseListActivity {

	public static void start(final Context context, List<Restaurant> restaurants) {
		final Intent intent = new Intent(context, RestaurantsListActivity.class);
		intent.putParcelableArrayListExtra(EXTRA_RESTAURANTS, new ArrayList<Parcelable>(restaurants));
		context.startActivity(intent, ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, R.anim.fake_fade_out).toBundle());
	}

	@OnClick(R.id.btn_profile)
	public void onProfile() {
		if(BuildConfig.DEBUG) {
			start(new Intent(this, BeaconsChartActivity.class), false);
		} else {
			UserProfileActivity.start(this, true);
		}
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
	protected void showBindActivity(int position) {
		BindActivity.start(this, (Restaurant) getListAdapter().getItem(position), true);
	}
}
