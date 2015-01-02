package com.omnom.android.restaurateur.model.table;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.omnom.android.restaurateur.model.ResponseBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ch3D on 26.08.2014.
 */
public class TableDataResponse extends ResponseBase implements Parcelable {
	public static final TableDataResponse NULL = new TableDataResponse("null_id", 0, Collections.EMPTY_LIST, "null_rest_id");

	public static final Creator<TableDataResponse> CREATOR = new Creator<TableDataResponse>() {

		@Override
		public TableDataResponse createFromParcel(Parcel in) {
			return new TableDataResponse(in);
		}

		@Override
		public TableDataResponse[] newArray(int size) {
			return new TableDataResponse[size];
		}
	};

	@Expose
	private int internalId;

	@Expose
	private List<String> qrCode = new ArrayList<String>();

	@Expose
	private String restaurantId;

	@Expose
	private String id;

	@Expose
	private int major;

	@Expose
	private int minor;

	@Expose
	private List<String> beaconUuids = new ArrayList<String>();

	public TableDataResponse(String id, int internalId, List<String> qrCode, String restaurantId) {
		this.internalId = internalId;
		this.qrCode = qrCode;
		this.restaurantId = restaurantId;
		this.id = id;
	}

	public TableDataResponse(Parcel parcel) {
		internalId = parcel.readInt();
		parcel.readStringList(qrCode);
		restaurantId = parcel.readString();
		id = parcel.readString();
		major = parcel.readInt();
		minor = parcel.readInt();
		parcel.readStringList(beaconUuids);
	}

	@Override
	public String toString() {
		return new GsonBuilder().create().toJson(this);
	}

	public int getInternalId() {
		return internalId;
	}

	public List<String> getQrCode() {
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

	public int getMajor() {
		return major;
	}

	public void setMajor(final int major) {
		this.major = major;
	}

	public int getMinor() {
		return minor;
	}

	public void setMinor(final int minor) {
		this.minor = minor;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeInt(internalId);
		dest.writeStringList(qrCode);
		dest.writeString(restaurantId);
		dest.writeString(id);
		dest.writeInt(major);
		dest.writeInt(minor);
		dest.writeStringList(beaconUuids);
	}
}
