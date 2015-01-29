package com.omnom.android.activity;

import android.content.Intent;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.adapter.MenuCategoryItemsAdapter;
import com.omnom.android.fragment.menu.AddItemFragment;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.utils.view.StickyListView;

import butterknife.InjectView;
import butterknife.OnItemClick;

/**
 * Created by Ch3D on 27.01.2015.
 */
public class MenuSubcategoryActivity extends BaseOmnomFragmentActivity {

	@InjectView(android.R.id.list)
	protected StickyListView mListView;

	private Menu mMenu;

	private int mPosition;

	private MenuCategoryItemsAdapter mAdapter;

	private UserOrder mOrder;

	@Override
	protected void handleIntent(final Intent intent) {
		mMenu = intent.getParcelableExtra(EXTRA_RESTAURANT_MENU);
		mPosition = intent.getIntExtra(EXTRA_POSITION, -1);
	}

	@OnItemClick(android.R.id.list)
	public void onListItemClick(final int position) {
		// TODO: Open dish details
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.nothing, R.anim.slide_out_right);
	}

	@Override
	public void initUi() {
		mAdapter = new MenuCategoryItemsAdapter(this, mMenu.categories().get(mPosition), mMenu.items().items());
		mListView.setAdapter(mAdapter);
		mListView.setShadowVisible(false);
		mListView.setDividerHeight(0);
		mListView.setDivider(null);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_menu_subcategory;
	}

	public void showAddFragment(final Item item) {
		getSupportFragmentManager().beginTransaction()
		                           .addToBackStack(null)
		                           .setCustomAnimations(R.anim.slide_in_up,
		                                                R.anim.slide_out_down,
		                                                R.anim.slide_in_up,
		                                                R.anim.slide_out_down)
		                           .replace(R.id.root, AddItemFragment.newInstance(mOrder, item))
		                           .commit();
	}
}
