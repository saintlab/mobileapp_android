package com.omnom.android.fragment.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.adapter.MenuCategoryItemsAdapter;
import com.omnom.android.fragment.base.BaseFragment;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.utils.MenuHelper;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.utils.ViewUtils;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Ch3D on 03.02.2015.
 */
public class MenuItemDetailsFragment extends BaseFragment implements View.OnClickListener {

	public static Fragment newInstance(Menu menu, final UserOrder order, final Item item) {
		final MenuItemDetailsFragment fragment = new MenuItemDetailsFragment();
		final Bundle args = new Bundle();
		args.putParcelable(Extras.EXTRA_ORDER, order);
		args.putParcelable(Extras.EXTRA_MENU_ITEM, item);
		args.putParcelable(Extras.EXTRA_RESTAURANT_MENU, menu);
		fragment.setArguments(args);
		return fragment;
	}

	public static void show(final FragmentManager manager, Menu menu, final UserOrder order, final Item item) {
		manager.beginTransaction()
		       .addToBackStack(null)
		       .setCustomAnimations(R.anim.slide_in_right,
		                            R.anim.slide_out_right,
		                            R.anim.slide_in_right,
		                            R.anim.slide_out_right)
		       .add(R.id.root, MenuItemDetailsFragment.newInstance(menu, order, item))
		       .commit();
	}

	@InjectView(R.id.txt_info_additional)
	protected TextView mTxtAdditional;

	@InjectView(R.id.panel_bottom)
	protected LinearLayout mPanelRecommendations;

	protected UserOrder mOrder;

	private MenuCategoryItemsAdapter.ViewHolder holder;

	private Item mItem;

	private Menu mMenu;

	@OnClick(R.id.btn_close)
	public void onClose() {
		getFragmentManager().popBackStack();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
	                         @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(Extras.EXTRA_ORDER);
			mItem = getArguments().getParcelable(Extras.EXTRA_MENU_ITEM);
			mMenu = getArguments().getParcelable(Extras.EXTRA_RESTAURANT_MENU);
		}
		if(mOrder == null) {
			mOrder = UserOrder.create();
		}
		return getActivity().getLayoutInflater().inflate(R.layout.fragment_menu_item_details, null);
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		final View viewRoot = view.findViewById(R.id.root);
		ButterKnife.inject(this, view);
		holder = new MenuCategoryItemsAdapter.ViewHolder(viewRoot);
		holder.setDelimiterVisible(false);
		refresh();

		final String description = mItem.description();
		ViewUtils.setVisible(mTxtAdditional, !TextUtils.isEmpty(description));
		mTxtAdditional.setText(description);

		final List<String> recommendations = mItem.recommendations();
		final boolean hasRecommendations = recommendations != null && recommendations.size() > 0;
		ViewUtils.setVisible(mPanelRecommendations, hasRecommendations);
		if(hasRecommendations) {
			for(String recId : recommendations) {
				final View itemView = LayoutInflater.from(view.getContext()).inflate(R.layout.item_menu_dish, null, false);
				MenuCategoryItemsAdapter.ViewHolder holder = new MenuCategoryItemsAdapter.ViewHolder(itemView);

				final Item item = MenuHelper.getItem(mMenu, recId);
				holder.updateState(mOrder, item);
				holder.bind(item);
				itemView.setTag(holder);
				itemView.setTag(R.id.item, item);

				final View btnApply = itemView.findViewById(R.id.btn_apply);
				btnApply.setOnClickListener(this);
				btnApply.setTag(R.id.item, item);

				mPanelRecommendations.addView(itemView);
			}
		}
	}

	private void refresh() {
		holder.updateState(mOrder, mItem);
		holder.bind(mItem);

		final int childCount = mPanelRecommendations.getChildCount();
		for(int i = 0; i < childCount; i++) {
			final View childAt = mPanelRecommendations.getChildAt(i);
			final MenuCategoryItemsAdapter.ViewHolder holder = (MenuCategoryItemsAdapter.ViewHolder) childAt.getTag();
			final Item item = (Item) childAt.getTag(R.id.item);
			if(holder != null && item != null) {
				holder.updateState(mOrder, item);
				holder.bind(item);
			}
		}
	}

	@Subscribe
	public void onOrderUpdate(OrderUpdateEvent event) {
		if(event == null || event.getItem() == null) {
			return;
		}
		refresh();
	}

	@OnClick(R.id.btn_apply)
	public void onApply() {
		MenuItemAddFragment.show(getFragmentManager(), mMenu.modifiers(), mOrder, mItem);
	}

	@Override
	public void onClick(final View v) {
		if(v.getId() == R.id.btn_apply) {
			final Item item = (Item) v.getTag(R.id.item);
			if(item != null) {
				MenuItemAddFragment.show(getFragmentManager(), mMenu.modifiers(), mOrder, item);
			}
		}
	}
}
