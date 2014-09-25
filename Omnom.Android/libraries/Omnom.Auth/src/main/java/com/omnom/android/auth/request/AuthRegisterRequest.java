package com.omnom.android.auth.request;

/**
 * Created by Ch3D on 25.09.2014.
 */
public class AuthRegisterRequest {
	public static AuthRegisterRequest create(String installId, String name, String nick, String email,
	                                         String phone,
	                                         String birthDate) {
		return new AuthRegisterRequest(installId, name, nick, email, phone, birthDate);
	}

	private String installId;
	private String name;
	private String nick;
	private String email;
	private String phone;
	private String birthDate;

	private AuthRegisterRequest(String installId, String name, String nick, String email, String phone,
	                            String birthDate) {
		this.installId = installId;
		this.name = name;
		this.nick = nick;
		this.email = email;
		this.phone = phone;
		this.birthDate = birthDate;
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

	public String getInstallId() {

		return installId;
	}

	public void setInstallId(String installId) {
		this.installId = installId;
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
