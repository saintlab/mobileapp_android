package com.omnom.android.restaurateur.model.restaurant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

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
	private boolean hasWaiterCall;

	@Expose
	private boolean hasPromo;

	@Expose
	private boolean hasMenu;

	@Expose
	private boolean hasTableOrder;

	@Expose
	private boolean hasBar;

	@Expose
	private boolean hasLunch;

	@Expose
	private boolean hasTakeaway;

	public Settings(Parcel parcel) {
		hasWaiterCall = toBoolean(parcel.readByte());
		hasPromo = toBoolean(parcel.readByte());
		hasMenu = toBoolean(parcel.readByte());
		hasTableOrder = toBoolean(parcel.readByte());
		hasBar = toBoolean(parcel.readByte());
		hasLunch = toBoolean(parcel.readByte());
		hasTakeaway = toBoolean(parcel.readByte());
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

	public boolean hasBar() {
		return hasBar;
	}

	public boolean hasLunch() {
		return hasLunch;
	}

	public boolean hasTakeaway() {
		return hasTakeaway;
	}

	public boolean hasTableOrder() {
		return hasTableOrder;
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
		dest.writeByte(toByte(hasTableOrder));
		dest.writeByte(toByte(hasBar));
		dest.writeByte(toByte(hasLunch));
		dest.writeByte(toByte(hasTakeaway));
	}

	private byte toByte(final boolean value) {
		return (byte) (value ? 1 : 0);
	}

	private boolean toBoolean(final byte value) {
		return value > 0;
	}

}
