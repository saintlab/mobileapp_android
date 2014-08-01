package com.omnom.android.linker.service;

import java.util.HashMap;

/**
 * Created by Ch3D on 01.08.2014.
 */
public class BluetoothGattAttributes {

	private static HashMap<String, String> attributes = new HashMap<String, String>();

	public static String lookup(String uuid, String defaultName) {
		String name = attributes.get(uuid);
		return name == null ? defaultName : name;
	}

	public static String BLE_REDBEAR_BEACON_SERVICE = "b0702980-a295-a8ab-f734-031a98a512de";
	public static String BLE_REDBEAR_BEACON_UUID = "B0702881-A295-A8AB-F734-031A98A512DE";
	public static String BLE_REDBEAR_BEACON_MAJOR_ID = "B0702882-A295-A8AB-F734-031A98A512DE";
	public static String BLE_REDBEAR_BEACON_MINOR_ID = "B0702883-A295-A8AB-F734-031A98A512DE";

	public static String BLE_REDBEAR_DEVICE_SERVICE = "180A";
	public static String BLE_REDBEAR_DEVICE_SOFTWARE_REVISION = "2A28";

	public static String BLE_REDBEAR_DEVICE_NAME = "38837046-FE96-4335-B751-D4826198F337";

	public static String BLE_REDBEAR_BATTERY_SERVICE = "180F";

	public static String BLE_REDBEAR_FIRMWARE_UPDATE_SERVICE = "E6775403-F0DD-40C4-87DB-95E755738AD1";

	static {
		// RBL Services.
		attributes.put(BLE_REDBEAR_BEACON_SERVICE, "BLE RedBear Beacon Service");
		attributes.put(BLE_REDBEAR_BEACON_UUID, "BLE RedBear Beacon Service");
		attributes.put(BLE_REDBEAR_BEACON_MINOR_ID, "BLE RedBear Beacon Service");
		attributes.put(BLE_REDBEAR_BEACON_MAJOR_ID, "BLE RedBear Beacon Service");
		// RBL Characteristics.
		attributes.put(BLE_REDBEAR_DEVICE_SERVICE, "BLE RedBear Device Service");
		attributes.put(BLE_REDBEAR_DEVICE_SOFTWARE_REVISION, "BLE RedBear Software Revision");
		attributes.put(BLE_REDBEAR_DEVICE_NAME, "BLE RedBear Device Name");
		attributes.put(BLE_REDBEAR_BATTERY_SERVICE, "BLE RedBear Battery Service");
		attributes.put(BLE_REDBEAR_FIRMWARE_UPDATE_SERVICE, "BLE RedBear Update Service");
	}
}
