package com.omnom.android.mixpanel;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.omnom.android.BuildConfig;
import com.omnom.android.auth.UserData;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.mixpanel.model.MixpanelEvent;
import com.omnom.android.mixpanel.model.UserRegisteredMixpanelEvent;
import com.omnom.android.restaurateur.model.bill.BillResponse;
import com.omnom.android.utils.utils.AndroidUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Ch3D on 03.10.2014.
 */
public class MixPanelHelper {

	public enum Project {
		ALL, OMNOM, OMNOM_ANDROID
	}

	private interface Command {
		void execute(MixpanelAPI api);
	}

	public static final String USER_ID = "ID";

	public static final String USER_NAME = "name";

	public static final String USER_NICK = "nick";

	public static final String USER_BIRTHDAY = "birth_date";

	public static final String USER_EMAIL = "email";

	public static final String USER_PHONE = "phone";

	public static final String USER_CREATED = "created";

	public static final String KEY_MIXPANEL_TIME = "time";

	public static final String KEY_DATA = "data";

	public static final String KEY_TIMESTAMP = "timestamp";

	private static final String TAG = MixPanelHelper.class.getSimpleName();

	private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	public static final SimpleDateFormat sSimpleDateFormatter = new SimpleDateFormat(TIMESTAMP_FORMAT);

	public static String formatDate(long ts) {
		return sSimpleDateFormatter.format(new Date(ts));
	}

	private final Gson mGson;

	private final boolean DEBUG = BuildConfig.DEBUG;

	private Map<Project, MixpanelAPI> mMixpanelApiMap;

	private long timeDiff = 0;

