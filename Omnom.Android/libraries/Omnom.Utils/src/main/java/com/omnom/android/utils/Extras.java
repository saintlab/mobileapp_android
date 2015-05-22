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
	public static final String EXTRA_SKIP_VIEW_RENDERING = "com.omnom.android.loader.skip_view_rendering";
	public static final String EXTRA_DEMO_MODE = "com.omnom.android.mode.demo";
	public static final String EXTRA_SPLASH_DELAY = "com.omnom.android.mode.splash_delay";
	public static final String EXTRA_APPLICATION_LAUNCH = "com.omnom.android.mode.application_launch";
	public static final String EXTRA_SKIP_SPLASH = "com.omnom.android.mode.skip_splash";
	public static final String EXTRA_CHANGE_TABLE = "com.omnom.android.table.change";
	public static final String EXTRA_ANIMATE = "com.omnom.android.linker.activity.animate";
	public static final String EXTRA_TABLE_NUMBER = "com.omnom.android.restaurant.table_number";
	public static final String EXTRA_TABLE_ID = "com.omnom.android.restaurant.table_id";
	public static final String EXTRA_PHONE = "com.omnom.android.user.phone";
	public static final String EXTRA_ORDERS = "com.omnom.android.table.orders";
	public static final String EXTRA_REQUEST_ID = "com.omnom.android.request_id";
	public static final String EXTRA_MARGIN = "com.omnom.android.page.margin";
	public static final String EXTRA_ACCENT_COLOR = "com.omnom.android.order.color.accent";
	public static final String EXTRA_ORDER_NUMBER = "com.omnom.android.order.color.order_number";
	public static final String EXTRA_PIN_CODE = "com.omnom.android.order.color.pin_code";
	public static final String EXTRA_ORDER_AMOUNT = "com.omnom.android.order.amount";
	public static final String EXTRA_ORDER_TIPS = "com.omnom.android.order.tips";
	public static final String EXTRA_SCAN_USED = "com.omnom.android.order.scan_used";
	public static final String EXTRA_CONFIRM_TYPE = "com.omnom.android.user.phone.confirm_type";
	public static final String EXTRA_CARD_DATA = "com.omnom.android.card.data";
	public static final String EXTRA_WISH_RESPONSE = "com.omnom.android.wish.response";
	public static final String EXTRA_WISH_ID = "com.omnom.android.wish.id";
	public static final String EXTRA_WISH_STATUS = "com.omnom.android.wish.status";
	public static final String EXTRA_TYPE = "com.omnom.android.type";
	public static final String EXTRA_ORDER = "com.omnom.android.order";
	public static final String EXTRA_ORDER_TIME = "com.omnom.android.order.time";
	public static final String EXTRA_DELIVERY_ADDRESS = "com.omnom.android.delivery.address";
	public static final String EXTRA_DELIVERY_TIME = "com.omnom.android.delivery.time";
	public static final String EXTRA_TAKEAWAY_ADDRESS = "com.omnom.android.takeaway.address";
	public static final String EXTRA_TAKEAWAY_AFTER = "com.omnom.android.takeaway.after";
	public static final String EXTRA_USER_ORDER = "com.omnom.android.wish.order";
	public static final String EXTRA_ORDER_DATA = "com.omnom.android.order.data";
	public static final String EXTRA_PAYMENT_DETAILS = "com.omnom.android.payment.details";
	public static final String EXTRA_PAYMENT_TYPE = "com.omnom.android.payment.type";
	public static final String EXTRA_PAYMENT_EVENT = "com.omnom.android.payment.event";
	public static final String EXTRA_TRANSACTION_URL = "com.omnom.android.payment.transaction_url";
	public static final String EXTRA_ANIMATION_EXIT = "com.omnom.android.extra.animation";
	public static final String EXTRA_RESTAURANT_MENU = "com.omnom.android.restaurant.menu";
	public static final String EXTRA_MENU_ITEM = "com.omnom.android.restaurant.menu.item";
	public static final String EXTRA_TABLE = "com.omnom.android.restaurant.table";
	public static final String EXTRA_RESTAURANT_MENU_CATEGORY = "com.omnom.android.restaurant.menu.category";
	public static final String EXTRA_POSITION = "com.omnom.android.position";
	public static final String EXTRA_TRANSLATION_CONTENT = "com.omnom.android.extra.translation.content";
	public static final String EXTRA_TRANSITION_PARAMS = "com.omnom.android.extra.transition.params";
	public static final String EXTRA_TRANSLATION_TOP = "com.omnom.android.extra.translation.top";
	public static final String EXTRA_TRANSLATION_BUTTON = "com.omnom.android.extra.translation.btn";
	public static final String EXTRA_TITLE_SIZE = "com.omnom.android.extra.title.height";
	public static final String EXTRA_PIVOT_Y = "com.omnom.android.position.y";
	public static final String EXTRA_ENTRANCE_DATA = "com.omnom.android.entrance.data";

	public static final String EXTRA_URI = "com.omnom.android.uri";

	public static final String EXTRA_CHARACTERISTIC_UUID = "ble.characteristic.uuid";
	public static final String EXTRA_CHARACTERISTIC_VALUE = "ble.characteristic.value";

	public static final int EXTRA_ERROR_WRONG_PASSWORD = 1;
	public static final int EXTRA_ERROR_WRONG_USERNAME = 2;
	public static final int EXTRA_ERROR_AUTHTOKEN_EXPIRED = 4;
	public static final int EXTRA_ERROR_LOGOUT = 8;

	public static final int EXTRA_ANIMATION_SLIDE_OUT_RIGHT = 0;

	public static final int EXTRA_LOADER_ANIMATION_SCALE_UP = 0;
	public static final int EXTRA_LOADER_ANIMATION_SCALE_DOWN = 1;
	public static final int EXTRA_LOADER_ANIMATION_FIXED = 2;

	public static final int REQUEST_CODE_CHANGE_TABLE = 2000;
	public static final int REQUEST_CODE_LOGIN = 3000;
	public static final int REQUEST_CODE_LOGIN_CONFIRM = 3001;
	public static final int RESULT_CODE_TABLE_CHANGED = 2001;
	public static final int REQUEST_CODE_MENU_ITEM = 5001;
	public static final int REQUEST_CODE_WISH_LIST = 5002;
	public static final int REQUEST_CODE_MENU_SUBCATEGORY = 5003;
	public static final int REQUEST_CODE_HANDLE_THREE_DS = 5004;

	public static final String ACTION_EVENT_PAYMENT = "com.saintlab.android.event.payment";
	public static final String ACTION_EVENT_ORDER_CREATE = "com.saintlab.android.order.create";
	public static final String ACTION_EVENT_ORDER_CLOSE = "com.saintlab.android.order.close";
	public static final String ACTION_EVENT_ORDER_UPDATE = "com.saintlab.android.order.update";
}
