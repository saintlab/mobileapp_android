package com.omnom.android.linker.activity.bind;

import android.content.Context;
import android.text.TextUtils;

import com.omnom.android.linker.R;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.Identifier;
import hugo.weaving.DebugLog;

/**
 * Created by Ch3D on 25.08.2014.
 */
public class BeaconFilter {
	private final int mMinRssi;
	private final String[] mBeaconIds;
	private Context mContext;

	public BeaconFilter(Context context) {
		mContext = context;
		mMinRssi = context.getResources().getInteger(R.integer.rssi_min_value);
		mBeaconIds = context.getResources().getStringArray(R.array.redbear_beacon_ids);
	}

	@DebugLog
	public boolean check(Beacon beacon) {
		if(beacon == null || beacon.getId1() == null) {
			return false;
		}
		final Identifier id1 = beacon.getId1();
		final String beaconId = id1.toString().toLowerCase();
		return isValidUuid(beaconId) && beacon.getRssi() >= mMinRssi;
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
}
