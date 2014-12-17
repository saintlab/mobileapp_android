package com.omnom.android.restaurateur.model.order;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 02.12.2014.
 */
public class User implements Parcelable {
	public static final Creator<User> CREATOR = new Creator<User>() {

		@Override
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};

	@Expose
	private int id;

	@Expose
	private String photoUrl;

	@Expose
	private String name;

	public User(Parcel parcel) {
		id = parcel.readInt();
		photoUrl = parcel.readString();
		name = parcel.readString();
	}

	public User() {
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(final String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeInt(id);
		dest.writeString(photoUrl);
		dest.writeString(name);
	}
}
