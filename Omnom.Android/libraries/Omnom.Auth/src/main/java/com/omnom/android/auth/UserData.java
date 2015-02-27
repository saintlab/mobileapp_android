package com.omnom.android.auth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

/**
 * Created by Ch3D on 03.09.2014.
 */
public class UserData {

	public static UserData createTestUser() {
		return create(5, "89833087335");
	}

	public static UserData create(final int id, final String phone) {
		UserData userData = new UserData();
		userData.id = id;
		userData.phone = phone;
		return userData;
	}

	@Expose
	private int id;

	@Expose
	private String name;

	@Expose
	private String nick;

	@Expose
	private String email;

	@Expose
	private String phone;

	@SerializedName("birth_date")
	@Expose
	private String birthDate;

	public int getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public void storeLogin(HashMap<String, String> params) {
		params.put("user_login", String.valueOf(getId()));
	}

	public void storePhone(HashMap<String, String> params) {
		params.put("user_phone", getPhone());
	}

	@Override
	public String toString() {
		return "UserData{" +
				"id=" + id +
				", name='" + name + '\'' +
				", nick='" + nick + '\'' +
				", email='" + email + '\'' +
				", phone='" + phone + '\'' +
				", birthDate='" + birthDate + '\'' +
				'}';
	}
}
