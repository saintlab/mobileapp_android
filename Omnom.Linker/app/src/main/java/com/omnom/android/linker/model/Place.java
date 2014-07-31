package com.omnom.android.linker.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ch3D on 31.07.2014.
 */
public class Place implements Parcelable {

	public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
		@Override
		public Place createFromParcel(Parcel in) {
			return new Place(in);
		}

		@Override
		public Place[] newArray(int size) {
			return new Place[size];
		}
	};

	private String id;
	private String name;
	private String location;
	private int rating;

	public Place(Parcel parcel) {
		this.id = parcel.readString();
		this.name = parcel.readString();
		this.location = parcel.readString();
		this.rating = parcel.readInt();
	}

	protected Place(String id, String name, String info, int rating) {
		this.id = id;
		this.name = name;
		this.location = info;
		this.rating = rating;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
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
		dest.writeString(location);
		dest.writeInt(rating);
	}
}
