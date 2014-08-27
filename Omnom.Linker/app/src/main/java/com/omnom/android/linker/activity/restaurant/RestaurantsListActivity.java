package com.omnom.android.linker.activity.restaurant;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.UserProfileActivity;
import com.omnom.android.linker.activity.base.BaseListActivity;
import com.omnom.android.linker.activity.bind.BindActivity;
import com.omnom.android.linker.adapter.MockPlacesAdapter;
import com.omnom.android.linker.model.Restaurant;

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
		startActivity(new Intent(this, UserProfileActivity.class));
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
