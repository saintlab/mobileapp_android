package com.omnom.android.linker.model.restaurant;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.omnom.android.linker.R;
import com.omnom.android.linker.utils.StringUtils;

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

	@Expose
	private Address address;

	public Restaurant(String id, String title, String authCode, String descr, Decoration decoration, Address address) {
		this.id = id;
		this.title = title;
		this.authCode = authCode;
		this.description = descr;
		this.decoration = decoration;
		this.address = address;
	}

	public Restaurant(Parcel in) {
		id = in.readString();
		title = in.readString();
		authCode = in.readString();
		description = in.readString();
		decoration = in.readParcelable(Decoration.class.getClassLoader());
		address = in.readParcelable(Address.class.getClassLoader());
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
		dest.writeParcelable(address, flags);
	}

	public String getAddress(final Context context) {
		final Address address = getAddress();
		if(address != null) {
			final String floor = !TextUtils.isEmpty(address.getFloor())
					? address.getFloor() + context.getString(R.string.floor_suffix) : StringUtils.EMPTY_STRING;
			return StringUtils.concat(context.getString(R.string.restaurant_address_delimiter),
			                          address.getCity(),
			                          address.getStreet(),
			                          address.getBuilding(),
			                          floor);
		}
		return StringUtils.EMPTY_STRING;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
}
