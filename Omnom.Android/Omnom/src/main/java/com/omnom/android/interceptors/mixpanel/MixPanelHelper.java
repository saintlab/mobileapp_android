package com.omnom.android.interceptors.mixpanel;

import android.util.Log;

import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Ch3D on 03.10.2014.
 */
public class MixPanelHelper {

	public static final String KEY_DATA = "data";
	public static final String KEY_TIMESTAMP = "timestamp";

	private static final String TAG = MixPanelHelper.class.getSimpleName();

	private final Gson mGson;
	private MixpanelAPI mMixpanelApi;

	public MixPanelHelper(MixpanelAPI api) {
		mMixpanelApi = api;
		mGson = new Gson();
	}

	protected void track(String event, Object request) {
		try {
			final JSONObject json = new JSONObject();
			json.put(KEY_DATA, mGson.toJson(request));
			track(event, json);
		} catch(JSONException e) {
			Log.e(TAG, "track", e);
		}
	}

	protected void track(String event, Map data) {
		final JSONObject json = new JSONObject(data);
		try {
			json.put(KEY_DATA, json.toString());
			track(event, json);
		} catch(JSONException e) {
			Log.e(TAG, "track", e);
		}
	}

	protected void track(String event, JSONObject json) {
		try {
			json.put(KEY_TIMESTAMP, System.currentTimeMillis());
			mMixpanelApi.track(event, json);
		} catch(JSONException e) {
			Log.e(TAG, "track", e);
		}
	}
}
