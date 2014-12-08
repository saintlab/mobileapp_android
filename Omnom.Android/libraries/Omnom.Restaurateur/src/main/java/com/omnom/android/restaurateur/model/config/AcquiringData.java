package com.omnom.android.restaurateur.model.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mvpotter on 12/3/2014.
 */
public class AcquiringData {

	@Expose
	private String baseUrl;

	@Expose
	private String testCvv;

	@Expose
	private String cardHolder;

	@Expose
	private String secretKey;

	@Expose
	private MerchantData merchantData;

	public AcquiringData(String baseUrl, String testCvv, String cardHolder, String secretKey, MerchantData merchantData) {
		this.baseUrl = baseUrl;
		this.testCvv = testCvv;
		this.cardHolder = cardHolder;
		this.secretKey = secretKey;
		this.merchantData = merchantData;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getTestCvv() {
		return testCvv;
	}

	public String getCardHolder() {
		return cardHolder;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public MerchantData getMerchantData() {
		return merchantData;
	}

}
