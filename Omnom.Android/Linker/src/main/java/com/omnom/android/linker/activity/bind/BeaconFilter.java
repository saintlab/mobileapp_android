package com.omnom.android.linker.activity.bind;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.omnom.android.linker.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		if (beacon == null || beacon.getId1() == null) {
			return false;
		}
		final Identifier id1 = beacon.getId1();
		final String beaconId = id1.toString().toLowerCase();
		return isValidUuid(beaconId)/* && beacon.getRssi() >= mMinRssi*/;
	}

	private boolean isValidUuid(final String beaconId) {
		if (TextUtils.isEmpty(beaconId)) {
			return false;
		}
		final String bid = beaconId.toLowerCase();
		for (final String item : mBeaconIds) {
			if (item.equals(bid)) {
				return true;
			}
		}
		return false;
	}

	public class Link {
		public BData p1;
		public BData p2;

		public Link(BData p1, BData p2) {
			this.p1 = p1;
			this.p2 = p2;
		}

		@Override
		public String toString() {
			return p1.minor + "->" + p2.minor + "=" + getLength();
		}

		public int getLength() {
			return Math.abs(p1.avgRssi - p2.avgRssi);
		}
	}

	public class BData {
		public int minor;
		public int avgRssi;
		public List<Integer> values;
		public Link link = null;

		@Override
		public String toString() {
			return "minor: " + minor + " avg: " + avgRssi + " values: " + Arrays.toString(values.toArray());
		}
	}

	public int getAvgRssi(List<Integer> data) {
		int result = 0;
		for (int i : data) {
			result += i;
		}
		return result / data.size();
	}

	public List<BData> findNearestBeacons(ArrayList<Beacon> mBeacons) {
		if (mBeacons.size() < 1) {
			return Collections.emptyList();
		}
		final HashMap<Integer, List<Integer>> minor2rssi = new HashMap<Integer, List<Integer>>();
		for (Beacon b : mBeacons) {
			int minor = Integer.parseInt(b.getIdValue(2));
			List<Integer> rssiList = minor2rssi.get(minor);
			if (rssiList == null) {
				rssiList = new ArrayList<Integer>();
				minor2rssi.put(minor, rssiList);
			}
			rssiList.add(b.getRssi());
		}
		final ArrayList<BData> datas = new ArrayList<BData>();
		final ArrayList<BData> result = new ArrayList<BData>();
		for (final Map.Entry<Integer, List<Integer>> entry : minor2rssi.entrySet()) {
			BData d = new BData();
			d.minor = entry.getKey();
			d.avgRssi = getAvgRssi(entry.getValue());
			d.values = entry.getValue();
			datas.add(d);
		}
		Collections.sort(datas, new Comparator<BData>() {
			@Override
			public int compare(BData lhs, BData rhs) {
				return lhs.avgRssi > rhs.avgRssi ? -1 : 1;
			}
		});
		for (int i = 0; i < datas.size() - 1; i++) {
			datas.get(i).link = new Link(datas.get(i), datas.get(i + 1));
		}
		for (int i = 0; i < datas.size(); i++) {
			Link link = datas.get(i).link;
			if (link != null) {
				Log.d("BEACONS", link.toString());
			}
		}
		Link maxLink = null;
		for (int i = 0; i < datas.size() - 1; i++) {
			int length1 = datas.get(i).link.getLength();
			if (maxLink == null || maxLink.getLength() < length1) {
				maxLink = datas.get(i).link;
			}
		}
		maxLink.p1.link = null;
		Log.d("BEACONS", "============remove max=========");
		for (int i = 0; i < datas.size(); i++) {
			Link link = datas.get(i).link;
			if (link != null) {
				Log.d("BEACONS", link.toString());
			}
		}
		Log.d("BEACONS", Arrays.toString(datas.toArray()));
		int i = 0;
		while(true) {
			BData element = datas.get(i);
			result.add(element);
			if(element.link != null) {
				i++;
			} else {
				break;
			}
		}
		return result;
	}
}
