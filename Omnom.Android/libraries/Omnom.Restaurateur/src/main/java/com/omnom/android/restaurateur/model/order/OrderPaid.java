package com.omnom.android.restaurateur.model.order;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 24.05.2015.
 */
public class OrderPaid implements Parcelable {
	public static final Creator<OrderPaid> CREATOR = new Creator<OrderPaid>() {

		@Override
		public OrderPaid createFromParcel(Parcel in) {
			return new OrderPaid(in);
		}

		@Override
		public OrderPaid[] newArray(int size) {
			return new OrderPaid[size];
		}
	};

	@Expose
	private int amount;

	@Expose
	private int tip;

	public OrderPaid() {
		// do nothing
	}

	public OrderPaid(Parcel parcel) {
		amount = parcel.readInt();
		tip = parcel.readInt();
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(final int amount) {
		this.amount = amount;
	}

	public int getTip() {
		return tip;
	}

	public void setTip(final int tip) {
		this.tip = tip;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeInt(amount);
		dest.writeInt(tip);
	}
}
