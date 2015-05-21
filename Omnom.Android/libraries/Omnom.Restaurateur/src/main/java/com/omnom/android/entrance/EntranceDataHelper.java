package com.omnom.android.entrance;

/**
 * Created by Ch3D on 21.05.2015.
 */
public class EntranceDataHelper {
	public static boolean isBar(EntranceData entranceData) {
		return entranceData != null && EntranceData.TYPE_BAR == entranceData.getType();
	}

	public static boolean isTakeAway(EntranceData entranceData) {
		return entranceData != null && EntranceData.TYPE_TAKEAWAY == entranceData.getType();
	}
}
