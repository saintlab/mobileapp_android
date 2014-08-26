package com.omnom.android.linker.model.ibeacon;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 25.08.2014.
 */
public class BeaconBuildRequest {
	@Expose
	public String uuid;

	@Expose
	public String tableNum;

	@Expose
	public String restaurantId;

	public BeaconBuildRequest(String uuid, String tableNumber, String restaurantId) {
		this.uuid = uuid;
		this.tableNum = tableNumber;
		this.restaurantId = restaurantId;
	}
}
