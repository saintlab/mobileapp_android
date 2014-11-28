package com.omnom.android.utils.loader;

import com.omnom.android.utils.R;

/**
 * Created by Ch3D on 16.10.2014.
 */
public class LoaderError {

	public static final LoaderError NO_CONNECTION_TRY =
			new LoaderError(R.drawable.ic_no_connection, R.string.error_no_internet_connection, R.string.try_once_again,
			                R.drawable.ic_repeat_small);

	public static final LoaderError UNKNOWN_QR_CODE =
			new LoaderError(R.drawable.ic_no_connection, R.string.error_unknown_qr, R.string.try_once_again,
			                R.drawable.ic_repeat_small);

	public static final LoaderError BLE_DISABLED =
			new LoaderError(R.drawable.ic_bluetooth_white, R.string.error_bluetooth_disabled, R.string.open_settings, 0);

	public static final LoaderError LOCATION_DISABLED =
			new LoaderError(R.drawable.ic_geolocation_white, R.string.error_location_disabled, R.string.open_settings, 0);

	public static final LoaderError WEAK_SIGNAL =
			new LoaderError(R.drawable.ic_weak_signal, R.string.error_weak_beacon_signal, R.string.repeat, R.drawable.ic_repeat_small);

	public static final LoaderError TWO_BEACONS =
			new LoaderError(R.drawable.ic_weak_signal, R.string.error_more_than_one_beacon, R.string.repeat, R.drawable.ic_repeat_small);

	public static final LoaderError PAYMENT_DECLINED =
			new LoaderError(R.drawable.ic_flying_credit_card, R.string.error_payment_declined, R.string.error_ok, 0);

	public static final LoaderError NO_ORDERS =
			new LoaderError(R.drawable.ic_bill_white, R.string.there_are_no_orders_on_this_table, R.string.error_ok, 0);

	private final int mDrawableResId;
	private final int mErrResId;
	private final int mBtnResId;
	private final int mBtnDrawableId;

	protected LoaderError(int drawableResId, int errResId, int btnResId, int btnDrawableId) {
		mDrawableResId = drawableResId;
		mErrResId = errResId;
		mBtnResId = btnResId;
		mBtnDrawableId = btnDrawableId;
	}

	public int getDrawableId() {
		return mDrawableResId;
	}

	public int getErrorId() {
		return mErrResId;
	}

	public int getButtonTextId() {
		return mBtnResId;
	}

	public int getmBtnDrawableId() {
		return mBtnDrawableId;
	}
}
