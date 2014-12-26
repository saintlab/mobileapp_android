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
import android.widget.ProgressBar;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.adapter.RestaurantsAdapter;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantsResponse;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.utils.view.SimpleListView;

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

	public static void start(BaseOmnomActivity activity, boolean finish) {
		activity.start(RestaurantsListActivity.class, finish);
	}

	public static void start(BaseOmnomActivity activity, List<Restaurant> restaurants) {
		final Intent intent = new Intent(activity, RestaurantsListActivity.class);
		intent.putParcelableArrayListExtra(EXTRA_RESTAURANTS, new ArrayList<Parcelable>(restaurants));
		activity.start(intent);
		activity.start(RestaurantsListActivity.class, false);
	}

	@InjectView(R.id.panel_top)
	protected View panelTop;

	@InjectView(android.R.id.list)
	protected SimpleListView list;

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

	private ImageView selectedImgCover;

	private View nextView;

	private boolean mItemClicked = false;

	private View footer;

	@OnClick(R.id.img_qr)
	public void doQrShortcut() {
		showToast(this, "NOT IMPLEMENTED YET!");
	}

	@OnClick(R.id.img_profile)
	public void doUserProfile() {
		UserProfileActivity.startSliding(this, -1, StringUtils.EMPTY_STRING);
	}

	@OnClick(R.id.btn_demo)
	public void doDemo() {
		ValidateActivity.startDemo(this,
		                           R.anim.fake_fade_in_instant,
		                           R.anim.fake_fade_out_instant,
		                           EXTRA_LOADER_ANIMATION_SCALE_DOWN);
	}

	@Override
	protected void handleIntent(final Intent intent) {
		if(getIntent().hasExtra(EXTRA_RESTAURANTS)) {
			mRestaurants = getIntent().getParcelableArrayListExtra(EXTRA_RESTAURANTS);
			mAdapter = new RestaurantsAdapter(this, mRestaurants);
		}
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
		if(footer != null) {
			list.removeFooterView(footer);
		}
		footer = LayoutInflater.from(getActivity()).inflate(R.layout.item_restaurants_footer, null);
		footer.findViewById(R.id.txt_info).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AndroidUtils.sendFeedbackEmail(getActivity(), R.string.email_subject_feedback);
			}
		});
		list.addFooterView(footer);
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(this);
		list.setScrollEnabled(true);
	}

	@Override
	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		if(mItemClicked) {
			// skip item selection
			return;
		}

		mItemClicked = true;
		mAdapter.setSelected(position);
		list.smoothScrollToPositionFromTop(position, 0);
		selectedImgCover = (ImageView) view.findViewById(R.id.img_cover);
		panelTop.animate().translationYBy(-panelTop.getHeight()).start();
		list.animate().translationYBy(-panelTop.getHeight()).start();
		nextView = list.getChildAt(position + 1);
		list.setScrollEnabled(false);
		if(nextView != null) {
			nextView.animate().alpha(0).start();
		}
		AnimationUtils.scaleHeight(selectedImgCover, getResources().getDimensionPixelSize(R.dimen.restaurant_cover_height_large),
		                           new Runnable() {
			                           @Override
			                           public void run() {
				                           list.requestLayout();
				                           final Restaurant item = (Restaurant) mAdapter.getItem(position);
				                           RestaurantActivity.start(RestaurantsListActivity.this, item);
				                           mItemClicked = false;
			                           }
		                           });
	}

	@Override
	protected void onStop() {
		super.onStop();
		mItemClicked = false;
		list.setScrollEnabled(true);
		panelTop.setTranslationY(0);
		list.setTranslationY(0);
		mAdapter.setSelected(-1);
		mAdapter.notifyDataSetChanged();
		if(nextView != null) {
			nextView.setAlpha(1);
		}
		ViewUtils.setHeight(selectedImgCover, getResources().getDimensionPixelSize(R.dimen.restaurant_cover_height_small));
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
