package com.omnom.android.linker.model.table;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.omnom.android.linker.model.ResponseBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ch3D on 26.08.2014.
 */
public class TableDataResponse extends ResponseBase {
	public static final TableDataResponse NULL = new TableDataResponse("null_id", 0, "null_qr_data", "null_rest_id");

	@Expose
	private int internalId;

	@Expose
	private String qrCode;

	@Expose
	private String restaurantId;

	@Expose
	private String id;
	@Expose
	private List<String> beaconUuids = new ArrayList<String>();

	public TableDataResponse(String id, int internalId, String qrCode, String restaurantId) {
		this.internalId = internalId;
		this.qrCode = qrCode;
		this.restaurantId = restaurantId;
		this.id = id;
	}

	@Override
	public String toString() {
		return new GsonBuilder().create().toJson(this);
	}

	public int getInternalId() {
		return internalId;
	}

	public String getQrCode() {
		return qrCode;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public String getId() {
		return id;
	}

	public List<String> getBeaconUuids() {
		return Collections.unmodifiableList(beaconUuids);
	}

	public void setBeaconUuids(List<String> beaconUuids) {
		this.beaconUuids = beaconUuids;
	}
}