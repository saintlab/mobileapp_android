package com.omnom.android.acquiring.mailru;

import com.omnom.android.acquiring.mailru.response.AcquiringResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringTransactionResponse;

import java.util.HashMap;

import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by Ch3D on 23.09.2014.
 */
public interface AcquiringServiceMailRu {
	@FormUrlEncoded
	@POST("/order/pay")
	public AcquiringResponse pay(@FieldMap HashMap<String, String> params);

	@FormUrlEncoded
	@POST("/card/register")
	public AcquiringResponse registerCard(@FieldMap HashMap<String, String> params);

	@FormUrlEncoded
	@POST("/card/verify")
	public AcquiringResponse verifyCard(@FieldMap HashMap<String, String> params);

	@FormUrlEncoded
	@POST("/card/delete")
	public AcquiringResponse deleteCard(@FieldMap HashMap<String, String> params);

	@FormUrlEncoded
	@POST("/transaction/check")
	public AcquiringTransactionResponse checkTransaction(@FieldMap HashMap<String, String> params);

	@FormUrlEncoded
	@POST("/transaction/extcheck")
	public AcquiringTransactionResponse checkTransactionExtended(@FieldMap HashMap<String, String> params);
}
