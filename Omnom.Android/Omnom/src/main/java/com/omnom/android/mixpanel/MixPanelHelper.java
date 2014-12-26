package com.omnom.android.mixpanel;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.omnom.android.auth.UserData;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.mixpanel.model.MixpanelEvent;
import com.omnom.android.mixpanel.model.UserRegisteredMixpanelEvent;
import com.omnom.android.restaurateur.model.bill.BillResponse;
import com.omnom.android.utils.utils.AndroidUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ch3D on 03.10.2014.
 */
public class MixPanelHelper {

	public static final String USER_ID = "id";

	public static final String USER_NAME = "$name";

	public static final String USER_NICK = "nick";

	public static final String USER_BIRTHDAY = "birthday";

	public static final String USER_EMAIL = "$email";

	public static final String USER_PHONE = "$phone";

	public static final String USER_CREATED = "$created";

	public static final String KEY_MIXPANEL_TIME = "time";

	public static final String KEY_DATA = "data";

	public static final String KEY_DEVICE_TIMESTAMP = "device_timestamp";

	public static final String KEY_TIMESTAMP = "timestamp";

	public static final String MIXPANEL_PUSH_ID = "1021785355576";

	private static final String TAG = MixPanelHelper.class.getSimpleName();

	private static final String TIMESTAMP_FORMAT = "{yyyy-MM-dd'T'HH:mm:ssZ}";

	public static final SimpleDateFormat sSimpleDateFormatter = new SimpleDateFormat(TIMESTAMP_FORMAT);

	public static String formatDate(long ts) {
		return sSimpleDateFormatter.format(new Date(ts));
	}

	private final Gson mGson;

	private MixpanelAPI mMixpanelApi;

	private Long timeDiff = 0L;

	public MixPanelHelper(MixpanelAPI api) {
		mMixpanelApi = api;
		mGson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	}

	public void track(String event, Object request) {
		try {
			final JSONObject json = new JSONObject();
			json.put(KEY_DATA, mGson.toJson(request));
			track(event, json);
		} catch(JSONException e) {
			Log.e(TAG, "track", e);
		}
	}

	public void track(String event, Map data) {
		final JSONObject json = new JSONObject(data);
		try {
			json.put(KEY_DATA, json.toString());
			track(event, json);
		} catch(JSONException e) {
			Log.e(TAG, "track", e);
		}
	}

	public void track(MixpanelEvent event) {
		try {
			final JSONObject json = new JSONObject(mGson.toJson(event));
			track(event.getName(), json);
		} catch(JSONException e) {
			Log.e(TAG, "track", e);
		}
	}

	public void trackDevice(Context context) {
		mMixpanelApi.getPeople().set("Android", Build.VERSION.SDK_INT);
		mMixpanelApi.getPeople().set("Device", Build.MANUFACTURER + " " + Build.MODEL);
		mMixpanelApi.getPeople().set("App", AndroidUtils.getAppVersion(context));
	}

	public void track(String event, JSONObject json) {
		addTimestamp(json);
		mMixpanelApi.track(event, json);
	}

	public void trackRevenue(final String userId, final OrderFragment.PaymentDetails details, final BillResponse billData) {
		mMixpanelApi.getPeople().identify(userId);
		final int tipValue = details.getTipValue(); // already in kopeks
		final double totalAmount = details.getAmount() * 100; // translate into kopeks

		Map<String, Number> userPayemtn = new HashMap<String, Number>();
		final double billSum = totalAmount - tipValue;
		userPayemtn.put("bill_sum", billSum);
		userPayemtn.put("tips_sum", tipValue);
		userPayemtn.put("total_sum", totalAmount);
		userPayemtn.put("number_of_payments", 1);
		mMixpanelApi.getPeople().increment(userPayemtn);

		JSONObject json = new JSONObject();
		addTimestamp(json);

		final double revenue = (billSum * billData.getAmountCommission()) + (tipValue * billData.getTipCommission());
		mMixpanelApi.getPeople().trackCharge(revenue, json);
	}

	public void setTimeDiff(final Long timeDiff) {
		this.timeDiff = timeDiff == null ? 0 : timeDiff;
	}

	private void addTimestamp(final JSONObject json) {
		try {
			final Long currentTime = System.currentTimeMillis();
			final Long timestamp = currentTime + timeDiff;
			json.put(KEY_MIXPANEL_TIME, TimeUnit.MILLISECONDS.toSeconds(timestamp));
			json.put(KEY_DEVICE_TIMESTAMP, sSimpleDateFormatter.format(new Date(currentTime)));
			json.put(KEY_TIMESTAMP, sSimpleDateFormatter.format(new Date(timestamp)));
		} catch(JSONException e) {
			Log.e(TAG, "track", e);
		}
	}

	public void trackUserLogin(Context context, UserData user) {
		if(user == null) {
			return;
		}
		final String id = String.valueOf(user.getId());
		mMixpanelApi.identify(id);
		mMixpanelApi.getPeople().identify(id);
		trackDevice(context);
		mMixpanelApi.getPeople().initPushHandling(MixPanelHelper.MIXPANEL_PUSH_ID);
	}

	public void trackUserRegister(final Context context, final UserData user) {
		if(user == null) {
			return;
		}
		final String id = String.valueOf(user.getId());
		mMixpanelApi.identify(id);
		track(new UserRegisteredMixpanelEvent(user));

		mMixpanelApi.getPeople().identify(id);
		mMixpanelApi.getPeople().set(toJson(user));
		mMixpanelApi.getPeople().set(MixPanelHelper.USER_CREATED, MixPanelHelper.formatDate(System.currentTimeMillis()));
		trackDevice(context);
		mMixpanelApi.getPeople().initPushHandling(MixPanelHelper.MIXPANEL_PUSH_ID);
	}

	public void trackUserDefault(final Context context, final UserData user) {
		if(user == null) {
			return;
		}
		// TODO:
		final String id = String.valueOf(user.getId());
		mMixpanelApi.getPeople().identify(id);
		mMixpanelApi.getPeople().initPushHandling(MixPanelHelper.MIXPANEL_PUSH_ID);
	}

	public JSONObject toJson(UserData user) {
		final JSONObject jsonUser = new JSONObject();
		try {
			jsonUser.put(USER_ID, user.getId());
			jsonUser.put(USER_NAME, user.getName());
			jsonUser.put(USER_NICK, user.getNick());
			jsonUser.put(USER_BIRTHDAY, user.getBirthDate());
			jsonUser.put(USER_EMAIL, user.getEmail());
			jsonUser.put(USER_PHONE, user.getPhone());
		} catch(JSONException e) {
			Log.e(TAG, "toJson", e);
		}
		return jsonUser;
	}
}
