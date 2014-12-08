package com.omnom.android.utils;

import com.omnom.android.auth.UserData;
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
}