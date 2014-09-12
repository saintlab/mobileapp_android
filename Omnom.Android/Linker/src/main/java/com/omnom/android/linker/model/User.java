package com.omnom.android.linker.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ch3D on 03.09.2014.
 */
public class User {
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

	@Expose
	private Object groups;

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

	public Object getGroups() {
		return groups;
	}

	public void setGroups(Object groups) {
		this.groups = groups;
	}
}
