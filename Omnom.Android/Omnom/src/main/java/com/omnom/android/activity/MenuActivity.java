package com.omnom.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.adapter.MenuCategoriesAdapter;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.InjectView;
import butterknife.OnItemClick;

public class MenuActivity extends BaseOmnomFragmentActivity {

	@InjectView(android.R.id.list)
	protected ListView mListView;

	@Nullable
	private Menu mMenu;

	@Nullable
	private MenuCategoriesAdapter mAdapter;

	private Target mTarget;

	@Nullable
	private Restaurant mRestaurant;

	@OnItemClick(android.R.id.list)
	public void onListItemClick(final int position) {
		final Intent intent = new Intent(this, MenuSubcategoryActivity.class);
		intent.putExtra(EXTRA_RESTAURANT_MENU, mMenu);
		intent.putExtra(EXTRA_POSITION, position);
		start(intent, R.anim.slide_in_right, R.anim.nothing, false);
	}

	@Override
	public void initUi() {

		mAdapter = new MenuCategoriesAdapter(this, mMenu.getFilledCategories());
		mListView.setAdapter(mAdapter);

		mTarget = new Target() {
			@Override
			public void onBitmapLoaded(final Bitmap bitmap, final Picasso.LoadedFrom from) {
				final BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
				final View root = findViewById(R.id.root);
				root.setBackgroundDrawable(drawable);
			}

			@Override
			public void onBitmapFailed(Drawable errorDrawable) {
			}

			@Override
			public void onPrepareLoad(Drawable placeHolderDrawable) {
			}
		};

		OmnomApplication.getPicasso(this)
		                .load(RestaurantHelper.getBackground(mRestaurant, getResources().getDisplayMetrics()))
		                .noFade().into(mTarget);
	}

	@Override
	protected void handleIntent(final Intent intent) {
		mRestaurant = intent.getParcelableExtra(EXTRA_RESTAURANT);
		mMenu = intent.getParcelableExtra(EXTRA_RESTAURANT_MENU);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.nothing, R.anim.slide_out_right);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_menu;
	}
}
