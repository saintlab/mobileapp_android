package com.omnom.android.activity.menu;

import android.content.Intent;
import android.os.Bundle;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.fragment.menu.AddItemFragment;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.model.UserOrderData;

/**
 * Created by Ch3D on 02.02.2015.
 */
public abstract class MenuFragmentActivity extends BaseOmnomFragmentActivity {

	protected UserOrder mOrder;

	public void updateItem(final Item item, final int count) {
		assert mOrder != null;
		mOrder.itemsTable().put(item.id(), UserOrderData.create(count, item));
		refresh();
	}

	protected abstract void refresh();

	@Override
	protected void handleIntent(final Intent intent) {
		super.handleIntent(intent);
		mOrder = intent.getParcelableExtra(EXTRA_ORDER);
	}

	@Override
	protected void handleSavedState(final Bundle savedInstanceState) {
		super.handleSavedState(savedInstanceState);
		if(savedInstanceState != null) {
			mOrder = savedInstanceState.getParcelable(EXTRA_ORDER);
		}
		if(mOrder == null) {
			mOrder = UserOrder.create();
		}
	}

	public void showAddFragment(final Item item) {
		AddItemFragment.show(getSupportFragmentManager(), R.id.root, mOrder, item);
	}
}
