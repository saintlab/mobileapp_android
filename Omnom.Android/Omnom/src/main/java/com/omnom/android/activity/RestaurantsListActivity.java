package com.omnom.android.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
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

public class RestaurantsListActivity extends BaseOmnomActivity implements AdapterView.OnItemClickListener {

	private static final String TAG = RestaurantsListActivity.class.getSimpleName();

	public static void start(BaseOmnomActivity activity) {
		start(activity, false);
	}

	public static void start(BaseOmnomActivity activity, boolean finish) {
		activity.start(new Intent(activity, RestaurantsListActivity.class), R.anim.slide_in_up, R.anim.fake_fade_out_long, finish);
	}

	public static void start(BaseOmnomActivity activity, List<Restaurant> restaurants) {
		final Intent intent = new Intent(activity, RestaurantsListActivity.class);
		intent.putParcelableArrayListExtra(EXTRA_RESTAURANTS, new ArrayList<Parcelable>(restaurants));
		activity.start(intent, R.anim.slide_in_up, R.anim.fake_fade_out_long, true);
	}

	@InjectView(R.id.panel_top)
	protected View panelTop;

	@InjectView(android.R.id.list)
	protected SimpleListView list;

	@InjectView(R.id.btn_demo)
	protected Button btnDemo;

	@InjectView(R.id.img_profile)
	protected ImageView imgProfile;

	@InjectView(R.id.swipe_refresh)
	protected SwipeRefreshLayout refreshView;

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
		ValidateActivityShortcut.start(this, R.anim.fake_fade_in_instant, R.anim.slide_out_down, EXTRA_LOADER_ANIMATION_SCALE_DOWN);
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
		refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				refresh();
			}
		});
		if(mAdapter == null) {
			refresh();
		} else {
			initList();
		}
	}

	private void refresh() {
		api.getRestaurants().subscribe(new Action1<RestaurantsResponse>() {
			@Override
			public void call(final RestaurantsResponse restaurants) {
				mAdapter = new RestaurantsAdapter(getActivity(), restaurants.getItems());
				initList();
				refreshView.setRefreshing(false);
			}
		}, new OmnomBaseErrorHandler(this) {
			@Override
			protected void onThrowable(final Throwable throwable) {
				Log.e(TAG, "getRestaurants", throwable);
				refreshView.setRefreshing(false);
			}
		});
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
		final SwingBottomInAnimationAdapter adapter = new SwingBottomInAnimationAdapter(mAdapter);
		adapter.setAbsListView(list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setScrollEnabled(true);
		refreshView.setEnabled(true);
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
		refreshView.setEnabled(false);
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
		refreshView.setEnabled(true);
		panelTop.setTranslationY(0);
		list.setTranslationY(0);
		if(mAdapter != null) {
			mAdapter.setSelected(-1);
			mAdapter.notifyDataSetChanged();
		}
		if(nextView != null) {
			nextView.setAlpha(1);
		}
		ViewUtils.setHeight(selectedImgCover, getResources().getDimensionPixelSize(R.dimen.restaurant_cover_height_small));
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_down);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_restaurants_list;
	}
}