	public MixPanelHelper() {
		mMixpanelApiMap = new HashMap<Project, MixpanelAPI>();
		mGson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
		sSimpleDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	public void addApi(final Project project, final MixpanelAPI api) {
		mMixpanelApiMap.put(project, api);
	}

	public void identify(final String id) {
		execute(Project.ALL, new Command() {
			@Override
			public void execute(MixpanelAPI api) {
				api.identify(id);
			}
		});
	}

	public void setPushRegistrationId(final String registrationId) {
		execute(Project.ALL, new Command() {
			@Override
			public void execute(MixpanelAPI api) {
				api.getPeople().setPushRegistrationId(registrationId);
			}
		});
	}

	public void clearPushRegistrationId() {
		execute(Project.ALL, new Command() {
			@Override
			public void execute(MixpanelAPI api) {
				api.getPeople().clearPushRegistrationId();
			}
		});
	}

	public void track(final Project project, final String event, final Object request) {
		try {
			JSONObject json;
			if(request instanceof List) {
				json = new JSONObject();
				json.put("list", new JSONArray(mGson.toJson(request)));
			} else {
				json = new JSONObject(mGson.toJson(request));
			}
			track(project, event, json);
		} catch(JSONException e) {
			if(DEBUG) {
				Log.e(TAG, "track", e);
			}
		}
	}

	public void track(final Project project, final String event, final String data) {
		final JSONObject json = new JSONObject();
		try {
			json.put(KEY_DATA, data);
			track(project, event, json);
		} catch(JSONException e) {
			if(DEBUG) {
				Log.e(TAG, "track", e);
			}
		}
	}

	public void track(final Project project, final String event, final Object[] data) {
		final JSONObject json = new JSONObject();
		try {
			json.put(KEY_DATA, new JSONArray(mGson.toJson(data)));
			track(project, event, json);
		} catch(JSONException e) {
			if(DEBUG) {
				Log.e(TAG, "track", e);
			}
		}
	}

	public void track(final Project project, final String event, final Map data) {
		final JSONObject json = new JSONObject(data);
		try {
			json.put(KEY_DATA, json.toString());
			track(project, event, json);
		} catch(JSONException e) {
			if(DEBUG) {
				Log.e(TAG, "track", e);
			}
		}
	}

	public void track(final Project project, final MixpanelEvent event) {
		try {
			final JSONObject json = new JSONObject(mGson.toJson(event));
			track(project, event.getName(), json);
		} catch(JSONException e) {
			if(DEBUG) {
				Log.e(TAG, "track", e);
			}
		}
	}

	public void trackDevice(final Project project, final Context context) {
		execute(project, new Command() {
			@Override
			public void execute(final MixpanelAPI api) {
				api.getPeople().set("Android", Build.VERSION.SDK_INT);
				api.getPeople().set("Device", Build.MANUFACTURER + " " + Build.MODEL);
				api.getPeople().set("App", AndroidUtils.getAppVersion(context));
			}
		});
	}

	public void track(final Project project, final String event, final JSONObject json) {
		addTimestamp(json);
		execute(project, new Command() {
			@Override
			public void execute(final MixpanelAPI api) {
				api.track(event, json);
			}
		});
	}

	public void trackRevenue(final Project project, final String userId,
	                         final OrderFragment.PaymentDetails details,
	                         final BillResponse billData) {
		final int tipValue = details.getTipValue(); // already in kopeks
		final double totalAmount = details.getAmount() * 100; // translate into kopeks

		final Map<String, Number> userPayment = new HashMap<String, Number>();
		final double billSum = totalAmount - tipValue;
		userPayment.put("bill_sum", billSum);
		userPayment.put("tips_sum", tipValue);
		userPayment.put("total_sum", totalAmount);
		userPayment.put("number_of_payments", 1);

		final JSONObject json = new JSONObject();
		addTimestamp(json);

		final double revenue = (billSum * billData.getAmountCommission()) + (tipValue * billData.getTipCommission());

		execute(project, new Command() {
			@Override
			public void execute(final MixpanelAPI api) {
				api.getPeople().identify(userId);
				api.getPeople().increment(userPayment);
				api.getPeople().trackCharge(revenue, json);
			}
		});
	}

	public void setTimeDiff(final long timeDiff) {
		this.timeDiff = timeDiff;
	}

	private void addTimestamp(final JSONObject json) {
		try {
			// TODO: Use x-server-time header
			// final Long currentTime = System.currentTimeMillis();
			// final Long timestamp = currentTime + timeDiff;
			final Date date = new Date();
			json.put(KEY_MIXPANEL_TIME, date.getTime());
			json.put(KEY_TIMESTAMP, sSimpleDateFormatter.format(date));
		} catch(JSONException e) {
			if(DEBUG) {
				Log.e(TAG, "track", e);
			}
		}
	}

	public void trackUserLogin(Context context, final UserData user) {
		if(user == null || user.isNull()) {
			return;
		}
		final String id = String.valueOf(user.getId());
		trackDevice(Project.ALL, context);
		execute(Project.ALL, new Command() {
			@Override
			public void execute(final MixpanelAPI api) {
				api.identify(id);
				api.getPeople().identify(id);
				api.getPeople().set(toJson(user));
			}
		});
	}

	public void trackUserRegister(final Project project, final Context context, final UserData user) {
		if(user == null || user.isNull()) {
			return;
		}
		final String id = String.valueOf(user.getId());
		track(project, new UserRegisteredMixpanelEvent(user));
		trackDevice(Project.ALL, context);
		execute(Project.ALL, new Command() {
			@Override
			public void execute(final MixpanelAPI api) {
				api.identify(id);
				api.getPeople().identify(id);
				api.getPeople().set(toJson(user));
				api.getPeople().set(MixPanelHelper.USER_CREATED, MixPanelHelper.formatDate(System.currentTimeMillis()));
			}
		});
	}

	public void trackUserDefault(final Project project, final UserData user) {
		if(user == null || user.isNull()) {
			return;
		}
		// TODO:
		final String id = String.valueOf(user.getId());
		execute(project, new Command() {
			@Override
			public void execute(final MixpanelAPI api) {
				api.identify(id);
				api.getPeople().identify(id);
				api.getPeople().set(toJson(user));
			}
		});
	}

	public JSONObject toJson(UserData user) {
		if(user == UserData.NULL) {
			throw new RuntimeException("wrong user");
		}
		final JSONObject jsonUser = new JSONObject();
		try {
			jsonUser.put(USER_ID, user.getId());
			jsonUser.put(USER_NAME, user.getName());
			jsonUser.put(USER_NICK, user.getNick());
			jsonUser.put(USER_BIRTHDAY, user.getBirthDate());
			jsonUser.put(USER_EMAIL, user.getEmail());
			jsonUser.put(USER_PHONE, user.getPhone());
		} catch(JSONException e) {
			if(DEBUG) {
				Log.e(TAG, "toJson", e);
			}
		}
		return jsonUser;
	}

	public void flush() {
		execute(Project.ALL, new Command() {
			@Override
			public void execute(MixpanelAPI api) {
				api.flush();
			}
		});
	}

	private void execute(final Project project, final Command command) {
		if(mMixpanelApiMap == null) {
			if(DEBUG) {
				Log.w(TAG, "MixpanelApiMap is null");
			}
			return;
		}
		if(mMixpanelApiMap.isEmpty()) {
			if(DEBUG) {
				Log.w(TAG, "mMixpanelApiMap is empty");
			}
		}
		if(project == Project.ALL) {
			Collection<MixpanelAPI> mixpanelAPIs = mMixpanelApiMap.values();
			for(MixpanelAPI api : mixpanelAPIs) {
				if(api != null) {
					command.execute(api);
				} else {
					if(DEBUG) {
						Log.w(TAG, "Mixpanel api is null for project " + project.name());
					}
				}
			}
		} else {
			MixpanelAPI api = mMixpanelApiMap.get(project);
			if(api != null) {
				command.execute(api);
			} else {
				if(DEBUG) {
					Log.w(TAG, "Mixpanel API is not set for project " + project.name());
				}
			}
		}
	}

	public void trackUserLogout() {
		execute(Project.ALL, new Command() {
			@Override
			public void execute(final MixpanelAPI api) {
				api.reset();
			}
		});
	}
}
