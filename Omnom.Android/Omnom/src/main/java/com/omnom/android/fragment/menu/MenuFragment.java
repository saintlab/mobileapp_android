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
import android.widget.ImageView;
import android.widget.ListView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.ValidateActivity;
import com.omnom.android.adapter.MenuCategoriesAdapter;
import com.omnom.android.fragment.base.BaseFragment;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.model.UserOrderData;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.utils.Extras;
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
public class MenuFragment extends BaseFragment {

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

	@InjectView(R.id.img_profile)
	protected ImageView mImgProfile;

	@InjectView(R.id.btn_previous)
	protected ImageView mImgPrev;

	@Nullable
	private Menu mMenu;

	private UserOrder mOrder;

	@Nullable
	private MenuCategoriesAdapter mAdapter;

	private Target mTarget;

	private Restaurant mRestaurant;

	@OnClick(R.id.img_profile)
	public void onProfile(View v) {
		final ValidateActivity activity = (ValidateActivity) getActivity();
		if(activity != null) {
			activity.onProfile(v);
		}
	}

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
		MenuSubcategoryFragment.show(getFragmentManager(), mOrder, mMenu, position);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle bundle) {
		return getActivity().getLayoutInflater().inflate(R.layout.fragment_menu, null);
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.inject(this, view);
		mAdapter = new MenuCategoriesAdapter(getActivity(), mMenu.getFilledCategories());
		View header = LayoutInflater.from(getActivity()).inflate(R.layout.item_menu_category_header, null);
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
}
