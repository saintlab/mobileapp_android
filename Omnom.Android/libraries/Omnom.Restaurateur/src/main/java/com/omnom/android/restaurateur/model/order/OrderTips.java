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
	private List<Integer> thresholds;

	@Expose
	private ArrayList<TipsValue> values = new ArrayList<TipsValue>();

	public OrderTips(Parcel parcel) {
		thresholds = new ArrayList<Integer>();
		parcel.readList(thresholds, List.class.getClassLoader());
		parcel.readTypedList(values, TipsValue.CREATOR);
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeList(thresholds);
		parcel.writeTypedList(values);
	}

	public List<Integer> getThresholds() {
		return thresholds;
	}

	public void setThresholds(List<Integer> thresholds) {
		this.thresholds = thresholds;
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
