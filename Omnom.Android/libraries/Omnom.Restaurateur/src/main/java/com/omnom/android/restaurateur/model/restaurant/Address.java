package com.omnom.android.restaurateur.model.restaurant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 08.09.2014.
 */
public class Address implements Parcelable {
	public static final Creator<Address> CREATOR = new Creator<Address>() {
		@Override
		public Address createFromParcel(Parcel in) {
			return new Address(in);
		}

		@Override
		public Address[] newArray(int size) {
			return new Address[size];
		}
	};
	@Expose
	private String city;
	@Expose
	private String street;
	@Expose
	private String building;
	@Expose
	private String floor;

	public Address(Parcel parcel) {
		city = parcel.readString();
		street = parcel.readString();
		building = parcel.readString();
		floor = parcel.readString();
	}

	public Address(String city, String street, String building, String floor) {
		this.city = city;
		this.street = street;
		this.building = building;
		this.floor = floor;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(city);
		dest.writeString(street);
		dest.writeString(building);
		dest.writeString(floor);
	}
}
