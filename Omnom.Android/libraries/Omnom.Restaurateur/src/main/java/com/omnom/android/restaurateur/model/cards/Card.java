package com.omnom.android.restaurateur.model.cards;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ch3D on 27.10.2014.
 */
public class Card {

	public static final String STATUS_REGISTERED = "registered";

	@Expose
	private String brand;

	@Expose
	private String issuer;

	@Expose
	private String issuerPhone;

	@Expose
	private String issuerWebsite;

	@Expose
	private int userId;

	@Expose
	private String externalCardId;

	@Expose
	private String maskedPan;

	@Expose
	@SerializedName("masked_pan_6_4")
	private String maskedPanMixpanel;

	@Expose
	private String confirmedBy;

	@Expose
	private String status;

	@Expose
	private String createdAt;

	@Expose
	private String updatedAt;

	@Expose
	private int id;

	@Expose
	private String association;

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getIssuerPhone() {
		return issuerPhone;
	}

	public void setIssuerPhone(String issuerPhone) {
		this.issuerPhone = issuerPhone;
	}

	public String getIssuerWebsite() {
		return issuerWebsite;
	}

	public void setIssuerWebsite(String issuerWebsite) {
		this.issuerWebsite = issuerWebsite;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getExternalCardId() {
		return externalCardId;
	}

	public void setExternalCardId(String externalCardId) {
		this.externalCardId = externalCardId;
	}

	public String getMaskedPan() {
		return maskedPan;
	}

	public void setMaskedPan(String maskedPan) {
		this.maskedPan = maskedPan;
	}

	public String getMaskedPanMixpanel() {
		return maskedPanMixpanel;
	}

	public void setMaskedPanMixpanel(String maskedPanMixpanel) {
		this.maskedPanMixpanel = maskedPanMixpanel;
	}

	public String getConfirmedBy() {
		return confirmedBy;
	}

	public void setConfirmedBy(String confirmedBy) {
		this.confirmedBy = confirmedBy;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAssociation() {
		return association;
	}

	public void setAssociation(String association) {
		this.association = association;
	}

	public boolean isRegistered() {
		return STATUS_REGISTERED.equals(status);
	}
}
