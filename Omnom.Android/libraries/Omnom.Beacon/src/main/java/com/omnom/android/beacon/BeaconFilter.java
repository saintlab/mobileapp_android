package com.omnom.android.beacon;

import android.text.TextUtils;

import com.omnom.android.utils.BaseOmnomApplication;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.Identifier;
import hugo.weaving.DebugLog;

/**
 * Created by Ch3D on 25.08.2014.
 */
public class BeaconFilter {
	private final String[] mBeaconIds;

	@Inject
	protected BeaconFilterAlgorithm mFilterAlgorithm;

	public BeaconFilter(BaseOmnomApplication app) {
		mBeaconIds = app.getResources().getStringArray(R.array.redbear_beacon_ids);
		app.inject(this);
	}

	@DebugLog
	public boolean check(Beacon beacon) {
		if(beacon == null || beacon.getId1() == null) {
			return false;
		}
		final Identifier id1 = beacon.getId1();
		final String beaconId = id1.toString().toLowerCase();
		return isValidUuid(beaconId);
	}

	private boolean isValidUuid(final String beaconId) {
		if(TextUtils.isEmpty(beaconId)) {
			return false;
		}
		final String bid = beaconId.toLowerCase();
		for(final String item : mBeaconIds) {
			if(item.equals(bid)) {
				return true;
			}
		}
		return false;
	}

	@DebugLog
	public List<Beacon> filterBeacons(ArrayList<Beacon> mBeacons) {
		return mFilterAlgorithm.filter(mBeacons);
	}
}
