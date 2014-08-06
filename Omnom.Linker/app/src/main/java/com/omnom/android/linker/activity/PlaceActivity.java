package com.omnom.android.linker.activity;

import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.model.Place;
import com.omnom.android.linker.widget.LoaderView;

import butterknife.InjectView;
import butterknife.OnClick;

public class PlaceActivity extends BaseActivity {

	public static final String EXTRA_SELECTED_PLACE = "com.omnom.android.linker.selected_place";
	public static final String EXTRA_PLACE = "com.omnom.android.linker.place";

	@InjectView(R.id.loader)
	LoaderView loader;

	@Override
	public void initUi() {
		Parcelable selectedPlace = getIntent().getParcelableExtra(EXTRA_SELECTED_PLACE);
		Parcelable place = getIntent().getParcelableExtra(EXTRA_PLACE);
		if(selectedPlace != null) {
			final Place p = (Place) selectedPlace;
			Toast.makeText(this, p.getName(), Toast.LENGTH_LONG).show();
		} else if(place != null) {
			final Place p = (Place) place;
			Toast.makeText(this, p.getName(), Toast.LENGTH_LONG).show();
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
		return R.layout.activity_place;
	}
}
