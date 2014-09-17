package com.omnom.android.linker.beacon;

import java.util.List;

import hugo.weaving.DebugLog;

/**
 * Created by xCh3Dx on 17.09.2014.
 */
public class BeaconUtils {

	public static int getMaxRssi(List<Integer> rssiList) {
		int result = Integer.MIN_VALUE;
		for (int i : rssiList) {
			if (i > result) {
				result = i;
			}
		}
		return result;
	}

	public static int getMinRssi(List<Integer> rssiList) {
		int result = Integer.MAX_VALUE;
		for (int i : rssiList) {
			if (i < result) {
				result = i;
			}
		}
		return result;
	}

	@DebugLog
	public static int getAvgRssi(List<Integer> rssiList) {
		int result = 0;
		for (int i : rssiList) {
			result += i;
		}
		return result / rssiList.size();
	}


}
