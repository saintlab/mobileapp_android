package com.omnom.android.currency;

import android.os.Parcel;
import android.os.Parcelable;

import com.omnom.android.utils.utils.StringUtils;

import java.math.BigDecimal;

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

	public static final Currency US = new Currency(100, true, "\uFE69");

	public static final Currency NULL = new Currency(1, false, StringUtils.EMPTY_STRING);

	private final long mFactor;

	private final BigDecimal mDecimalFactor;

	private final boolean mIsFractional;

	private final String mSymbol;

	public Currency(Parcel parcel) {
		mFactor = parcel.readLong();
		mIsFractional = parcel.readInt() == 1;
		mSymbol = parcel.readString();
		mDecimalFactor = BigDecimal.valueOf(mFactor);
	}

	private Currency(long factor, boolean isFractional, String symbol) {
		mFactor = factor;
		mIsFractional = isFractional;
		mSymbol = symbol;
		mDecimalFactor = BigDecimal.valueOf(factor);
	}

	@Override
	public boolean equals(final Object o) {
		if(this == o) {
			return true;
		}
		if(o == null || getClass() != o.getClass()) {
			return false;
		}

		final Currency currency = (Currency) o;

		if(mFactor != currency.mFactor) {
			return false;
		}
		if(mIsFractional != currency.mIsFractional) {
			return false;
		}
		if(mDecimalFactor != null ? !mDecimalFactor.equals(currency.mDecimalFactor) : currency.mDecimalFactor != null) {
			return false;
		}
		return !(mSymbol != null ? !mSymbol.equals(currency.mSymbol) : currency.mSymbol != null);

	}

	@Override
	public int hashCode() {
		int result = (int) (mFactor ^ (mFactor >>> 32));
		result = 31 * result + (mDecimalFactor != null ? mDecimalFactor.hashCode() : 0);
		result = 31 * result + (mIsFractional ? 1 : 0);
		result = 31 * result + (mSymbol != null ? mSymbol.hashCode() : 0);
		return result;
	}

	public BigDecimal getDecimalFactor() {
		return mDecimalFactor;
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
