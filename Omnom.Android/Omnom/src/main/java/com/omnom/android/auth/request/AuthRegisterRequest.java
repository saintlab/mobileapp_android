package com.omnom.android.auth.request;

/**
 * Created by Ch3D on 25.09.2014.
 */
public class AuthRegisterRequest {
	public static AuthRegisterRequest create(String installId, String firstName, String lastName, String nickName, String email,
	                                         String phone,
	                                         String birthDate) {
		return new AuthRegisterRequest(installId, firstName, lastName, nickName, email, phone, birthDate);
	}

	private String installId;
	private String firstName;
	private String lastName;
	private String nickName;
	private String email;
	private String phone;
	private String birthDate;

	private AuthRegisterRequest(String installId, String firstName, String lastName, String nickName, String email, String phone,
	                            String birthDate) {
		this.installId = installId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.nickName = nickName;
		this.email = email;
		this.phone = phone;
		this.birthDate = birthDate;
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
