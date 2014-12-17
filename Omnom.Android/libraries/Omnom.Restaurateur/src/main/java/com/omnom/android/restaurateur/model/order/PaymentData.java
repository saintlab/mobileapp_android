package com.omnom.android.restaurateur.model.order;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 02.12.2014.
 */
public class PaymentData implements Parcelable {
	public static final Creator<PaymentData> CREATOR = new Creator<PaymentData>() {

		@Override
		public PaymentData createFromParcel(Parcel in) {
			return new PaymentData(in);
		}

		@Override
		public PaymentData[] newArray(int size) {
			return new PaymentData[size];
		}
	};

	@Expose
	private Order order;

	@Expose
	private User user;

	@Expose
	private Transaction transaction;

	public PaymentData(Parcel parcel) {
		order = parcel.readParcelable(Order.class.getClassLoader());
		user = parcel.readParcelable(User.class.getClassLoader());
		transaction = parcel.readParcelable(Transaction.class.getClassLoader());
	}

	public PaymentData() {
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(final Order order) {
		this.order = order;
	}

	public User getUser() {
		return user;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(final Transaction transaction) {
		this.transaction = transaction;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeParcelable(order, flags);
		dest.writeParcelable(user, flags);
		dest.writeParcelable(transaction, flags);
	}
}
