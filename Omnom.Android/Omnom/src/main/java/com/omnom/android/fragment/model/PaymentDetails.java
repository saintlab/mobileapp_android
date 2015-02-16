package com.omnom.android.fragment.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.omnom.android.mixpanel.model.SplitWay;
import com.omnom.android.mixpanel.model.TipsWay;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.utils.utils.StringUtils;

public class PaymentDetails implements Parcelable {

	public static final Parcelable.Creator<PaymentDetails> CREATOR = new Parcelable.Creator<PaymentDetails>() {
		@Override
		public PaymentDetails createFromParcel(Parcel in) {
			return new PaymentDetails(in);
		}

		@Override
		public PaymentDetails[] newArray(int size) {
			return new PaymentDetails[size];
		}
	};

	private double mAmount;

	private int mTip;

	private int mTipValue;

	private int mTipsWay;

	private int mSplitWay;

	private String tableId;

	private String mTransactionUrl = StringUtils.EMPTY_STRING;

	private long mTransactionTimestmap = 0;

	private String restaurantName;

	private String orderId;

	private int mBillId;

	public PaymentDetails(Parcel parcel) {
		mAmount = parcel.readDouble();
		mTip = parcel.readInt();
		mTipValue = parcel.readInt();
		orderId = parcel.readString();
		tableId = parcel.readString();
		restaurantName = parcel.readString();
		mTipsWay = parcel.readInt();
		mSplitWay = parcel.readInt();
		mTransactionUrl = parcel.readString();
		mTransactionTimestmap = parcel.readLong();
		mBillId = parcel.readInt();
	}

	public PaymentDetails(double amount, int tip, Order order, TipsWay tipsWay, int tipValue, SplitWay splitWay) {
		mAmount = amount;
		mTip = tip;
		mTipValue = tipValue;
		tableId = order.getTableId();
		restaurantName = order.getRestaurantId();
		orderId = order.getId();
		mTipsWay = tipsWay.ordinal();
		mSplitWay = splitWay.ordinal();
	}

	@Override
	public String toString() {
		return "PaymentDetails{" +
				"mAmount=" + mAmount +
				", mTip=" + mTip +
				", mTipValue=" + mTipValue +
				", mTipsWay=" + mTipsWay +
				", mSplitWay=" + mSplitWay +
				", tableId='" + tableId + '\'' +
				", mTransactionUrl='" + mTransactionUrl + '\'' +
				", mTransactionTimestmap=" + mTransactionTimestmap +
				", restaurantName='" + restaurantName + '\'' +
				", orderId='" + orderId + '\'' +
				", mBillId=" + mBillId +
				'}';
	}

	public boolean isSimilar(PaymentDetails details) {
		return details != null &&
				getBillId() == details.getBillId() &&
				getAmount() == details.getAmount() &&
				getTip() == details.getTip() &&
				getTipValue() == details.getTipValue();
	}

	public String getOrderId() {
		return orderId;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public String getTableId() {
		return tableId;
	}

	public int getSplitWay() {
		return mSplitWay;
	}

	public int getTipsWay() {
		return mTipsWay;
	}

	public int getTipValue() {
		return mTipValue;
	}

	public int getTip() {
		return mTip;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeDouble(mAmount);
		dest.writeInt(mTip);
		dest.writeInt(mTipValue);
		dest.writeString(orderId);
		dest.writeString(tableId);
		dest.writeString(restaurantName);
		dest.writeInt(mTipsWay);
		dest.writeInt(mSplitWay);
		dest.writeString(mTransactionUrl);
		dest.writeLong(mTransactionTimestmap);
		dest.writeInt(mBillId);
	}

	public double getAmount() {
		return mAmount;
	}

	public String getTransactionUrl() {
		return mTransactionUrl;
	}

	public void setTransactionUrl(final String transactionUrl) {
		this.mTransactionUrl = transactionUrl;
	}

	public long getTransactionTimestmap() {
		return mTransactionTimestmap;
	}

	public void setTransactionTimestmap(final long transactionTimestmap) {
		mTransactionTimestmap = transactionTimestmap;
	}

	public int getBillId() {
		return mBillId;
	}

	public void setBillId(final int billId) {
		mBillId = billId;
	}

}
