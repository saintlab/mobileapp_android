package com.omnom.android.activity.base;

import android.content.Intent;

import com.omnom.android.entrance.EntranceData;

public abstract class BaseOmnomModeSupportActivity extends BaseOmnomFragmentActivity {

	protected EntranceData mEntranceData;

	@Override
	protected void handleIntent(Intent intent) {
		super.handleIntent(intent);
		mEntranceData = intent.getParcelableExtra(EXTRA_ENTRANCE_DATA);
	}

}
