package com.omnom.android.fragment.menu;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ListView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.adapter.MenuCategoriesAdapter;
import com.omnom.android.fragment.base.BaseFragment;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.model.UserOrderData;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.utils.ViewUtils;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by Ch3D on 02.02.2015.
 */
public class MenuFragment extends BaseFragment implements FragmentManager.OnBackStackChangedListener {

	public static final int DURATION_INCREMENT_MOVE_UP = 75;

	public static final int DURATION_INCREMENT_MOVE_DOWN = 50;

	public static final int TITLE_PADDING_TOP = 8;

	public static Fragment newInstance(final Menu menu, final Restaurant restaurant, final UserOrder order) {
		final MenuFragment fragment = new MenuFragment();
		final Bundle args = new Bundle();
		args.putParcelable(Extras.EXTRA_RESTAURANT_MENU, menu);
		args.putParcelable(Extras.EXTRA_RESTAURANT, restaurant);
		args.putParcelable(Extras.EXTRA_ORDER, order);
		fragment.setArguments(args);
		return fragment;
	}

	public static void show(final FragmentManager manager, final Restaurant rest, final Menu menu, final UserOrder order) {
		manager.beginTransaction()
		       .addToBackStack(null)
		       .setCustomAnimations(R.anim.slide_in_right,
		                            R.anim.slide_out_right,
		                            R.anim.slide_in_right,
		                            R.anim.slide_out_right)
		       .replace(R.id.fragment_container, MenuFragment.newInstance(menu, rest, order))
		       .commit();
	}

	@InjectView(android.R.id.list)
	protected ListView mListView;

	@InjectView(R.id.btn_previous)
	protected ImageView mImgPrev;

	@Nullable
	private Menu mMenu;

	private UserOrder mOrder;

	@Nullable
	private MenuCategoriesAdapter mAdapter;

	private Target mTarget;

	private Restaurant mRestaurant;

	private View mSelectedView;

	private int mCategoryItemHeight;

	private int mTitleAnimationPadding;

	private LinearInterpolator mInterpolator;

	private int mScreenHeight;

	@OnClick(R.id.btn_previous)
	public void onPrevious(View v) {
		getFragmentManager().popBackStack();
	}

	@Subscribe
	public void onOrderUpdate(OrderUpdateEvent event) {
		final Item item = event.getItem();
		mOrder.itemsTable().put(item.id(), UserOrderData.create(event.getCount(), item));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(Extras.EXTRA_ORDER);
			mRestaurant = getArguments().getParcelable(Extras.EXTRA_RESTAURANT);
			mMenu = getArguments().getParcelable(Extras.EXTRA_RESTAURANT_MENU);
		}
		if(mOrder == null) {
			mOrder = UserOrder.create();
		}
	}

	@OnItemClick(android.R.id.list)
	public void onListItemClick(final int position) {
		getFragmentManager().addOnBackStackChangedListener(this);
		mSelectedView = mListView.getChildAt(position);

		final float heightPixels = mListView.getContext().getResources().getDisplayMetrics().heightPixels;
		final int anchorY = mSelectedView.getBottom() + (mCategoryItemHeight / 2);
		final int ty = -mSelectedView.getBottom() + mCategoryItemHeight + mTitleAnimationPadding;

		mSelectedView.animate().translationY(ty)
		             .setInterpolator(mInterpolator)
		             .setDuration(getResources().getInteger(R.integer.default_animation_duration_short) + DURATION_INCREMENT_MOVE_UP)
		             .start();

		for(int i = position + 1; i <= mListView.getLastVisiblePosition(); i++) {
			final View v = mListView.getChildAt(i);
			v.animate().translationY(mScreenHeight - v.getBottom()).alpha(0)
			 .setInterpolator(mInterpolator)
			 .setDuration(getResources().getInteger(R.integer.default_animation_duration_short) + DURATION_INCREMENT_MOVE_UP)
			 .start();
		}

		for(int i = mListView.getFirstVisiblePosition(); i <= position - 1; i++) {
			final View v = mListView.getChildAt(i);
			v.animate().translationY(-(v.getTop() + mCategoryItemHeight)).alpha(0)
			 .setInterpolator(mInterpolator)
			 .setDuration(getResources().getInteger(R.integer.default_animation_duration_short) + DURATION_INCREMENT_MOVE_UP)
			 .start();
		}

		final float v = anchorY / heightPixels;
		MenuSubcategoryFragment.show(getFragmentManager(), mOrder, mMenu, position - mListView.getHeaderViewsCount(), v);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getFragmentManager().removeOnBackStackChangedListener(this);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle bundle) {
		return getActivity().getLayoutInflater().inflate(R.layout.fragment_menu, null);
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		ButterKnife.inject(this, view);

		mScreenHeight = getResources().getDisplayMetrics().heightPixels;
		mCategoryItemHeight = getResources().getDimensionPixelSize(R.dimen.menu_category_height);
		mTitleAnimationPadding = ViewUtils.dipToPixels(view.getContext(), TITLE_PADDING_TOP);
		mInterpolator = new LinearInterpolator();

		mAdapter = new MenuCategoriesAdapter(getActivity(), mMenu.getFilledCategories());
		View header = LayoutInflater.from(getActivity()).inflate(R.layout.item_menu_categories_header, null);
		mListView.addHeaderView(header, null, false);
		mListView.setAdapter(mAdapter);

		mTarget = new Target() {
			@Override
			public void onBitmapLoaded(final Bitmap bitmap, final Picasso.LoadedFrom from) {
				final BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
				drawable.setColorFilter(new PorterDuffColorFilter(
						getResources().getColor(R.color.menu_transparent_black),
						PorterDuff.Mode.DARKEN));
				final View root = view.findViewById(R.id.root);
				root.setBackgroundDrawable(drawable);
			}

			@Override
			public void onBitmapFailed(Drawable errorDrawable) {
			}

			@Override
			public void onPrepareLoad(Drawable placeHolderDrawable) {
			}
		};

		OmnomApplication.getPicasso(getActivity())
		                .load(RestaurantHelper.getBackground(mRestaurant, getResources().getDisplayMetrics()))
		                .noFade().into(mTarget);
	}

	@Override
	public void onBackStackChanged() {
		if(getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() == 1) {
			ViewUtils.setVisible(mImgPrev, true);

			final int positionForView = mListView.getPositionForView(mSelectedView);

			for(int i = positionForView + 1; i <= mListView.getLastVisiblePosition(); i++) {
				final View v = mListView.getChildAt(i);
				v.animate().translationY(0).alpha(1)
				 .setInterpolator(mInterpolator)
				 .setDuration(getResources().getInteger(R.integer.default_animation_duration_short) + DURATION_INCREMENT_MOVE_UP)
				 .start();
			}

			for(int i = 0; i <= positionForView - 1; i++) {
				final View v = mListView.getChildAt(i);
				v.animate().translationY(0).alpha(1)
				 .setInterpolator(mInterpolator)
				 .setDuration(getResources().getInteger(R.integer.default_animation_duration_short) + DURATION_INCREMENT_MOVE_DOWN)
				 .start();
			}

			mSelectedView.animate().translationY(0)
			             .setInterpolator(mInterpolator)
			             .setDuration(getResources().getInteger(R.integer.default_animation_duration_short)
					                          + DURATION_INCREMENT_MOVE_DOWN).start();
		} else {
			ViewUtils.setVisible(mImgPrev, false);
		}
	}
}
