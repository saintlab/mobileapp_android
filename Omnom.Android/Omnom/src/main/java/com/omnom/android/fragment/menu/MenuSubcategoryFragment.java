package com.omnom.android.fragment.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.omnom.android.R;
import com.omnom.android.adapter.MenuCategoryItemsAdapter;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.model.UserOrderData;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.view.StickyListView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Ch3D on 02.02.2015.
 */
public class MenuSubcategoryFragment extends Fragment {

	public static void show(final FragmentManager manager, final UserOrder order, final Menu menu, final int position) {
		manager.beginTransaction()
		       .addToBackStack(null)
		       .setCustomAnimations(R.anim.slide_in_right,
		                            R.anim.slide_out_right,
		                            R.anim.slide_in_right,
		                            R.anim.slide_out_right)
		       .add(R.id.fragment_container, MenuSubcategoryFragment.newInstance(order, menu, position))
		       .commit();
	}

	private static Fragment newInstance(final UserOrder order, final Menu menu, final int position) {
		assert order != null && menu != null && position >= 0;

		final MenuSubcategoryFragment fragment = new MenuSubcategoryFragment();
		final Bundle args = new Bundle();
		args.putParcelable(Extras.EXTRA_ORDER, order);
		args.putParcelable(Extras.EXTRA_RESTAURANT_MENU, menu);
		args.putInt(Extras.EXTRA_POSITION, position);
		fragment.setArguments(args);
		return fragment;
	}

	@InjectView(android.R.id.list)
	protected StickyListView mListView;

	@Inject
	protected Bus mBus;

	private UserOrder mOrder;

	private Menu mMenu;

	private int mPosition;

	private MenuCategoryItemsAdapter mAdapter;

	@Subscribe
	public void onOrderUpdate(OrderUpdateEvent event) {
		final Item item = event.getItem();
		mOrder.itemsTable().put(item.id(), UserOrderData.create(event.getCount(), item));
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(Extras.EXTRA_ORDER);
			mMenu = getArguments().getParcelable(Extras.EXTRA_RESTAURANT_MENU);
			mPosition = getArguments().getInt(Extras.EXTRA_POSITION, -1);
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle bundle) {
		return getActivity().getLayoutInflater().inflate(R.layout.activity_menu_subcategory, null);
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
				// MenuItemDetailsActivity.start(MenuSubcategoryActivity.this, mOrder, mAdapter.getItem(position), REQUEST_CODE_MENU_ITEM);
			}
		});
	}

	public void showAddFragment(final Item item) {
		AddItemFragment.show(getFragmentManager(), R.id.fragment_container, mOrder, item);
	}
}
