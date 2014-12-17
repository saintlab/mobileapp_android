package com.omnom.android.socket.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.omnom.android.auth.UserData;
import com.omnom.android.restaurateur.model.order.PaymentData;
import com.omnom.android.restaurateur.model.order.Transaction;
import com.omnom.android.utils.UserHelper;
import com.omnom.android.utils.utils.AmountHelper;

/**
 * Created by Ch3D on 26.11.2014.
 */
public class PaymentSocketEvent extends BaseSocketEvent implements Parcelable {
	public static final Creator<PaymentSocketEvent> CREATOR = new Creator<PaymentSocketEvent>() {

		@Override
		public PaymentSocketEvent createFromParcel(Parcel in) {
			return new PaymentSocketEvent(in);
		}

		@Override
		public PaymentSocketEvent[] newArray(int size) {
			return new PaymentSocketEvent[size];
		}
	};

	public static PaymentSocketEvent createDemoEvent(UserData userData, double amount) {
		final PaymentData data = new PaymentData();
		data.setUser(UserHelper.toPaymentUser(userData));
		data.setTransaction(new Transaction(AmountHelper.toInt(amount), 0));
		return new PaymentSocketEvent(data);
	}

	private PaymentData mPaymentData;

	public PaymentSocketEvent(final PaymentData paymentData) {
		super();
		mPaymentData = paymentData;
	}

	public PaymentSocketEvent(Parcel parcel) {
		mPaymentData = parcel.readParcelable(PaymentData.class.getClassLoader());
	}

	public PaymentData getPaymentData() {
		return mPaymentData;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeParcelable(mPaymentData, flags);
	}
}
