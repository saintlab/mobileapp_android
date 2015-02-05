package com.omnom.android.fragment.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;

import com.omnom.android.R;
import com.omnom.android.adapter.MenuCategoryItemsAdapter;
import com.omnom.android.fragment.base.BaseFragment;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.model.UserOrderData;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.utils.view.StickyListView;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Ch3D on 02.02.2015.
 */
public class MenuSubcategoryFragment extends BaseFragment {

	public static void show(final FragmentManager manager, final UserOrder order, final Menu menu, final int position, final float ypos) {
		manager.beginTransaction()
		       .addToBackStack(null)
		       .setCustomAnimations(R.anim.fold_in,
		                            R.anim.slide_out_right,
		                            R.anim.fold_in,
		                            R.anim.slide_out_right)
		       .add(R.id.fragment_container, MenuSubcategoryFragment.newInstance(order, menu, position, ypos))
		       .commit();
	}

	private static Fragment newInstance(final UserOrder order, final Menu menu, final int position, final float ypos) {
		assert order != null && menu != null && position >= 0;

		final MenuSubcategoryFragment fragment = new MenuSubcategoryFragment();
		final Bundle args = new Bundle();
		args.putParcelable(Extras.EXTRA_ORDER, order);
		args.putParcelable(Extras.EXTRA_RESTAURANT_MENU, menu);
		args.putInt(Extras.EXTRA_POSITION, position);
		args.putFloat(Extras.EXTRA_PIVOT_Y, ypos);
		fragment.setArguments(args);
		return fragment;
	}

	@InjectView(android.R.id.list)
	protected StickyListView mListView;

	private UserOrder mOrder;

	private Menu mMenu;

	private int mPosition;

	private MenuCategoryItemsAdapter mAdapter;

	private float mPivotY;

	@Subscribe
	public void onOrderUpdate(OrderUpdateEvent event) {
		final Item item = event.getItem();
		mOrder.itemsTable().put(item.id(), UserOrderData.create(event.getCount(), item));
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public Animation onCreateAnimation(final int transit, final boolean enter, final int nextAnim) {
		if(!enter) {
			AnimationUtils.animateAlpha(mListView, false);
		}

		final ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.0f, enter ? 0.0f : 1.0f,
		                                                         enter ? 1.0f : 0.0f,
		                                                         Animation.RELATIVE_TO_SELF, 0.5f,
		                                                         Animation.RELATIVE_TO_SELF, mPivotY);
		scaleAnimation.setDuration(350);
		scaleAnimation.setInterpolator(new LinearInterpolator());
		scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(final Animation animation) {
				if(enter) {
					ViewUtils.setVisible2(mListView, false);
				}
			}

			@Override
			public void onAnimationEnd(final Animation animation) {
				if(enter) {
					AnimationUtils.animateAlpha(mListView, true);
				}
			}

			@Override
			public void onAnimationRepeat(final Animation animation) {

			}
		});
		return scaleAnimation;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(Extras.EXTRA_ORDER);
			mMenu = getArguments().getParcelable(Extras.EXTRA_RESTAURANT_MENU);
			mPosition = getArguments().getInt(Extras.EXTRA_POSITION, -1);
			mPivotY = getArguments().getFloat(Extras.EXTRA_PIVOT_Y, -1);
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle bundle) {
		return getActivity().getLayoutInflater().inflate(R.layout.fragment_menu_subcategory, null);
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.inject(this, view);

		mAdapter = new MenuCategoryItemsAdapter(this, mOrder, mMenu.categories().get(mPosition), mMenu.items().items());
		mListView.setAdapter(mAdapter);
		mListView.setShadowVisible(false);
		mListView.setDividerHeight(0);
		mListView.setDivider(null);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				if(mAdapter == null || mOrder == null) {
					return;
				}
				MenuItemDetailsFragment.show(getFragmentManager(), mMenu, mOrder, mAdapter.getItem(position));
			}
		});
	}

	public void showAddFragment(final Item item) {
		MenuItemAddFragment.show(getFragmentManager(), mMenu.modifiers(), mOrder, item);
	}
}
