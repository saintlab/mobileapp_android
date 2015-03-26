package com.omnom.android.activity.base;

import android.content.Intent;

import com.omnom.android.activity.holder.EntranceData;

public abstract class BaseOmnomModeSupportActivity extends BaseOmnomFragmentActivity {

	protected EntranceData entranceData;

	@Override
	protected void handleIntent(Intent intent) {
		super.handleIntent(intent);
		entranceData = intent.getParcelableExtra(EXTRA_ENTRANCE_DATA);
	}

}
