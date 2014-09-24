package com.omnom.android.acquiring.mailru.model;

/**
 * Created by Ch3D on 24.09.2014.
 */
public class UserData {
	private String phone;
	private String id;

	public static UserData createTestUser() {
		return create("5", "89833087335");
	}

	public static UserData create(String login, String phone) {
		UserData userData = new UserData();
		userData.phone = phone;
		userData.id = login;
		return userData;
	}

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
}
