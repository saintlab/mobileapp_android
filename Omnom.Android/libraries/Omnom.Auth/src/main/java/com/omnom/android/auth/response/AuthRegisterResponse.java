package com.omnom.android.auth.response;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 25.09.2014.
 */
public class AuthRegisterResponse extends AuthResponse {
	@Expose
	private String installId;
	@Expose
	private String firstName;
	@Expose
	private String lastName;
	@Expose
	private String nickName;
	@Expose
	private String email;
	@Expose
	private String phone;
	@Expose
	private String birthDate;
	@Expose
	private String createdAt;
	@Expose
	private String updatedAt;
	@Expose
	private String id;
	@Expose
	private boolean emailValidated;
	@Expose
	private boolean phoneValidated;

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String created_at) {
		this.createdAt = created_at;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isEmailValidated() {
		return emailValidated;
	}

	public void setEmailValidated(boolean emailValidated) {
		this.emailValidated = emailValidated;
	}

	public boolean isPhoneValidated() {
		return phoneValidated;
	}

	public void setPhoneValidated(boolean phoneValidated) {
		this.phoneValidated = phoneValidated;
	}

	public String getInstallId() {

		return installId;
	}

	public void setInstallId(String installId) {
		this.installId = installId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
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
}
