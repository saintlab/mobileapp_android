package com.omnom.android.currency;

import android.os.Parcel;
import android.os.Parcelable;

import com.omnom.android.utils.utils.StringUtils;

/**
 * Created by Ch3D on 26.05.2015.
 */
public class Currency implements Parcelable {

	public static final Creator<Currency> CREATOR = new Creator<Currency>() {

		@Override
		public Currency createFromParcel(Parcel in) {
			return new Currency(in);
		}

		@Override
		public Currency[] newArray(int size) {
			return new Currency[size];
		}
	};

	public static final Currency RU = new Currency(100, true, "\uF5FC");

	public static final Currency NULL = new Currency(1, false, StringUtils.EMPTY_STRING);

	private final long mFactor;

	private final boolean mIsFractional;

	private final String mSymbol;

	public Currency(Parcel parcel) {
		mFactor = parcel.readLong();
		mIsFractional = parcel.readInt() == 1;
		mSymbol = parcel.readString();
	}

	private Currency(long factor, boolean isFractional, String symbol) {
		mFactor = factor;
		mIsFractional = isFractional;
		mSymbol = symbol;
	}

	public boolean isFractional() {
		return mIsFractional;
	}

	public float getFractionalFactor() {
		return mFactor;
	}

	public String getSymbol() {
		return mSymbol;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeLong(mFactor);
		dest.writeInt(mIsFractional ? 1 : 0);
		dest.writeString(mSymbol);
	}
}
