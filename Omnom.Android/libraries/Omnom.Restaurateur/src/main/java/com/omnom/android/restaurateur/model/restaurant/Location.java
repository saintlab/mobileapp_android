package com.omnom.android.restaurateur.model.restaurant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by mvpotter on 19.01.2014.
 */
public class Location implements Parcelable {
	public static final Creator<Location> CREATOR = new Creator<Location>() {
		@Override
		public Location createFromParcel(Parcel in) {
			return new Location(in);
		}

		@Override
		public Location[] newArray(int size) {
			return new Location[size];
		}
	};

	@Expose
	private double longitude;
	@Expose
	private double latitude;

	public Location(Parcel parcel) {
		longitude = parcel.readDouble();
		latitude = parcel.readDouble();
	}

	public static Creator<Location> getCreator() {
		return CREATOR;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(longitude);
		dest.writeDouble(latitude);
	}
}
