package com.omnom.android.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.adapter.WishListAdapter;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.utils.activity.OmnomActivity;

import java.util.Collections;

import butterknife.InjectView;
import butterknife.OnClick;

public class WishActivity extends BaseOmnomFragmentActivity implements View.OnClickListener {

	public static int RESULT_CLEARED = 1;

	public static void start(OmnomActivity activity, UserOrder order, int code) {
		final Intent intent = new Intent(activity.getActivity(), WishActivity.class);
		intent.putExtra(EXTRA_ORDER, order);
		activity.startForResult(intent, R.anim.slide_in_up, R.anim.nothing_long, code);
	}

	@InjectView(android.R.id.list)
	protected ListView mList;

	private UserOrder mOrder;

	private WishListAdapter mAdapter;

	private boolean mClear = false;

	@OnClick(R.id.txt_close)
	public void onClose() {
		finish();
	}

	@Override
	public void finish() {
		setResult(mClear ? RESULT_CLEARED : RESULT_OK);
		super.finish();
		overridePendingTransition(R.anim.nothing, R.anim.slide_out_down);
	}

	@Override
	protected void handleIntent(final Intent intent) {
		super.handleIntent(intent);
		mOrder = intent.getParcelableExtra(EXTRA_ORDER);
	}

	@Override
	public void initUi() {
		mAdapter = new WishListAdapter(this, mOrder.getSelectedItems(), Collections.EMPTY_LIST, this);
		mList.setAdapter(mAdapter);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_wish;
	}

	@Override
	public void onClick(final View v) {
		if(v.getId() == R.id.btn_clear) {
			doClear();
		}
		if(v.getId() == R.id.btn_send) {

		}
	}

	private void doClear() {
		mClear = true;
		mOrder.itemsTable().clear();
		mAdapter = new WishListAdapter(this, mOrder.getSelectedItems(), Collections.EMPTY_LIST, this);
		mList.setAdapter(mAdapter);
	}
}
