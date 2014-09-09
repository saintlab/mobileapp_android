package com.omnom.android.linker.service;

import java.util.UUID;

/**
 * Created by Ch3D on 01.08.2014.
 */
public class BeaconAttributes {
	public static final String REDBEAR_BEACON_LAYOUT = "m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24";

	public static final UUID UUID_BLE_REDBEAR_BEACON_SERVICE = UUID.fromString("b0702980-a295-a8ab-f734-031a98a512de");
	public static final UUID UUID_BLE_REDBEAR_BEACON_MAJOR_ID = UUID.fromString("b0702882-a295-a8ab-f734-031a98a512de");
	public static final UUID UUID_BLE_REDBEAR_BEACON_MINOR_ID = UUID.fromString("b0702883-a295-a8ab-f734-031a98a512de");
	public static final UUID UUID_BLE_REDBEAR_BEACON_SIGNAL_MEASURED = UUID.fromString("b0702884-a295-a8ab-f734-031a98a512de");
	public static final UUID UUID_BLE_REDBEAR_BEACON_SIGNAL_TX = UUID.fromString("b0702887-a295-a8ab-f734-031a98a512de");

	public static final UUID UUID_BLE_REDBEAR_PASSWORD_SERVICE = UUID.fromString("81dcb1fe-31d2-d293-e311-f58390c8c39d");
	public static final UUID UUID_BLE_REDBEAR_PASSWORD = UUID.fromString("81dcb1fe-31d2-d293-e311-f583b0cbc39d");
	public static final String BEACON_ID = "e2c56db5-dffb-48d2-b060-d0f5a71096e0";
	public static final String BEACON_ID_NEW = "f93c1af8-ffb2-488a-a952-a250db61dec4";

	public static final String RBL_DEFAULT_PASSKEY = "000000";
	public static final byte[] RBL_DEFAULT_TX = new byte[]{2};
}
