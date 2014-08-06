package com.omnom.android.linker.service;

import java.util.UUID;

/**
 * Created by Ch3D on 01.08.2014.
 */
public class RBLBluetoothAttributes {
	public static final String REDBEAR_BEACON_LAYOUT = "m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24";

	public static final UUID UUID_BLE_REDBEAR_BEACON_SERVICE = UUID.fromString("b0702980-a295-a8ab-f734-031a98a512de");
	public static final UUID UUID_BLE_REDBEAR_BEACON_MAJOR_ID = UUID.fromString("b0702882-a295-a8ab-f734-031a98a512de");
	public static final UUID UUID_BLE_REDBEAR_BEACON_MINOR_ID = UUID.fromString("b0702883-a295-a8ab-f734-031a98a512de");

	public static final UUID UUID_BLE_REDBEAR_PASSWORD_SERVICE = UUID.fromString("81dcb1fe-31d2-d293-e311-f58390c8c39d");
	public static final UUID UUID_BLE_REDBEAR_PASSWORD = UUID.fromString("81DCB1FE-31D2-D293-E311-F583B0CBC39D");
	public static final String RBL_PASSKEY = "000000";
}
