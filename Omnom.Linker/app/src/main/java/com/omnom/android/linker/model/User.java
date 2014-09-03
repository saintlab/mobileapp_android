package com.omnom.android.linker.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ch3D on 03.09.2014.
 */
public class User {
	@Expose
	private Integer id;

	@Expose
	private String name;

	@Expose
	private String nick;

	@Expose
	private String email;

	@Expose
	private Integer phone;

	@SerializedName("birth_date")
	@Expose
	private Object birthDate;
	
	@Expose
	private List<Object> groups = new ArrayList<Object>();

	public Integer getId() {
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

	public Integer getPhone() {
		return phone;
	}

	public void setPhone(Integer phone) {
		this.phone = phone;
	}

	public Object getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Object birthDate) {
		this.birthDate = birthDate;
	}

	public List<Object> getGroups() {
		return groups;
	}

	public void setGroups(List<Object> groups) {
		this.groups = groups;
	}
}
