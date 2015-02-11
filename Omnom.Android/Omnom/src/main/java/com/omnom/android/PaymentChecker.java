package com.omnom.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.utils.utils.StringUtils;

/**
 * Created by Ch3D on 10.02.2015.
 */
public class PaymentChecker {
	private static final String PAYMENTS = "preferences.payments";

	private final Gson mGson;

	private Context mContext;

	public PaymentChecker(Context context) {
		mContext = context;
		mGson = new Gson();
	}

	public void onPaymentRequested(@Nullable final OrderFragment.PaymentDetails details) {
		if(details == null) {
			// fail fast
			return;
		}

		final String strId = String.valueOf(details.getBillId());
		getPreferences().edit().putString(strId, null).apply();
		getPreferences().edit().putString(strId, mGson.toJson(details)).apply();
	}

	/**
	 * @param details
	 * @return false - if current payment was already processed, true - otherwise.
	 */
	public OrderFragment.PaymentDetails check(@Nullable final OrderFragment.PaymentDetails details) {
		if(details == null) {
			// fail fast
			return null;
		}

		final OrderFragment.PaymentDetails prevDetails = getPayment(details.getBillId());
		final boolean same = details.isSame(prevDetails);
		if(same) {
			return prevDetails;
		}
		return null;
	}

	@Nullable
	private OrderFragment.PaymentDetails getPayment(int id) {
		if(id > 0) {
			final String billId = String.valueOf(id);
			final String string = getPreferences().getString(billId, StringUtils.EMPTY_STRING);
			if(TextUtils.isEmpty(string)) {
				return null;
			}
			return mGson.fromJson(string, OrderFragment.PaymentDetails.class);
		}
		return null;
	}

	private SharedPreferences getPreferences() {return mContext.getSharedPreferences(PAYMENTS, Context.MODE_PRIVATE);}

	public void onPrePayment(@Nullable final OrderFragment.PaymentDetails details) {
		if(details == null) {
			// fail fast
			return;
		}

		final String strId = String.valueOf(details.getBillId());
		getPreferences().edit().putString(strId, null).apply();
		getPreferences().edit().putString(strId, mGson.toJson(details)).apply();
	}

	public void clearCache(@Nullable final OrderFragment.PaymentDetails details) {
		if(details == null) {
			// fail fast
			return;
		}

		final int billId = details.getBillId();
		final OrderFragment.PaymentDetails payment = getPayment(billId);
		if(payment != null && details.isSame(payment)) {
			getPreferences().edit().remove(String.valueOf(billId)).apply();
		}
	}
}
