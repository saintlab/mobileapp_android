package com.omnom.android.activity;

import android.widget.ListView;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;

import butterknife.InjectView;

public class WishActivity extends BaseOmnomFragmentActivity {

	@InjectView(android.R.id.list)
	protected ListView mList;

	@Override
	public void initUi() {
	
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_wish;
	}
}
