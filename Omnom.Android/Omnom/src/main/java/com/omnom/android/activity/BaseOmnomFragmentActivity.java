package com.omnom.android.activity;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.omnom.android.OmnomApplication;
import com.omnom.util.activity.BaseFragmentActivity;

/**
 * Created by Ch3D on 01.10.2014.
 */
public abstract class BaseOmnomFragmentActivity extends BaseFragmentActivity {
	public final MixpanelAPI getMixPanel() {
		return OmnomApplication.getMixPanel(this);
	}
}
