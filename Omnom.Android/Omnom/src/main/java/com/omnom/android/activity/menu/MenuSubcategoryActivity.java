package com.omnom.android.activity.menu;

import android.content.Intent;

import com.omnom.android.R;
import com.omnom.android.adapter.MenuCategoryItemsAdapter;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.model.UserOrderData;
import com.omnom.android.utils.activity.OmnomActivity;
import com.omnom.android.utils.view.StickyListView;

import butterknife.InjectView;

/**
 * Created by Ch3D on 27.01.2015.
 */
public class MenuSubcategoryActivity extends MenuFragmentActivity {

	public static void start(final OmnomActivity activity, final UserOrder order, final Menu menu, final int position, final int code) {
		final Intent intent = new Intent(activity.getActivity(), MenuSubcategoryActivity.class);
		intent.putExtra(EXTRA_ORDER, order);
		intent.putExtra(EXTRA_RESTAURANT_MENU, menu);
		intent.putExtra(EXTRA_POSITION, position);
		activity.startForResult(intent, R.anim.slide_in_right, R.anim.nothing, code);
	}

	@InjectView(android.R.id.list)
	protected StickyListView mListView;

	private Menu mMenu;

	private int mPosition;

	private MenuCategoryItemsAdapter mAdapter;

	@Override
	protected void refresh() {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void handleIntent(final Intent intent) {
		super.handleIntent(intent);
		mMenu = intent.getParcelableExtra(EXTRA_RESTAURANT_MENU);
		mPosition = intent.getIntExtra(EXTRA_POSITION, -1);
	}

	@Override
	public void finish() {
		final Intent data = new Intent();
		data.putExtra(EXTRA_ORDER, mOrder);
		setResult(RESULT_OK, data);
		super.finish();
		overridePendingTransition(R.anim.nothing, R.anim.slide_out_right);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_MENU_ITEM && resultCode == RESULT_OK) {
			final UserOrderData orderData = data.getParcelableExtra(EXTRA_ORDER_DATA);
			if(orderData != null) {
				mOrder.itemsTable().put(orderData.item().id(), orderData);
				refresh();
			}
		}
	}

	@Override
	public void initUi() {
		//mAdapter = new MenuCategoryItemsAdapter(this, mOrder, mMenu.categories().get(mPosition), mMenu.items().items());
		//mListView.setAdapter(mAdapter);
		//mListView.setShadowVisible(false);
		//mListView.setDividerHeight(0);
		//mListView.setDivider(null);
		//mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		//	@Override
		//	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		//		if(mAdapter == null || mOrder == null) {
		//			return;
		//		}
		//		MenuItemDetailsActivity.start(MenuSubcategoryActivity.this, mOrder, mAdapter.getItem(position), REQUEST_CODE_MENU_ITEM);
		//	}
		//});
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_menu_subcategory;
	}
}
