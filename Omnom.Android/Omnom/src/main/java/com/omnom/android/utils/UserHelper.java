package com.omnom.android.utils;

import android.content.Context;

import com.omnom.android.OmnomApplication;
import com.omnom.android.auth.UserData;
import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.restaurateur.model.order.User;

/**
 * Created by Ch3D on 04.12.2014.
 */
public class UserHelper {
	public static User toPaymentUser(final UserData userData) {
		User user = new User();
		user.setId(userData.getId());
		user.setName(userData.getName());
		return user;
	}

	public static UserData getUserData(final Context context) {
		final UserProfile userProfile = OmnomApplication.get(context).getUserProfile();
		return userProfile != null ? userProfile.getUser() : null;
	}
}