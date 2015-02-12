package com.omnom.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.utils.utils.StringUtils;

/**
 * Created by Ch3D on 10.02.2015.
 */
public class PaymentChecker {
	private static final String PAYMENTS = "com.omnom.android.preferences.payments_checker";

	private static String TAG = PaymentChecker.class.getSimpleName();

	private final Gson mGson;

	private Context mContext;

	public PaymentChecker(Context context) {
		mContext = context;
		mGson = new Gson();
	}

	private boolean validate(final OrderFragment.PaymentDetails details) {
		if(details == null) {
			return false;
		}
		if(details.getBillId() <= 0) {
			return false;
		}
		return true;
	}

	public void onPaymentRequested(@Nullable final OrderFragment.PaymentDetails details) {
		if(validate(details)) {
			final String strId = String.valueOf(details.getBillId());
			getPreferences().edit().putString(strId, null).apply();
			getPreferences().edit().putString(strId, mGson.toJson(details)).apply();
		}
	}

	/**
	 * @param details
	 * @return false - if current payment was already processed, true - otherwise.
	 */
	public OrderFragment.PaymentDetails check(@Nullable final OrderFragment.PaymentDetails details) {
		if(validate(details)) {
			final OrderFragment.PaymentDetails prevDetails = getPayment(details.getBillId());
			final boolean same = details.isSimilar(prevDetails);
			if(same) {
				return prevDetails;
			}
		}
		return null;
	}

	@Nullable
	private OrderFragment.PaymentDetails getPayment(final int id) {
		if(id > 0) {
			final String billId = String.valueOf(id);
			final String string = getPreferences().getString(billId, StringUtils.EMPTY_STRING);
			if(TextUtils.isEmpty(string)) {
				return null;
			}
			try {
				return mGson.fromJson(string, OrderFragment.PaymentDetails.class);
			} catch(JsonSyntaxException e) {
				Log.e(TAG, "getPayment: id = " + id, e);
				return null;
			}
		}
		return null;
	}

	private SharedPreferences getPreferences() {return mContext.getSharedPreferences(PAYMENTS, Context.MODE_PRIVATE);}

	public void onPrePayment(@Nullable final OrderFragment.PaymentDetails details) {
		if(validate(details)) {
			final String strId = String.valueOf(details.getBillId());
			final SharedPreferences preferences = getPreferences();
			preferences.edit().putString(strId, null).apply(); // remove old value
			preferences.edit().putString(strId, mGson.toJson(details)).apply();
		}
	}

	public void clearCache(@Nullable final OrderFragment.PaymentDetails details) {
		if(validate(details)) {
			final int billId = details.getBillId();
			final OrderFragment.PaymentDetails payment = getPayment(billId);
			if(payment != null && details.isSimilar(payment)) {
				getPreferences().edit().remove(String.valueOf(billId)).apply();
			}
		}
	}
}
