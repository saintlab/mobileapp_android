package com.omnom.android.linker.model.restaurant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

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

	@Expose
	private String id;

	@Expose
	private String authCode;

	@Expose
	private String description;

	@Expose
	private String title;

	@Expose
	private Decoration decoration;

	public Restaurant(String id, String title, String authCode, String descr, Decoration decoration) {
		this.id = id;
		this.title = title;
		this.authCode = authCode;
		this.description = descr;
		this.decoration = decoration;
	}

	public Restaurant(Parcel in) {
		id = in.readString();
		title = in.readString();
		authCode = in.readString();
		description = in.readString();
		decoration = in.readParcelable(Decoration.class.getClassLoader());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Decoration getDecoration() {
		return decoration;
	}

	public void setDecoration(Decoration decoration) {
		this.decoration = decoration;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(title);
		dest.writeString(authCode);
		dest.writeString(description);
		dest.writeParcelable(decoration, flags);
	}
}
