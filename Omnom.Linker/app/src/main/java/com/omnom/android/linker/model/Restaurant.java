package com.omnom.android.linker.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ch3D on 31.07.2014.
 */
public class Restaurant implements Parcelable {

	public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>() {
		@Override
		public Restaurant createFromParcel(Parcel in) {
			return new Restaurant(in);
		}

		@Override
		public Restaurant[] newArray(int size) {
			return new Restaurant[size];
		}
	};

	private String   id;
	private String   name;
	private Location location;
	private String   locationName;
	private int      rating;

	public Restaurant(Parcel parcel) {
		this.id = parcel.readString();
		this.name = parcel.readString();
		this.location = parcel.readParcelable(Location.class.getClassLoader());
		this.locationName = parcel.readString();
		this.rating = parcel.readInt();
	}

	protected Restaurant(String id, String name, Location location, String locationName, int rating) {
		this.id = id;
		this.name = name;
		this.location = location;
		this.locationName = locationName;
		this.rating = rating;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
		dest.writeParcelable(location, flags);
		dest.writeString(locationName);
		dest.writeInt(rating);
	}
}
