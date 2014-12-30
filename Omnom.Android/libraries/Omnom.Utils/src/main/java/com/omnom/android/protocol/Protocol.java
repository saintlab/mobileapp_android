package com.omnom.android.protocol;

/**
 * Created by Ch3D on 11.08.2014.
 */
public interface Protocol {
	public static final String HEADER_X_MOBILE_VENDOR = "x-mobile-vendor";
	public static final String HEADER_X_MOBILE_MODEL = "x-mobile-model";
	public static final String HEADER_X_MOBILE_OS_VERSION = "x-mobile-os-version";
	public static final String HEADER_X_MOBILE_PLATFORM = "x-mobile-platform";

	public static final String FIELD_ID = "id";
	public static final String FIELD_LOGIN = "login";
	public static final String FIELD_EMAIL = "email";
	public static final String FIELD_PASSWORD = "password";
	public static final String FIELD_RESTAURANT_ID = "restaurant_id";
	public static final String FIELD_TABLE_ID = "table_id";
	public static final String FIELD_ORDER_ID = "order_id";
	public static final String FIELD_AMOUNT = "amount";
	public static final String FIELD_TIP = "tip";
	public static final String FIELD_CARD_ID = "card_id";
	public static final String FIELD_TABLE_NUMBER = " table_num";

	public static final String FIELD_BEACON_UUID = "uuid";
	public static final String FIELD_MAJOR_ID = "major";
	public static final String FIELD_MINOR_ID = "minor";
	public static final String FIELD_BEACON_UUID_OLD = "old_uuid";
	public static final String FIELD_MAJOR_ID_OLD = "old_major";
	public static final String FIELD_MINOR_ID_OLD = "old_minor";

	public static final String FIELD_QR_DATA = "qr";
	public static final String HEADER_AUTH_TOKEN = "X-Authentication-Token";
	public static final String X_REQUEST_ID = "X-Request-ID";
}
