package com.omnom.android.restaurateur.model.order;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 10.10.2014.
 */
public class TipsValue implements Parcelable {
	public static final Creator<TipsValue> CREATOR = new Creator<TipsValue>() {

		@Override
		public TipsValue createFromParcel(Parcel in) {
			return new TipsValue(in);
		}

		@Override
		public TipsValue[] newArray(int size) {
			return new TipsValue[size];
		}
	};
	@Expose
	private int amount;

	@Expose
	private int percent;

	public TipsValue(Parcel parcel) {
		amount = parcel.readInt();
		percent = parcel.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(amount);
		dest.writeInt(percent);
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
