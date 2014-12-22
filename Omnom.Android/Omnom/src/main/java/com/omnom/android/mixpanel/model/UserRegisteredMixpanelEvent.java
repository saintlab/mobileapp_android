package com.omnom.android.mixpanel.model;

import com.google.gson.annotations.Expose;
import com.omnom.android.BuildConfig;
import com.omnom.android.auth.UserData;

/**
 * Created by xCh3Dx on 20.12.2014.
 */
public class UserRegisteredMixpanelEvent extends BaseMixpanelEvent {

	public static final String EVENT_NAME = "user_registered";

	@Expose
	public final int id;

	@Expose
	public final String name;

	@Expose
	public final String phone;

	@Expose
	public final String email;

	public UserRegisteredMixpanelEvent(UserData user) {
		super(user);
		if(BuildConfig.DEBUG) {
			if(user == null) {
				throw new RuntimeException("User cannot be null!");
			}
		}
		id = user.getId();
		name = user.getName();
		phone = user.getPhone();
		email = user.getEmail();
	}

	@Override
	public String getName() {
		return EVENT_NAME;
	}
}
