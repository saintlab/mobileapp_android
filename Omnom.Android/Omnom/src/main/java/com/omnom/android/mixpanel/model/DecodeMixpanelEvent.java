package com.omnom.android.mixpanel.model;

import com.google.gson.annotations.Expose;
import com.omnom.android.auth.UserData;

/**
 * Created by Ch3D on 22.12.2014.
 */
public abstract class DecodeMixpanelEvent extends BaseMixpanelEvent {
	public static final String METHODE_BLUETOOTH = "bluetooth";

	public static final String METHODE_QR = "qr";

	@Expose
	protected final String restaurantName;

	@Expose
	protected final String tableId;

	@Expose
	protected final String method;

	public DecodeMixpanelEvent(UserData userData, String tableId, String restaurantName, String method) {
		super(userData);
		this.tableId = tableId;
		this.method = method;
		this.restaurantName = restaurantName;
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
