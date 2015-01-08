package com.omnom.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.adapter.RestaurantsAdapter;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantsResponse;
import com.omnom.android.utils.loader.LoaderView;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.view.SimpleListView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.functions.Action1;

public class RestaurantsListActivity extends BaseOmnomActivity implements AdapterView.OnItemClickListener {

	public static final double LOGO_SCALE_SMALL = 0.47;

	public static final double LOGO_SCALE_LARGE = 0.55;

	public static final int SCROLL_DURATION = 200;

	private static final String TAG = RestaurantsListActivity.class.getSimpleName();

	private static final int SLIDE_LEFT = 0;

	private static final int SLIDE_UP = 1;

	public static void start(BaseOmnomActivity activity) {
		start(activity, false);
	}

	public static void start(BaseOmnomActivity activity, boolean finish) {
		activity.start(new Intent(activity, RestaurantsListActivity.class), R.anim.slide_in_up, R.anim.fake_fade_out_long, finish);
	}

	public static void start(BaseOmnomFragmentActivity activity, boolean finish) {
		activity.start(new Intent(activity, RestaurantsListActivity.class), R.anim.slide_in_up, R.anim.fake_fade_out_long, finish);
	}

	private static Intent getIntent(final Context context, final List<Restaurant> restaurants) {
		final Intent intent = new Intent(context, RestaurantsListActivity.class);
		intent.putParcelableArrayListExtra(EXTRA_RESTAURANTS, new ArrayList<Parcelable>(restaurants));
		return intent;
	}

	public static void start(BaseOmnomActivity activity, List<Restaurant> restaurants) {
		final Intent intent = getIntent(activity, restaurants);
		activity.start(intent, R.anim.slide_in_up, R.anim.fake_fade_out_long, true);
	}

	public static void start(BaseOmnomFragmentActivity activity, List<Restaurant> restaurants) {
		final Intent intent = getIntent(activity, restaurants);
		activity.start(intent, R.anim.slide_in_up, R.anim.fake_fade_out_long, true);
	}

	public static void startLeft(final ValidateActivity activity) {
		final Intent intent = new Intent(activity, RestaurantsListActivity.class);
		intent.putExtra(EXTRA_ANIMATE, SLIDE_LEFT);
		activity.startForResult(intent, R.anim.slide_in_left, R.anim.slide_out_right, REQUEST_CODE_CHANGE_TABLE);
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

	private LoaderView selectedCover;

	private View nextView;

	private boolean mItemClicked = false;

	private View footer;

	private int mAnimation;

	private int logoSizeSmall;

	private int logoSizeLarge;

	@OnClick(R.id.txt_qr)
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
		mAnimation = intent.getIntExtra(EXTRA_ANIMATE, -1);
		if(getIntent().hasExtra(EXTRA_RESTAURANTS)) {
			mRestaurants = getIntent().getParcelableArrayListExtra(EXTRA_RESTAURANTS);
			mAdapter = new RestaurantsAdapter(this, mRestaurants);
		}
	}

	@Override
	public void initUi() {
		final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		logoSizeSmall = (int) (displayMetrics.widthPixels * LOGO_SCALE_SMALL + 0.5);
		logoSizeLarge = (int) (displayMetrics.widthPixels * LOGO_SCALE_LARGE + 0.5);
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
		final int paddingDiff = (int) (getResources().getDimension(R.dimen.image_button_size) +
								       getResources().getDimension(R.dimen.activity_vertical_margin) * 2 -
								       getResources().getDimension(R.dimen.activity_vertical_margin_large) + 0.5);
		final int height = panelTop.getHeight();

		mItemClicked = true;
		mAdapter.setSelected(position);
		mAdapter.notifyDataSetChanged();
		list.smoothScrollToPositionFromTop(position, 0, SCROLL_DURATION);
		panelTop.animate().translationYBy(-height).start();
		selectedCover = (LoaderView) view.findViewById(R.id.cover);

		if(position + 2 == list.getCount()) {
			postDelayed(SCROLL_DURATION, new Runnable() {
				@Override
				public void run() {
					animateRestaurant(position, view, height, paddingDiff, true);
				}
			});
		} else {
			animateRestaurant(position, view, height, paddingDiff, false);
		}
	}

	private void animateRestaurant(final int position, final View view, final int height,
	                               final int paddingDiff, boolean isLast) {
		final int listTranslation;
		final int topTranslation;
		if (isLast) {
			footer.animate().alpha(0).start();
			topTranslation = -view.getTop();
			listTranslation = -height;
		} else {
			topTranslation = 0;
			listTranslation = -height;
		}
		refreshView.animate().translationYBy(listTranslation + topTranslation + paddingDiff).start();

		nextView = list.getChildAt(position + 1);
		list.setScrollEnabled(false);
		refreshView.setEnabled(false);
		if(nextView != null) {
			nextView.animate().alpha(0).start();
		}
		final int duration = getResources().getInteger(R.integer.default_animation_duration_medium);
		AnimationUtils.scale(selectedCover, logoSizeLarge, duration, new Runnable() {
			@Override
			public void run() {
			}
		});
		selectedCover.scaleUp(duration, logoSizeLarge, true, new Runnable() {
			@Override
			public void run() {
				list.requestLayout();
				final Restaurant item = (Restaurant) mAdapter.getItem(position);
				RestaurantActivity.start(RestaurantsListActivity.this, item, topTranslation);
				mItemClicked = false;
			}
		});
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_CHANGE_TABLE && resultCode == RESULT_CODE_TABLE_CHANGED) {
			setResult(RESULT_CODE_TABLE_CHANGED);
			RestaurantsListActivity.super.finish();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		mItemClicked = false;
		list.setScrollEnabled(true);
		refreshView.setEnabled(true);
		panelTop.setTranslationY(0);
		if(footer != null && footer.getAlpha() < 1) {
			footer.animate().alpha(1).start();
		}
		refreshView.setTranslationY(0);
		if(mAdapter != null) {
			mAdapter.setSelected(-1);
			mAdapter.notifyDataSetChanged();
		}
		if(nextView != null) {
			nextView.setAlpha(1);
		}
		if (selectedCover != null) {
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) selectedCover.getLayoutParams();
			layoutParams.width = logoSizeSmall;
			layoutParams.height = logoSizeSmall;
			selectedCover.setSize(logoSizeSmall, logoSizeSmall);
		}
	}

	@Override
	public void finish() {
		super.finish();
		if(mAnimation == SLIDE_LEFT) {
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		} else {
			overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_down);
		}
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_restaurants_list;
	}
}
