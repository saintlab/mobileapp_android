package com.omnom.android.utils.loader;

import com.omnom.android.utils.R;

/**
 * Created by Ch3D on 16.10.2014.
 */
public class LoaderError {

	public static final String EVENT_NO_SERVER_CONNECTION = "no_server_connection";

	public static final LoaderError NO_CONNECTION_TRY =
			new LoaderError(EVENT_NO_SERVER_CONNECTION, R.drawable.ic_no_connection, R.string.error_no_internet_connection,
			                R.string.try_once_again,
			                R.drawable.ic_repeat_small);

	public static final String EVENT_UNKNOWN_ERROR = "unknown_error";

	public static final LoaderError UNKNOWN_ERROR =
			new LoaderError(EVENT_UNKNOWN_ERROR, R.drawable.ic_line_noise, R.string.something_went_wrong,
							R.string.error_ok, 0);

	public static final String EVENT_ORDER_CLOSED_ERROR = "order_closed";

	public static final LoaderError ORDER_CLOSED_ERROR =
			new LoaderError(EVENT_ORDER_CLOSED_ERROR, R.drawable.ic_flying_credit_card, R.string.order_closed,
					R.string.error_ok, 0);

	public static final String EVENT_BACKEND_ERROR = "no_restaurant_connection";

	public static final LoaderError BACKEND_ERROR =
			new LoaderError(EVENT_BACKEND_ERROR, R.drawable.ic_line_noise, R.string.something_went_wrong, R.string.lets_try_again,
			                R.drawable.ic_repeat_small);

	public static final String EVENT_NO_TABLE = "no_table";

	public static final LoaderError UNKNOWN_QR_CODE =
			new LoaderError(EVENT_NO_TABLE, R.drawable.ic_line_noise, R.string.error_unknown_qr, R.string.check_again,
			                R.drawable.ic_repeat_small);

	public static final String BLUETOOTH_DISABLED = "bluetooth_turned_off";

	public static final LoaderError BLE_DISABLED =
			new LoaderError(BLUETOOTH_DISABLED, R.drawable.ic_bluetooth_white, R.string.error_bluetooth_disabled,
			                R.string.turn_on_bluetooth, 0);

	public static final String EVENT_GEOLOCATION_DISABLED = "no_geolocation_permission";

	public static final LoaderError LOCATION_DISABLED =
			new LoaderError(EVENT_GEOLOCATION_DISABLED, R.drawable.ic_geolocation_white, R.string.error_location_disabled,
			                R.string.open_settings, 0);

	public static final String EVENT_LOW_SIGNAL = "low signal";

	public static final LoaderError WEAK_SIGNAL =
			new LoaderError(EVENT_LOW_SIGNAL, R.drawable.ic_weak_signal, R.string.error_weak_beacon_signal,
			                R.string.repeat,
			                R.drawable.ic_repeat_small);

	public static final String EVENT_LOW_SIGNAL_MULTIPLE = "low_signal_multiple_beacons";

	public static final LoaderError TWO_BEACONS =
			new LoaderError(EVENT_LOW_SIGNAL_MULTIPLE, R.drawable.ic_weak_signal,
			                R.string.error_more_than_one_beacon,
			                R.string.repeat, R.drawable.ic_repeat_small);

	public static final String EVENT_PAYMENT_DECLINED = "payment_declined";

	public static final LoaderError PAYMENT_DECLINED =
			new LoaderError(EVENT_PAYMENT_DECLINED, R.drawable.ic_flying_credit_card,
			                R.string.error_payment_declined,
			                R.string.error_ok, 0);

	public static final String EVENT_NO_ORDERS = "no_orders";

	public static final LoaderError NO_ORDERS =
			new LoaderError(EVENT_NO_ORDERS, R.drawable.ic_bill_white, R.string.there_are_no_orders_on_this_table, R.string.error_ok, 0);

	private final int mDrawableResId;

	private final int mErrResId;

	private final int mBtnResId;

	private final int mBtnDrawableId;

	private String mEventName;

	protected LoaderError(final String eventName, int drawableResId, int errResId, int btnResId, int btnDrawableId) {
		mEventName = eventName;
		mDrawableResId = drawableResId;
		mErrResId = errResId;
		mBtnResId = btnResId;
		mBtnDrawableId = btnDrawableId;
	}

	/**
	 * @return mixpanel event name
	 */
	public String getEventName() {
		return mEventName;
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
