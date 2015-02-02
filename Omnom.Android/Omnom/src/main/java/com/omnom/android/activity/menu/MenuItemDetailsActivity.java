package com.omnom.android.activity.menu;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.adapter.MenuCategoryItemsAdapter;
import com.omnom.android.fragment.menu.AddItemFragment;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.utils.activity.OmnomActivity;

import butterknife.InjectView;
import butterknife.OnClick;

public class MenuItemDetailsActivity extends MenuFragmentActivity {

	public static void start(OmnomActivity activity, UserOrder order, Item item, final int code) {
		final Intent intent = new Intent(activity.getActivity(), MenuItemDetailsActivity.class);
		intent.putExtra(EXTRA_ORDER, order);
		intent.putExtra(EXTRA_MENU_ITEM, item);
		activity.startForResult(intent, R.anim.slide_in_right, R.anim.nothing, code);
	}

	@InjectView(R.id.txt_info_additional)
	protected TextView mTxtAdditional;

	private MenuCategoryItemsAdapter.ViewHolder holder;

	private Item mItem;

	@Override
	public void finish() {
		final Intent data = new Intent();
		data.putExtra(EXTRA_ORDER_DATA, mOrder.itemsTable().get(mItem.id()));
		setResult(RESULT_OK, data);
		super.finish();
		overridePendingTransition(R.anim.nothing, R.anim.slide_out_right);
	}

	@Override
	protected void refresh() {
		holder.updateState(mOrder, mItem);
		holder.bind(mItem);
	}

	@Override
	protected void handleIntent(final Intent intent) {
		super.handleIntent(intent);
		mItem = intent.getParcelableExtra(EXTRA_MENU_ITEM);
	}

	@OnClick(R.id.btn_apply)
	public void onApply(View v) {
		AddItemFragment.show(getSupportFragmentManager(), R.id.root, mOrder, mItem);
	}

	@Override
	public void initUi() {
		final View viewById = findViewById(R.id.root);
		holder = new MenuCategoryItemsAdapter.ViewHolder(viewById);
		holder.setDelimiterVisible(false);
		refresh();
		mTxtAdditional.setText(mItem.description());
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_menu_item_details;
	}
}
