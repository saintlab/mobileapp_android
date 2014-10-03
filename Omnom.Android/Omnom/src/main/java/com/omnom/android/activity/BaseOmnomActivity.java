package com.omnom.android.activity;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.omnom.android.OmnomApplication;
import com.omnom.util.activity.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ch3D on 01.10.2014.
 */
public abstract class BaseOmnomActivity extends BaseActivity {
	private static final String TAG_MIXPANEL = MixpanelAPI.class.getSimpleName();
	private Gson mGson;

	public final MixpanelAPI getMixPanel() {
		return OmnomApplication.getMixPanel(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGson = new Gson();
	}

	protected final void track(final String eventName, final Object o) {
		final String s = mGson.toJson(o);
		try {
			System.err.println(">>> track: " + eventName + " -> " + s);
			getMixPanel().track(eventName, new JSONObject(s));
		} catch(JSONException e) {
			Log.e(TAG_MIXPANEL, "track", e);
		}
	}

	protected final void track(final String eventName, final String s) {
		try {
			System.err.println(">>> track: " + eventName + " -> " + s);
			getMixPanel().track(eventName, new JSONObject(s));
		} catch(JSONException e) {
			Log.e(TAG_MIXPANEL, "track", e);
		}
	}

	@Override
	protected void onDestroy() {
		//		getMixPanel().flush();
		super.onDestroy();
	}
}
