package com.omnom.android.mixpanel.model;

import com.google.gson.annotations.Expose;
import com.omnom.android.auth.UserData;

/**
 * Created by Ch3D on 22.12.2014.
 */
abstract class DecodeMixpanelEvent extends AbstractBaseMixpanelEvent {
	public static final String METHOD_BLUETOOTH = "Bluetooth";

	public static final String METHOD_QR = "QR";

	public static final String METHOD_HASH = "Hash";

	@Expose
	protected final String requestId;

	@Expose
	protected final String restaurantId;

	@Expose
	protected final String restaurantName;

	@Expose
	protected final String tableId;

	@Expose
	protected final String methodUsed;

	public DecodeMixpanelEvent(String requestId, UserData userData, String tableId, String restaurantId, String methodUsed) {
		this(requestId, userData, tableId, restaurantId, null, methodUsed);
	}

	public DecodeMixpanelEvent(String requestId, UserData userData, String tableId, String restaurantId, String restaurantName, String methodUsed) {
		super(userData);
		this.requestId = requestId;
		this.tableId = tableId;
		this.methodUsed = methodUsed;
		this.restaurantId = restaurantId;
		this.restaurantName = restaurantName;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public String getTableId() {
		return tableId;
	}

	public String getMethod() {
		return methodUsed;
	}
}
