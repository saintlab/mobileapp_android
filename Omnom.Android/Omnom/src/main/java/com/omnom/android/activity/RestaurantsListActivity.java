package com.omnom.android.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.adapter.RestaurantsAdapter;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantsResponse;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.functions.Action1;

import static com.omnom.android.utils.utils.AndroidUtils.showToast;

public class RestaurantsListActivity extends BaseOmnomActivity {

	private static final String TAG = RestaurantsListActivity.class.getSimpleName();

	public static void start(Context context) {
		final Intent intent = new Intent(context, RestaurantsListActivity.class);
		context.startActivity(intent);
	}

	@InjectView(android.R.id.list)
	protected ListView list;

	@InjectView(R.id.panel_top)
	protected View panelTop;

	@InjectView(R.id.btn_demo)
	protected Button btnDemo;

	@InjectView(R.id.img_qr)
	protected ImageView imgQr;

	@InjectView(R.id.img_profile)
	protected ImageView imgProfile;

	@Inject
	RestaurateurObeservableApi api;

	private RestaurantsAdapter mAdapter;

	@OnClick(R.id.img_qr)
	public void doQrShortcut() {
		showToast(this, "NOT IMPLEMENTED YET!");
	}

	@OnClick(R.id.img_profile)
	public void doUserProfile() {
		UserProfileActivity.startSliding(this, -1);
	}

	@OnClick(R.id.btn_demo)
	public void doDemo() {
		ValidateActivity.start(this,
		                       R.anim.fake_fade_in_instant,
		                       R.anim.fake_fade_out_instant,
		                       EXTRA_LOADER_ANIMATION_SCALE_DOWN, true);
	}

	@Override
	public void initUi() {
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				final Restaurant item = (Restaurant) mAdapter.getItem(position);
				final ImageView imgCover = (ImageView) view.findViewById(R.id.img_cover);
				//imgCover.animate().translationYBy(-100).start();
				//AnimationUtils.scaleHeight(imgCover, 450);
				final BitmapDrawable drawable = (BitmapDrawable) imgCover.getDrawable();
				final ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat
						.makeThumbnailScaleUpAnimation(view, drawable.getBitmap(), (int)view.getX(), (int)view.getY());
				startActivity(new Intent(getActivity(), UserProfileActivity.class), activityOptionsCompat.toBundle());
			}
		});

		api.getRestaurants().subscribe(new Action1<RestaurantsResponse>() {
			@Override
			public void call(final RestaurantsResponse restaurants) {
				mAdapter = new RestaurantsAdapter(getActivity(), restaurants.getItems());
				list.setAdapter(mAdapter);
				list.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.item_restaurants_footer, null));
			}
		}, new OmnomBaseErrorHandler(this) {
			@Override
			protected void onThrowable(final Throwable throwable) {
				Log.e(TAG, "getRestaurants", throwable);
			}
		});
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_restaurants_list;
	}
}
