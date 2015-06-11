package com.omnom.android.auth;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.omnom.android.utils.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by Ch3D on 03.09.2014.
 */
public class UserData implements Parcelable {

	public static final UserData NULL = new UserData(-1, StringUtils.EMPTY_STRING, StringUtils.EMPTY_STRING, StringUtils.EMPTY_STRING,
	                                                 StringUtils.EMPTY_STRING, StringUtils.EMPTY_STRING, StringUtils.EMPTY_STRING);

	public static final Creator<UserData> CREATOR = new Creator<UserData>() {
		@Override
		public UserData createFromParcel(Parcel in) {
			return new UserData(in);
		}

		@Override
		public UserData[] newArray(int size) {
			return new UserData[size];
		}
	};

	public static UserData createDemoUser(String name) {
		return new UserData(-1, name, StringUtils.EMPTY_STRING, StringUtils.EMPTY_STRING,
		                    StringUtils.EMPTY_STRING, StringUtils.EMPTY_STRING, StringUtils.EMPTY_STRING);
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

	@Expose
	private String avatar;

	@SerializedName("birth_date")
	@Expose
	private String birthDate;

	public UserData() {

	}

	public UserData(final int id,
	                final String name,
	                final String nick,
	                final String email,
	                final String phone,
	                final String avatar,
	                final String birthDate) {
		this.id = id;
		this.name = name;
		this.nick = nick;
		this.email = email;
		this.phone = phone;
		this.avatar = avatar;
		this.birthDate = birthDate;
	}

	private UserData(Parcel in) {
		id = in.readInt();
		name = in.readString();
		nick = in.readString();
		email = in.readString();
		phone = in.readString();
		avatar = in.readString();
		birthDate = in.readString();
	}

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

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(nick);
		dest.writeString(email);
		dest.writeString(phone);
		dest.writeString(avatar);
		dest.writeString(birthDate);
	}
}
