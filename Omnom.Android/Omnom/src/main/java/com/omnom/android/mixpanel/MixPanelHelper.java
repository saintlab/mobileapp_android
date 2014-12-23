package com.omnom.android.mixpanel;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.mixpanel.model.MixpanelEvent;
import com.omnom.android.restaurateur.model.bill.BillResponse;

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

	public static final String KEY_MIXPANEL_TIME = "time";

	public static final String KEY_DATA = "data";

	public static final String KEY_DEVICE_TIMESTAMP = "device_timestamp";

	public static final String KEY_TIMESTAMP = "timestamp";

	private static final String TAG = MixPanelHelper.class.getSimpleName();

	private static final String TIMESTAMP_FORMAT = "{yyyy-MM-dd'T'HH:mm:ssZ}";

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

	public void track(String event, JSONObject json) {
		addTimestamp(json);
		mMixpanelApi.track(event, json);
	}

	public void trackRevenue(final String userId, final OrderFragment.PaymentDetails details, final BillResponse billData) {
		mMixpanelApi.getPeople().identify(userId);
		final int tipValue = details.getTipValue();
		final double totalAmount = details.getAmount() * 100;

		Map<String, Number> userPayemtn = new HashMap<String, Number>();
		userPayemtn.put("bill_sum", totalAmount - tipValue);
		userPayemtn.put("tips_sum", tipValue);
		userPayemtn.put("total_sum", totalAmount);
		userPayemtn.put("number_of_payments", 1);
		mMixpanelApi.getPeople().increment(userPayemtn);

		JSONObject json = new JSONObject();
		addTimestamp(json);
		mMixpanelApi.getPeople().trackCharge(totalAmount, json);
	}

	public void setTimeDiff(final Long timeDiff) {
		this.timeDiff = timeDiff == null ? 0 : timeDiff;
	}

	private void addTimestamp(final JSONObject json) {
		try {
			final Long currentTime = System.currentTimeMillis();
			final Long timestamp = currentTime + timeDiff;
			json.put(KEY_MIXPANEL_TIME, TimeUnit.MILLISECONDS.toSeconds(timestamp));
			json.put(KEY_DEVICE_TIMESTAMP, new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date(currentTime)));
			json.put(KEY_TIMESTAMP, new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date(timestamp)));
		} catch(JSONException e) {
			Log.e(TAG, "track", e);
		}
	}

}
