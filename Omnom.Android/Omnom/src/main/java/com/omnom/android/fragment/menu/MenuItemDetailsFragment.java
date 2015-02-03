package com.omnom.android.fragment.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.adapter.MenuCategoryItemsAdapter;
import com.omnom.android.fragment.base.BaseFragment;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.utils.Extras;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Ch3D on 03.02.2015.
 */
public class MenuItemDetailsFragment extends BaseFragment {

	public static Fragment newInstance(final UserOrder order, final Item item) {
		final MenuItemDetailsFragment fragment = new MenuItemDetailsFragment();
		final Bundle args = new Bundle();
		args.putParcelable(Extras.EXTRA_ORDER, order);
		args.putParcelable(Extras.EXTRA_MENU_ITEM, item);
		fragment.setArguments(args);
		return fragment;
	}

	public static void show(final FragmentManager manager, final UserOrder order, final Item item) {
		manager.beginTransaction()
		       .addToBackStack(null)
		       .setCustomAnimations(R.anim.slide_in_right,
		                            R.anim.slide_out_right,
		                            R.anim.slide_in_right,
		                            R.anim.slide_out_right)
		       .add(R.id.fragment_container, MenuItemDetailsFragment.newInstance(order, item))
		       .commit();
	}

	@InjectView(R.id.txt_info_additional)
	protected TextView mTxtAdditional;

	protected UserOrder mOrder;

	private MenuCategoryItemsAdapter.ViewHolder holder;

	private Item mItem;

	@Override
	public View onCreateView(final LayoutInflater inflater,
	                         @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(Extras.EXTRA_ORDER);
			mItem = getArguments().getParcelable(Extras.EXTRA_MENU_ITEM);
		}
		if(mOrder == null) {
			mOrder = UserOrder.create();
		}
		return getActivity().getLayoutInflater().inflate(R.layout.activity_menu_item_details, null);
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		final View viewRoot = view.findViewById(R.id.root);
		ButterKnife.inject(this, view);
		holder = new MenuCategoryItemsAdapter.ViewHolder(viewRoot);
		holder.setDelimiterVisible(false);
		refresh();
		mTxtAdditional.setText(mItem.description());
	}

	private void refresh() {
		holder.updateState(mOrder, mItem);
		holder.bind(mItem);
	}

	@Subscribe
	public void onOrderUpdate(OrderUpdateEvent event) {
		if(event == null || event.getItem() == null) {
			return;
		}
		if(mItem.id().equals(event.getItem().id())) {
			refresh();
		}
	}

	@OnClick(R.id.btn_apply)
	public void onApply() {
		MenuItemAddFragment.show(getFragmentManager(), mOrder, mItem);
	}
}
