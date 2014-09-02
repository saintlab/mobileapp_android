package com.omnom.android.linker.utils;

/**
* Created by Ch3D on 02.09.2014.
*/
public class UserDataHolder {
	public static UserDataHolder create(String user, String pass) {
		final UserDataHolder dataHolder = new UserDataHolder();
		dataHolder.username = user;
		dataHolder.password = pass;
		return dataHolder;
	}

	private String username;
	private String password;

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}
}
