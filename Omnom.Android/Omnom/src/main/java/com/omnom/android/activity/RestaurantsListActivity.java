package com.omnom.android.activity;

import android.animation.Animator;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.activity.validate.ValidateActivity;
import com.omnom.android.adapter.RestaurantsAdapter;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.restaurateur.model.restaurant.RestaurantsResponse;
import com.omnom.android.utils.loader.LoaderView;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.view.SimpleListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;

public class RestaurantsListActivity extends BaseOmnomActivity
		implements AdapterView.OnItemClickListener {

	public static final double LOGO_SCALE_SMALL = 0.47;

	public static final double LOGO_SCALE_LARGE = 0.65;

	public static final int SCROLL_DURATION = 300;

	private static final String TAG = RestaurantsListActivity.class.getSimpleName();

	private static final int SLIDE_LEFT = 0;

	private static final int SLIDE_UP = 1;

	private class RestaurantsComparator implements Comparator<Restaurant> {

		@Override
		public int compare(Restaurant lhs, Restaurant rhs) {
			if(lhs.distance() == null && rhs.distance() == null) {
				return 0;
			} else if(lhs.distance() == null) {
				return -1;
			} else if(rhs.distance() == null) {
				return 1;
			}

			return (int) (lhs.distance() - rhs.distance()) * 10;
		}

	}

	public static void start(BaseOmnomActivity activity) {
		start(activity, false);
	}

	public static void start(BaseOmnomActivity activity, boolean finish) {
		activity.start(new Intent(activity, RestaurantsListActivity.class), R.anim.slide_in_up,
		               R.anim.fake_fade_out_long, finish);
	}

	public static void start(BaseOmnomFragmentActivity activity, boolean finish) {
		activity.start(new Intent(activity, RestaurantsListActivity.class), R.anim.slide_in_up,
		               R.anim.fake_fade_out_long, finish);
	}

	private static Intent getIntent(final Context context, final List<Restaurant> restaurants) {
		final Intent intent = new Intent(context, RestaurantsListActivity.class);
		intent.putParcelableArrayListExtra(EXTRA_RESTAURANTS,
		                                   new ArrayList<Parcelable>(restaurants));
		return intent;
	}

	public static void start(BaseOmnomFragmentActivity activity, List<Restaurant> restaurants) {
		final Intent intent = getIntent(activity, restaurants);
		activity.start(intent, R.anim.slide_in_up, R.anim.fake_fade_out_long, true);
	}

	public static void startLeft(final ValidateActivity activity) {
		final Intent intent = new Intent(activity, RestaurantsListActivity.class);
		intent.putExtra(EXTRA_ANIMATE, SLIDE_LEFT);
		activity.start(intent, R.anim.slide_in_left, R.anim.slide_out_right, true);
	}

	@InjectView(R.id.panel_top)
	protected View panelTop;

	@InjectView(android.R.id.list)
	protected SimpleListView list;

	@InjectView(R.id.img_profile)
	protected ImageView imgProfile;

	@InjectView(R.id.swipe_refresh)
	protected SwipeRefreshLayout refreshView;

	@InjectView(R.id.panel_demo)
	protected View panelDemo;

	@InjectView(R.id.scan_qr)
	protected TextView scanQr;

	@Inject
	RestaurateurObservableApi api;

	private RestaurantsAdapter mAdapter;

	@Nullable
	private ArrayList<Restaurant> mRestaurants;

	private LoaderView selectedCover;

	private boolean mItemClicked = false;

	private View footer;

	private int mAnimation;

	private int logoSizeSmall;

	private int logoSizeLarge;

	@OnClick(R.id.scan_qr)
	public void doQrShortcut() {
		ValidateActivityShortcut.start(this, R.anim.slide_in_right, R.anim.slide_out_left,
		                               EXTRA_LOADER_ANIMATION_SCALE_DOWN);
	}

	@OnClick(R.id.img_profile)
	public void doUserProfile() {
		UserProfileActivity.startSliding(this, -1, StringUtils.EMPTY_STRING);
	}

	@OnClick({R.id.panel_demo, R.id.btn_demo})
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
			Collections.sort(mRestaurants, new RestaurantsComparator());
			mAdapter = new RestaurantsAdapter(this, mRestaurants);
		}
	}

	@Override
	public void initUi() {
		final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		logoSizeSmall = (int) (displayMetrics.widthPixels * LOGO_SCALE_SMALL + 0.5);
		logoSizeLarge = LoaderView.getLoaderSizeDefault(this);
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

	@Override
	protected void onStart() {
		super.onStart();
		scanQr.setPressed(false);
	}

	private void refresh() {
		Observable<RestaurantsResponse> restaurantsObservable;
		if(getLocation() != null) {
			restaurantsObservable = api.getRestaurantsAll(getLocation().getLatitude(), getLocation().getLongitude());
		} else {
			restaurantsObservable = api.getRestaurantsAll();
		}
		restaurantsObservable.subscribe(new Action1<RestaurantsResponse>() {
			@Override
			public void call(final RestaurantsResponse restaurants) {
				Collections.sort(restaurants.getItems(), new RestaurantsComparator());
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
				AndroidUtils.sendFeedbackEmail(getActivity(), R.string.email_subject_feedback,
				                               R.string.email_subject_feedback);
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
	public void onItemClick(final AdapterView<?> parent, final View view, final int position,
	                        final long id) {
		if(mItemClicked) {
			// skip item selection
			return;
		}
		mItemClicked = true;

		final int paddingDiff = (int) (getResources().getDimension(R.dimen.image_button_size) +
				getResources().getDimension(R.dimen.activity_vertical_margin) * 2 -
				getResources().getDimension(R.dimen.activity_vertical_margin_large) + 0.5);
		final int height = panelTop.getHeight();

		Restaurant item = (Restaurant) mAdapter.getItem(position);
		if(RestaurantHelper.isBar(item)) {
			ValidateActivity.start(this, R.anim.slide_in_right, R.anim.slide_out_left, EXTRA_LOADER_ANIMATION_SCALE_DOWN, item);
		} else {
			mAdapter.setSelected(position);
			list.smoothScrollToPositionFromTop(position, 0, SCROLL_DURATION);
			selectedCover = (LoaderView) view.findViewById(R.id.cover);
			animateRestaurant(position, view, height, paddingDiff,
			                  (position + 2 == list.getCount()));
		}
	}

	private void animateRestaurant(final int position, final View view, final int height,
	                               final int paddingDiff, final boolean isLast) {
		final int duration = getResources().getInteger(R.integer.default_animation_duration_short);

		panelTop.animate().translationYBy(-height).setDuration(duration).start();
		panelDemo.animate().alpha(0).setDuration(duration).start();
		if(!isLast) {
			animateLogo(duration);
			animateRefreshView(-height, 0, paddingDiff, duration, position);
		}
		AnimationUtils
				.smoothScrollToPositionFromTop(list, position, SCROLL_DURATION, new Runnable() {
					@Override
					public void run() {
						if(isLast) {
							final int halfDuration = (int) (duration / 2.0);
							animateRefreshView(-height, -view.getTop(), paddingDiff, halfDuration,
							                   position);
							list.setScrollEnabled(false);
							refreshView.setEnabled(false);
							footer.animate().alpha(0).setDuration(halfDuration).start();
							animateLogo(halfDuration);
						}
					}
				});
	}

	private void animateLogo(final int duration) {
		if(selectedCover != null) {
			AnimationUtils.scale(selectedCover, logoSizeLarge, duration);
			selectedCover.scaleUp(duration, logoSizeLarge, true, new Runnable() {
				@Override
				public void run() {

				}
			});
		}
	}

	private void animateRefreshView(final int listTranslation, final int topTranslation,
	                                final int paddingDiff, final int duration,
	                                final int position) {
		refreshView.animate()
		           .translationYBy(listTranslation + topTranslation + paddingDiff)
		           .setDuration(duration)
		           .setListener(new Animator.AnimatorListener() {

			           private boolean finished = false;

			           @Override
			           public void onAnimationStart(Animator animation) {

			           }

			           @Override
			           public void onAnimationEnd(Animator animation) {
				           if(!finished) {
					           finished = true;
					           startRestaurantActivity(position, topTranslation);
				           }
			           }

			           @Override
			           public void onAnimationCancel(Animator animation) {

			           }

			           @Override
			           public void onAnimationRepeat(Animator animation) {

			           }
		           })
		           .start();
	}

	private void startRestaurantActivity(final int position, final int topTranslation) {
		list.requestLayout();
		final Restaurant item = (Restaurant) mAdapter.getItem(position);
		RestaurantActivity.start(RestaurantsListActivity.this, item, topTranslation);
		mItemClicked = false;
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode,
	                                final Intent data) {
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
		}
		panelDemo.animate().alpha(1).start();
		if(selectedCover != null) {
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) selectedCover
					.getLayoutParams();
			layoutParams.width = logoSizeSmall;
			layoutParams.height = logoSizeSmall;
			selectedCover.setSize(logoSizeSmall, logoSizeSmall);
			list.setSelection(list.getSelectedItemPosition());
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
