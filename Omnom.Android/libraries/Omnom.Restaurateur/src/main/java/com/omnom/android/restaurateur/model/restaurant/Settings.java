package com.omnom.android.restaurateur.model.restaurant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mvpotter on 12/30/2014.
 */
public class Settings implements Parcelable {

	public static final Creator<Settings> CREATOR = new Creator<Settings>() {
		@Override
		public Settings createFromParcel(Parcel in) {
			return new Settings(in);
		}

		@Override
		public Settings[] newArray(int size) {
			return new Settings[size];
		}
	};

	@Expose
	@SerializedName("has_waiter_call")
	private boolean hasWaiterCall;

	@Expose
	@SerializedName("has_promo")
	private boolean hasPromo;

	@Expose
	@SerializedName("has_menu")
	private boolean hasMenu;

	public Settings(Parcel parcel) {
		hasWaiterCall = toBoolean(parcel.readByte());
		hasPromo = toBoolean(parcel.readByte());
		hasMenu = toBoolean(parcel.readByte());
	}

	public boolean hasWaiterCall() {
		return hasWaiterCall;
	}

	public boolean hasPromo() {
		return hasPromo;
	}

	public boolean hasMenu() {
		return hasMenu;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte(toByte(hasWaiterCall));
		dest.writeByte(toByte(hasPromo));
		dest.writeByte(toByte(hasMenu));
	}

	private byte toByte(final boolean value) {
		return (byte) (value ? 1 : 0);
	}

	private boolean toBoolean(final byte value) {
		return value > 0;
	}

}
