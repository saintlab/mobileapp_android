package com.saintlab.android.linker.activity.restaurant;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.View;

import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.utils.activity.BaseActivity;
import com.omnom.android.utils.loader.LoaderView;
import com.omnom.android.utils.utils.ViewUtils;
import com.saintlab.android.linker.R;

import butterknife.InjectView;
import butterknife.OnClick;

public class RestaurantActivity extends BaseActivity {

	public static void start(final Context context, Restaurant restaurant) {
		final Intent intent = new Intent(context, RestaurantActivity.class);
		intent.putExtra(EXTRA_RESTAURANT, restaurant);
		context.startActivity(intent, ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, R.anim.fake_fade_out).toBundle());
	}

	@InjectView(R.id.loader)
	LoaderView loader;

	@InjectView(R.id.btn_back)
	View btnBack;

	@Override
	public void initUi() {
		Parcelable selectedPlace = getIntent().getParcelableExtra(EXTRA_SELECTED_RESTAURANT);
		Parcelable place = getIntent().getParcelableExtra(EXTRA_RESTAURANT);
		if(selectedPlace != null) {
			ViewUtils.setVisibleGone(btnBack, true);
		} else if(place != null) {
			ViewUtils.setVisibleGone(btnBack, false);
		} else {
			finish();
		}
		loader.post(new Runnable() {
			@Override
			public void run() {
				loader.scaleDown(null, null);
			}
		});
	}

	@OnClick(R.id.btn_back)
	protected void onBackClicked(View view) {
		finish();
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_bind;
	}
}
