package com.omnom.android.mixpanel.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.omnom.android.auth.UserData;
import com.omnom.android.restaurateur.model.beacon.BeaconFindRequest;
import com.omnom.android.utils.utils.AmountHelper;

/**
 * Created by mvpotter on 21/11/14.
 */
public final class BillViewEvent implements Event {

	private static String BEACON_FORMAT = "%s+%s+%s";

	@Expose
	@SerializedName("restaurant_id")
	private String restaurantId;
	@Expose
	private String beacon;
	@Expose
	@SerializedName("omn_user")
	private UserData user;
	@Expose
	private int amount;

	public BillViewEvent(String restaurantId, BeaconFindRequest beacon,
	                     UserData user, double amount) {
		String beaconStr = beacon == null ? null :
											String.format(BEACON_FORMAT, beacon.getUuid(),
																		 beacon.getMajor(),
																		 beacon.getMinor());
		this.restaurantId = restaurantId;
		this.beacon = beaconStr;
		this.user = user;
		this.amount = AmountHelper.toInt(amount);
	}

	@Override
	public String getName() {
		return "BILL_VIEW";
	}

	public int getAmount() {
		return amount;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public String getBeacon() {
		return beacon;
	}

	public UserData getUser() {
		return user;
	}

}
