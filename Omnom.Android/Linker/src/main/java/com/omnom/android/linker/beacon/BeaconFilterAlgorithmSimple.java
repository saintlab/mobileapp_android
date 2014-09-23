package com.omnom.android.linker.beacon;

import android.content.Context;
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
import hugo.weaving.DebugLog;

/**
 * Created by xCh3Dx on 17.09.2014.
 */
public class BeaconFilterAlgorithmSimple implements BeaconFilterAlgorithm {

	private final int mMinRssi;

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


	public BeaconFilterAlgorithmSimple(Context context) {
		mMinRssi = context.getResources().getInteger(R.integer.rssi_min_value);
	}

	@Override
	public List<Beacon> filter(List<Beacon> beacons) {
		if (beacons.size() < 1) {
			return Collections.emptyList();
		}
		final HashMap<Integer, List<Integer>> minor2rssi = new HashMap<Integer, List<Integer>>();
		final HashMap<Integer, Beacon> minor2beacon = new HashMap<Integer, Beacon>();

		// associate minor -> rssi[] and minor -> beacon
		for (final Beacon b : beacons) {
			int minor = Integer.parseInt(b.getIdValue(2));
			List<Integer> rssiList = minor2rssi.get(minor);
			if (rssiList == null) {
				rssiList = new ArrayList<Integer>();
				minor2rssi.put(minor, rssiList);
			}
			rssiList.add(b.getRssi());
			minor2beacon.put(minor, b);
		}

		final ArrayList<BData> datas = new ArrayList<BData>();
		for (final Map.Entry<Integer, List<Integer>> entry : minor2rssi.entrySet()) {
			final List<Integer> rssiValues = entry.getValue();
			if (rssiValues != null) {
				final Integer key = entry.getKey();
				final Beacon beacon = minor2beacon.get(key);
				final BData d = new BData(beacon);
				d.minor = key;
				d.avgRssi = BeaconUtils.getAvgRssi(clearFluctuation(rssiValues));
				d.values = rssiValues;
				datas.add(d);
			}
		}

		Collections.sort(datas, mBDataComparator);
		for (int i = 0; i < datas.size() - 1; i++) {
			datas.get(i).link = new Link(datas.get(i), datas.get(i + 1));
		}
		logLinks(datas);
		// remove max link
		final Link maxLink = getMaxLink(datas);
		if(maxLink != null && maxLink.p1 != null) {
			maxLink.p1.link = null;
		}
		Log.d("BEACONS", "============remove max=========");
		logLinks(datas);
		Log.d("BEACONS", Arrays.toString(datas.toArray()));
		return getLinkedElements(datas);
	}

	public List<Integer> clearFluctuation(List<Integer> rssiValues) {
		if (rssiValues.size() < 4) {
			return rssiValues;
		}
		final Integer minRssi = BeaconUtils.getMinRssi(rssiValues);
		final Integer maxRssi = BeaconUtils.getMaxRssi(rssiValues);
		if (!maxRssi.equals(minRssi)) {
			rssiValues.remove(minRssi);
			rssiValues.remove(maxRssi);
		}
		return rssiValues;
	}

	@DebugLog
	private ArrayList<Beacon> getLinkedElements(ArrayList<BData> datas) {
		final ArrayList<Beacon> result = new ArrayList<Beacon>();
		BData current = datas.get(0);
		if (current.avgRssi >= mMinRssi) {
			result.add(current.getBeacon());
		}
		while (current.link != null) {
			if (current.avgRssi >= mMinRssi) {
				result.add(current.getBeacon());
			}
			current = current.link.p2;
		}
		return result;
	}

	private void logLinks(ArrayList<BData> datas) {
		for (int i = 0; i < datas.size(); i++) {
			Link link = datas.get(i).link;
			if (link != null) {
				Log.d("BEACONS", link.toString());
			}
		}
	}

	private final Comparator<BData> mBDataComparator = new Comparator<BData>() {
		@Override
		public int compare(BData lhs, BData rhs) {
			return lhs.avgRssi > rhs.avgRssi ? -1 : 1;
		}
	};


	@DebugLog
	private Link getMaxLink(ArrayList<BData> datas) {
		Link maxLink = null;
		for (int i = 0; i < datas.size() - 1; i++) {
			int length1 = datas.get(i).link.getLength();
			if (maxLink == null || maxLink.getLength() < length1) {
				maxLink = datas.get(i).link;
			}
		}
		return maxLink;
	}

}
