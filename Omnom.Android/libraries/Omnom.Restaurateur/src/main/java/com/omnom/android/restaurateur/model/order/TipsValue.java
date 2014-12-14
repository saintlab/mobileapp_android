package com.omnom.android.restaurateur.model.order;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

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
	private List<Integer> amounts;

	@Expose
	private int percent;

	public TipsValue(Parcel parcel) {
		amounts = new ArrayList<Integer>();
		parcel.readList(amounts, List.class.getClassLoader());
		percent = parcel.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(amounts);
		dest.writeInt(percent);
	}

	public List<Integer> getAmounts() {
		return amounts;
	}

	public void setAmounts(List<Integer> amounts) {
		this.amounts = amounts;
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
