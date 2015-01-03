package com.omnom.android.utils;

import com.omnom.android.utils.preferences.Preferences;

/**
 * Created by Ch3D on 14.08.2014.
 */
public interface Extras extends Preferences {
	public static final String EXTRA_BEACON = "com.omnom.android.linker.beacon";
	public static final String EXTRA_RESTAURANT = "com.omnom.android.linker.restaurant";
	public static final String EXTRA_RESTAURANTS = "com.omnom.android.linker.restaurants";
	public static final String EXTRA_USERNAME = "com.omnom.android.linker.username";
	public static final String EXTRA_PASSWORD = "com.omnom.android.linker.password";
	public static final String EXTRA_ERROR_CODE = "com.omnom.android.linker.error.code";
	public static final String EXTRA_SELECTED_RESTAURANT = "com.omnom.android.linker.selected_restaurant";
	public static final String EXTRA_SHOW_BACK = "com.omnom.android.linker.bind.show_back";
	public static final String EXTRA_LOADER_ANIMATION = "com.omnom.android.linker.loader.animation";
	public static final String EXTRA_DEMO_MODE = "com.omnom.android.mode.demo";
	public static final String EXTRA_DURATION_SPLASH = "com.omnom.android.mode.duration_splash";
	public static final String EXTRA_SKIP_SPLASH = "com.omnom.android.mode.skip_splash";
	public static final String EXTRA_SCAN_QR = "com.omnom.android.mode.scan_qr";
	public static final String EXTRA_ANIMATE = "com.omnom.android.linker.activity.animate";
	public static final String EXTRA_TABLE_NUMBER = "com.omnom.android.restaurant.table_number";
	public static final String EXTRA_TABLE_ID = "com.omnom.android.restaurant.table_id";
	public static final String EXTRA_PHONE = "com.omnom.android.user.phone";
	public static final String EXTRA_ORDERS = "com.omnom.android.table.orders";
	public static final String EXTRA_REQUEST_ID = "com.omnom.android.request_id";
	public static final String EXTRA_MARGIN = "com.omnom.android.page.margin";
	public static final String EXTRA_ACCENT_COLOR = "com.omnom.android.order.color.accent";
	public static final String EXTRA_ORDER_AMOUNT = "com.omnom.android.order.amount";
	public static final String EXTRA_SCAN_USED = "com.omnom.android.order.scan_used";
	public static final String EXTRA_CONFIRM_TYPE = "com.omnom.android.user.phone.confirm_type";
	public static final String EXTRA_CARD_DATA = "com.omnom.android.card.data";
	public static final String EXTRA_TYPE = "com.omnom.android.type";
	public static final String EXTRA_ORDER = "com.omnom.android.order";
	public static final String EXTRA_PAYMENT_DETAILS = "com.omnom.android.payment.details";
	public static final String EXTRA_PAYMENT_EVENT = "com.omnom.android.payment.event";
	public static final String EXTRA_TRANSACTION_URL = "com.omnom.android.payment.transaction_url";
	public static final String EXTRA_ANIMATION_EXIT = "com.omnom.android.extra.animation";
	public static final String EXTRA_TRANSLATION_TOP = "com.omnom.android.extra.translation.top";

	public static final String EXTRA_CHARACTERISTIC_UUID = "ble.characteristic.uuid";
	public static final String EXTRA_CHARACTERISTIC_VALUE = "ble.characteristic.value";

	public static final int EXTRA_ERROR_WRONG_PASSWORD = 1;
	public static final int EXTRA_ERROR_WRONG_USERNAME = 2;
	public static final int EXTRA_ERROR_AUTHTOKEN_EXPIRED = 4;
	public static final int EXTRA_ERROR_LOGOUT = 8;

	public static final int EXTRA_ANIMATION_SLIDE_OUT_RIGHT = 0;

	public static final int EXTRA_LOADER_ANIMATION_SCALE_UP = 0;
	public static final int EXTRA_LOADER_ANIMATION_SCALE_DOWN = 1;
}
