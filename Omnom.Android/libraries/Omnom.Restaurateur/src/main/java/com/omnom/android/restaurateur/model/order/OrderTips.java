package com.omnom.android.restaurateur.model.order;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ch3D on 10.10.2014.
 */
public class OrderTips implements Parcelable {
	public static final Creator<OrderTips> CREATOR = new Creator<OrderTips>() {

		@Override
		public OrderTips createFromParcel(Parcel in) {
			return new OrderTips(in);
		}

		@Override
		public OrderTips[] newArray(int size) {
			return new OrderTips[size];
		}
	};
	@Expose
	private int threshold;

	@Expose
	private ArrayList<TipsValue> values = new ArrayList<TipsValue>();

	public OrderTips(Parcel parcel) {
		threshold = parcel.readInt();
		parcel.readTypedList(values, TipsValue.CREATOR);
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(threshold);
		parcel.writeTypedList(values);
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public List<TipsValue> getValues() {
		return values;
	}

	public void setValues(ArrayList<TipsValue> values) {
		this.values = values;
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
