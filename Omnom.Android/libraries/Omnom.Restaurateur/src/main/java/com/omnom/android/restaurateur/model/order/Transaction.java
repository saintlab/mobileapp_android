package com.omnom.android.restaurateur.model.order;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 02.12.2014.
 */
public class Transaction implements Parcelable {
	public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {

		@Override
		public Transaction createFromParcel(Parcel in) {
			return new Transaction(in);
		}

		@Override
		public Transaction[] newArray(int size) {
			return new Transaction[size];
		}
	};

	@Expose
	private int userId;

	@Expose
	private int orderId;

	@Expose
	private int amount;

	@Expose
	private int tip;

	public Transaction() {
		// do nothing
	}

	public Transaction(int amount, int tip) {
		this.amount = amount;
		this.tip = tip;
	}

	public Transaction(int userId, int amount, int tip) {
		this(amount, tip);
		this.userId = userId;
	}

	public Transaction(Parcel parcel) {
		userId = parcel.readInt();
		orderId = parcel.readInt();
		amount = parcel.readInt();
		tip = parcel.readInt();
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getTip() {
		return tip;
	}

	public void setTip(final int tip) {
		this.tip = tip;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(final int amount) {
		this.amount = amount;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeInt(userId);
		dest.writeInt(orderId);
		dest.writeInt(amount);
		dest.writeInt(tip);
	}
}
