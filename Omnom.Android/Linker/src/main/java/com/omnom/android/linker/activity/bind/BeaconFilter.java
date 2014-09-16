package com.omnom.android.linker.activity.bind;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.omnom.android.linker.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.Identifier;
import hugo.weaving.DebugLog;

/**
 * Created by Ch3D on 25.08.2014.
 */
public class BeaconFilter {

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
		private Beacon mBeacon;

		public BData(Beacon beacon) {
			mBeacon = beacon;
		}

		@Override
		public String toString() {
			return "minor: " + minor + " avg: " + avgRssi + " values: " + Arrays.toString(values.toArray());
		}

		public Beacon getBeacon() {
			return mBeacon;
		}
	}

	public static int getMaxRssi(List<Integer> rssiList) {
		int result = Integer.MIN_VALUE;
		for(int i : rssiList) {
			if(i > result) {
				result = i;
			}
		}
		return result;
	}

	public static int getMinRssi(List<Integer> rssiList) {
		int result = Integer.MAX_VALUE;
		for(int i : rssiList) {
			if(i < result) {
				result = i;
			}
		}
		return result;
	}

	public static int getAvgRssi(List<Integer> rssiList) {
		int result = 0;
		for(int i : rssiList) {
			result += i;
		}
		return result / rssiList.size();
	}

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
		return isValidUuid(beaconId)/* && beacon.getRssi() >= mMinRssi*/;
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

	private final Comparator<BData> mBDataComparator = new Comparator<BData>() {
		@Override
		public int compare(BData lhs, BData rhs) {
			return lhs.avgRssi > rhs.avgRssi ? -1 : 1;
		}
	};

	public List<Beacon> findNearestBeacons(ArrayList<Beacon> mBeacons) {
		if(mBeacons.size() < 1) {
			return Collections.emptyList();
		}
		final SparseArray<List<Integer>> minor2rssi = new SparseArray<List<Integer>>();
		final SparseArray<Beacon> minor2beacon = new SparseArray<Beacon>();

		// associate minor -> rssi[] and minor -> beacon
		for(final Beacon b : mBeacons) {
			int minor = Integer.parseInt(b.getIdValue(2));
			List<Integer> rssiList = minor2rssi.get(minor);
			if(rssiList == null) {
				rssiList = new ArrayList<Integer>();
				minor2rssi.put(minor, rssiList);
			}
			rssiList.add(b.getRssi());
			minor2beacon.put(minor, b);
		}

		final ArrayList<BData> datas = new ArrayList<BData>();
		for(int i = 0; i < minor2rssi.size(); i++) {
			final List<Integer> rssiValues = minor2rssi.get(i);
			if(rssiValues != null) {
				final Beacon beacon = minor2beacon.get(i);
				final BData d = new BData(beacon);
				d.minor = i;
				d.avgRssi = getAvgRssi(clearFluctuation(rssiValues));
				d.values = rssiValues;
				datas.add(d);
			}
		}

		Collections.sort(datas, mBDataComparator);
		for(int i = 0; i < datas.size() - 1; i++) {
			datas.get(i).link = new Link(datas.get(i), datas.get(i + 1));
		}
		logLinks(datas);
		// remove max link
		getMaxLink(datas).p1.link = null;
		Log.d("BEACONS", "============remove max=========");
		logLinks(datas);
		Log.d("BEACONS", Arrays.toString(datas.toArray()));
		return getLinkedElements(datas);
	}

	@DebugLog
	public List<Integer> clearFluctuation(List<Integer> rssiValues) {
		if(rssiValues.size() < 4) {
			return rssiValues;
		}
		final Integer minRssi = getMinRssi(rssiValues);
		final Integer maxRssi = getMaxRssi(rssiValues);
		if(!maxRssi.equals(minRssi)) {
			rssiValues.remove(minRssi);
			rssiValues.remove(maxRssi);
		}
		return rssiValues;
	}

	private ArrayList<Beacon> getLinkedElements(ArrayList<BData> datas) {
		final ArrayList<Beacon> result = new ArrayList<Beacon>();
		BData current = datas.get(0);
		while(current.link != null) {
			if(current.avgRssi >= mMinRssi) {
				final Beacon beacon = current.getBeacon();
				result.add(beacon);
			}
			current = current.link.p2;
		}
		return result;
	}

	private void logLinks(ArrayList<BData> datas) {
		for(int i = 0; i < datas.size(); i++) {
			Link link = datas.get(i).link;
			if(link != null) {
				Log.d("BEACONS", link.toString());
			}
		}
	}

	private Link getMaxLink(ArrayList<BData> datas) {
		Link maxLink = null;
		for(int i = 0; i < datas.size() - 1; i++) {
			int length1 = datas.get(i).link.getLength();
			if(maxLink == null || maxLink.getLength() < length1) {
				maxLink = datas.get(i).link;
			}
		}
		return maxLink;
	}

}
