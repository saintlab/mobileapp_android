package com.omnom.android.linker.beacon;

import java.util.List;

import altbeacon.beacon.Beacon;

/**
 * Created by xCh3Dx on 17.09.2014.
 */
public interface BeaconFilterAlgorithm {
	public List<Beacon> filter(List<Beacon> beacons);
}
