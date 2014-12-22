package com.omnom.android.mixpanel.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 22.12.2014.
 */
public abstract class DecodeMixpanelEvent implements MixpanelEvent {
	public static final String METHODE_BLUETOOTH = "bluetooth";

	public static final String METHODE_QR = "qr";

	@Expose
	protected final String restaurantName;

	@Expose
	protected final String tableId;

	@Expose
	protected final String methode;

	public DecodeMixpanelEvent(String tableId, String restaurantName, String methode) {
		this.tableId = tableId;
		this.methode = methode;
		this.restaurantName = restaurantName;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public String getTableId() {
		return tableId;
	}

	public String getMethode() {
		return methode;
	}
}
