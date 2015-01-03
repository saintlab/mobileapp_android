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
import java.util.Collection;
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

	private Map<Project, MixpanelAPI> mMixpanelApiMap;

	private Long timeDiff = 0L;

	public MixPanelHelper(Map<Project, MixpanelAPI> apiMap) {
		mMixpanelApiMap = apiMap;
		mGson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
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
			final JSONObject json = new JSONObject(mGson.toJson(request));
			track(project, event, json);
		} catch(JSONException e) {
			Log.e(TAG, "track", e);
		}
	}

	public void track(final Project project, final String event, final Map data) {
		final JSONObject json = new JSONObject(data);
		try {
			json.put(KEY_DATA, json.toString());
			track(project, event, json);
		} catch(JSONException e) {
			Log.e(TAG, "track", e);
		}
	}

	public void track(final Project project, final MixpanelEvent event) {
		try {
			final JSONObject json = new JSONObject(mGson.toJson(event));
			track(project, event.getName(), json);
		} catch(JSONException e) {
			Log.e(TAG, "track", e);
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

	public void trackUserLogin(final Project project, Context context, UserData user) {
		if(user == null) {
			return;
		}
		final String id = String.valueOf(user.getId());
		trackDevice(Project.ALL, context);
		execute(Project.ALL, new Command() {
			@Override
			public void execute(final MixpanelAPI api) {
				api.identify(id);
				api.getPeople().identify(id);
				api.getPeople().initPushHandling(MixPanelHelper.MIXPANEL_PUSH_ID);
			}
		});
	}

	public void trackUserRegister(final Project project, final Context context, final UserData user) {
		if(user == null) {
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
				api.getPeople().initPushHandling(MixPanelHelper.MIXPANEL_PUSH_ID);
			}
		});
	}

	public void trackUserDefault(final Project project, final Context context, final UserData user) {
		if(user == null) {
			return;
		}
		// TODO:
		final String id = String.valueOf(user.getId());
		execute(project, new Command() {
			@Override
			public void execute(final MixpanelAPI api) {
				api.identify(id);
				api.getPeople().identify(id);
				api.getPeople().initPushHandling(MixPanelHelper.MIXPANEL_PUSH_ID);
			}
		});
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

	public void flush() {
		execute(Project.ALL, new Command() {
			@Override
			public void execute(MixpanelAPI api) {
				api.flush();
			}
		});
	}

	private void execute(final Project project, final Command command) {
		if (mMixpanelApiMap == null || mMixpanelApiMap.isEmpty()) {
			Log.w(TAG, "mMixpanelApiMap is not set");
			return;
		}
		if (project == Project.ALL) {
			Collection<MixpanelAPI> mixpanelAPIs = mMixpanelApiMap.values();
			for (MixpanelAPI api: mixpanelAPIs) {
				if (api != null) {
					command.execute(api);
				} else {
					Log.w(TAG, "Mixpanel api is null for project " + project.name());
				}
			}
		} else {
			MixpanelAPI api = mMixpanelApiMap.get(project);
			if (api != null) {
				command.execute(api);
			} else {
				Log.w(TAG, "Mixpanel API is not set for project " + project.name());
			}
		}
	}

	private interface Command {
		void execute(MixpanelAPI api);
	}

	public static enum Project {
		ALL, OMNOM, OMNOM_ANDROID
	}

}
