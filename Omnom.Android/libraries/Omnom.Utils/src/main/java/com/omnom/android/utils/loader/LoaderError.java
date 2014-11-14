package com.omnom.android.utils.loader;

import com.omnom.android.utils.R;

/**
 * Created by Ch3D on 16.10.2014.
 */
public class LoaderError {

	public static final LoaderError NO_CONNECTION_TRY = new LoaderError(R.drawable.ic_no_connection,
	                                                                    R.string.error_unknown_server_error,
	                                                                    R.string.try_once_again);

	public static final LoaderError BLE_DISABLED = new LoaderError(R.drawable.ic_bluetooth_white,
	                                                               R.string.error_bluetooth_disabled,
	                                                               R.string.open_settings);

	public static final LoaderError LOCATION_DISABLED = new LoaderError(R.drawable.ic_geolocation_white,
	                                                                    R.string.error_location_disabled,
	                                                                    R.string.open_settings);

	public static final LoaderError WEAK_SIGNAL = new LoaderError(R.drawable.ic_weak_signal,
	                                                              R.string.error_weak_beacon_signal,
	                                                              R.string.repeat);

	public static final LoaderError TWO_BEACONS = new LoaderError(R.drawable.ic_weak_signal,
	                                                              R.string.error_more_than_one_beacon,
	                                                              R.string.repeat);
	private final int mDrawableResId;
	private final int mErrResId;
	private final int mBtnResId;

	protected LoaderError(int drawableResId, int errResId, int btnResId) {
		mDrawableResId = drawableResId;
		mErrResId = errResId;
		mBtnResId = btnResId;
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
}
