package com.omnom.android.restaurateur.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.omnom.android.restaurateur.model.config.AcquiringData;
import com.omnom.android.restaurateur.model.config.MerchantData;

import java.lang.reflect.Type;

/**
 * Created by mvpotter on 12/3/2014.
 */
public class MailRuSerializer implements JsonSerializer<AcquiringData>, JsonDeserializer<AcquiringData> {

	private static final String BASE_URL_PROPERTY = "OMNMailRuAcquiringBaseURL";
	private static final String TEST_CVV_PROPERTY = "OMNMailRuTestCVV";
	private static final String CARD_HOLDER_PROPERTY = "OMNMailRu_cardholder";
	private static final String SECRET_KEY_PROPERTY = "OMNMailRu_secret_key";
	private static final String MERCH_ID_PROPERTY = "OMNMailRu_merch_id";
	private static final String VTERM_ID_PROPERTY = "OMNMailRu_vterm_id";

	@Override
	public JsonElement serialize(AcquiringData src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject mailRu = new JsonObject();
		mailRu.addProperty(BASE_URL_PROPERTY, src.getBaseUrl());
		mailRu.addProperty(TEST_CVV_PROPERTY, src.getTestCvv());
		mailRu.addProperty(CARD_HOLDER_PROPERTY, src.getCardHolder());
		mailRu.addProperty(SECRET_KEY_PROPERTY, src.getSecretKey());
		MerchantData merchantData = src.getMerchantData();
		if (merchantData != null) {
			mailRu.addProperty(MERCH_ID_PROPERTY, merchantData.getMerchId());
			mailRu.addProperty(VTERM_ID_PROPERTY, merchantData.getVtermId());
		}
		return mailRu;
	}


	@Override
	public AcquiringData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject mailRuObj = (JsonObject) json;
		String baseUrl = mailRuObj.get(BASE_URL_PROPERTY).getAsString();
		String testCvv = mailRuObj.get(TEST_CVV_PROPERTY).getAsString();
		String cardHolder = mailRuObj.get(CARD_HOLDER_PROPERTY).getAsString();
		String secretKey = mailRuObj.get(SECRET_KEY_PROPERTY).getAsString();
		MerchantData merchantData = new MerchantData(mailRuObj.get(MERCH_ID_PROPERTY).getAsString(),
													 mailRuObj.get(VTERM_ID_PROPERTY).getAsString());
		return new AcquiringData(baseUrl, testCvv, cardHolder, secretKey, merchantData);
	}

}
