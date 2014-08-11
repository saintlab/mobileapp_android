package com.omnom.android.linker.activity;

import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.model.Restaurant;
import com.omnom.android.linker.widget.loader.LoaderView;

import butterknife.InjectView;
import butterknife.OnClick;

public class RestaurantActivity extends BaseActivity {

	public static final String EXTRA_SELECTED_RESTAURANT = "com.omnom.android.linker.selected_restaurant";
	public static final String EXTRA_RESTAURANT          = "com.omnom.android.linker.restaurant";

	@InjectView(R.id.loader)
	LoaderView loader;

	@Override
	public void initUi() {
		Parcelable selectedPlace = getIntent().getParcelableExtra(EXTRA_SELECTED_RESTAURANT);
		Parcelable place = getIntent().getParcelableExtra(EXTRA_RESTAURANT);
		if(selectedPlace != null) {
			final Restaurant p = (Restaurant) selectedPlace;
			Toast.makeText(this, p.getTitle(), Toast.LENGTH_LONG).show();
		} else if(place != null) {
			final Restaurant p = (Restaurant) place;
			Toast.makeText(this, p.getTitle(), Toast.LENGTH_LONG).show();
		} else {
			finish();
		}
		loader.scaleDown(null);
	}

	@OnClick(R.id.btn_back)
	protected void onBackClicked(View view) {
		finish();
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_restaurant;
	}
}
