package com.omnom.android.acquiring.mailru.model;

import java.util.HashMap;

/**
 * Created by Ch3D on 24.09.2014.
 */
public class UserData {
	public static UserData createTestUser() {
		return create("5", "89833087335");
	}

	public static UserData create(String login, String phone) {
		UserData userData = new UserData();
		userData.phone = phone;
		userData.id = login;
		return userData;
	}

	public static UserData create(final com.omnom.android.auth.UserData userData) {
		return create(String.valueOf(userData.getId()), userData.getPhone());
	}

	private String phone;

	private String id;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String user_phone) {
		this.phone = user_phone;
	}

	public String getId() {
		return id;
	}

	public void setLogin(String user_login) {
		this.id = user_login;
	}

	public void storeLogin(HashMap<String, String> params) {
		params.put("user_login", getId());
	}

	public void storePhone(HashMap<String, String> params) {
		params.put("user_phone", getPhone());
	}
}
