package com.omnom.android.activity;

import android.content.Intent;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.adapter.MenuCategoryItemsAdapter;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.utils.view.StickyListView;

import butterknife.InjectView;

/**
 * Created by Ch3D on 27.01.2015.
 */
public class MenuSubcategoryActivity extends BaseOmnomFragmentActivity {

	@InjectView(android.R.id.list)
	protected StickyListView mListView;

	private Menu mMenu;

	private int mPosition;

	private MenuCategoryItemsAdapter mAdapter;

	@Override
	protected void handleIntent(final Intent intent) {
		mMenu = intent.getParcelableExtra(EXTRA_RESTAURANT_MENU);
		mPosition = intent.getIntExtra(EXTRA_POSITION, -1);
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
}
