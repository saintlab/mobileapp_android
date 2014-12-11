package com.omnom.android.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.adapter.RestaurantsAdapter;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantsResponse;
import com.omnom.android.utils.utils.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.functions.Action1;

import static com.omnom.android.utils.utils.AndroidUtils.showToast;

public class RestaurantsListActivity extends BaseOmnomActivity implements AdapterView.OnItemClickListener {

	private static final String TAG = RestaurantsListActivity.class.getSimpleName();

	public static void start(BaseOmnomActivity activity) {
		activity.start(RestaurantsListActivity.class, false);
	}

	public static void start(BaseOmnomActivity activity, List<Restaurant> restaurants) {
		final Intent intent = new Intent(activity, RestaurantsListActivity.class);
		intent.putParcelableArrayListExtra(EXTRA_RESTAURANTS, new ArrayList<Parcelable>(restaurants));
		activity.start(intent);
		activity.start(RestaurantsListActivity.class, false);
	}

	@InjectView(android.R.id.list)
	protected ListView list;

	@InjectView(R.id.btn_demo)
	protected Button btnDemo;

	@InjectView(R.id.img_profile)
	protected ImageView imgProfile;

	@InjectView(R.id.progress)
	protected ProgressBar progressBar;

	@Inject
	RestaurateurObeservableApi api;

	private RestaurantsAdapter mAdapter;

	@Nullable
	private ArrayList<Restaurant> mRestaurants;

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
	protected void handleIntent(final Intent intent) {
		mRestaurants = getIntent().getParcelableArrayListExtra(EXTRA_RESTAURANTS);
		mAdapter = new RestaurantsAdapter(this, mRestaurants);
	}

	@Override
	public void initUi() {
		if(mAdapter == null) {
			AnimationUtils.animateAlpha(progressBar, true);
			api.getRestaurants().subscribe(new Action1<RestaurantsResponse>() {
				@Override
				public void call(final RestaurantsResponse restaurants) {
					mAdapter = new RestaurantsAdapter(getActivity(), restaurants.getItems());
					AnimationUtils.animateAlpha(progressBar, false, new Runnable() {
						@Override
						public void run() {
							initList();
						}
					});
				}
			}, new OmnomBaseErrorHandler(this) {
				@Override
				protected void onThrowable(final Throwable throwable) {
					AnimationUtils.animateAlpha(progressBar, false);
					Log.e(TAG, "getRestaurants", throwable);
				}
			});
		} else {
			initList();
		}
	}

	private void initList() {
		list.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.item_restaurants_footer, null));
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		final Restaurant item = (Restaurant) mAdapter.getItem(position);
		RestaurantActivity.start(RestaurantsListActivity.this, item);
		//final ImageView imgCover = (ImageView) view.findViewById(R.id.img_cover);
		////imgCover.animate().translationYBy(-100).start();
		////AnimationUtils.scaleHeight(imgCover, 450);
		//final BitmapDrawable drawable = (BitmapDrawable) imgCover.getDrawable();
		//final ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat
		//		.makeThumbnailScaleUpAnimation(view, drawable.getBitmap(), (int)view.getX(), (int)view.getY());
		//startActivity(new Intent(getActivity(), UserProfileActivity.class), activityOptionsCompat.toBundle());
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_restaurants_list;
	}
}
