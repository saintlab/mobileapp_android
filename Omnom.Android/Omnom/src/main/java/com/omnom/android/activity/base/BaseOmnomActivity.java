package com.omnom.android.activity.base;

import android.location.Location;
import android.os.Bundle;

import com.omnom.android.OmnomApplication;
import com.omnom.android.activity.helper.LocationActivityHelper;
import com.omnom.android.activity.helper.OmnomActivityHelper;
import com.omnom.android.auth.UserData;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.mixpanel.model.MixpanelEvent;
import com.omnom.android.restaurateur.model.bill.BillResponse;
import com.omnom.android.utils.UserHelper;
import com.omnom.android.utils.activity.BaseActivity;

/**
 * Created by Ch3D on 01.10.2014.
 */
public abstract class BaseOmnomActivity extends BaseActivity {

	protected LocationActivityHelper locationHelper;

	private boolean isBusy;

	public final MixPanelHelper getMixPanelHelper() {
		return OmnomApplication.getMixPanelHelper(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		locationHelper = new OmnomActivityHelper(getActivity());
	}

	protected final OmnomApplication getApp() {
		return OmnomApplication.get(getActivity());
	}

	@Override
	protected void onStart() {
		super.onStart();
		locationHelper.onStart();
	}

	protected final void track(final MixPanelHelper.Project project, final MixpanelEvent event) {
		getMixPanelHelper().track(project, event);
	}

	public void trackRevenue(final MixPanelHelper.Project project, final String userId,
	                         final OrderFragment.PaymentDetails details,
	                         final BillResponse billData) {

		getMixPanelHelper().trackRevenue(project, userId, details, billData);
	}

	@Override
	protected void onDestroy() {
		locationHelper.onDestroy();
		getMixPanelHelper().flush();
		super.onDestroy();
	}

	protected UserData getUserData() {
		return UserHelper.getUserData(this);
	}

	protected boolean isBusy() {
		return isBusy;
	}

	protected void busy(final boolean isBusy) {
		this.isBusy = isBusy;
	}

	protected Location getLocation() {
		return locationHelper.getLocation();
	}

}
