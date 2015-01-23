package com.omnom.android.mixpanel.model;

import com.google.gson.annotations.Expose;
import com.omnom.android.auth.UserData;

/**
 * Created by Ch3D on 22.12.2014.
 */
abstract class DecodeMixpanelEvent extends AbstractBaseMixpanelEvent {
	public static final String METHOD_BLUETOOTH = "bluetooth";

	public static final String METHOD_QR = "qr";

	public static final String METHOD_HASH = "hash";

	@Expose
	protected final String requestId;

	@Expose
	protected final String restaurantId;

	@Expose
	protected final String restaurantName;

	@Expose
	protected final String tableId;

	@Expose
	protected final String method;

	public DecodeMixpanelEvent(String requestId, UserData userData, String tableId, String restaurantId, String method) {
		this(requestId, userData, tableId, restaurantId, null, method);
	}

	public DecodeMixpanelEvent(String requestId, UserData userData, String tableId, String restaurantId, String restaurantName, String method) {
		super(userData);
		this.requestId = requestId;
		this.tableId = tableId;
		this.method = method;
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
		return method;
	}
}
