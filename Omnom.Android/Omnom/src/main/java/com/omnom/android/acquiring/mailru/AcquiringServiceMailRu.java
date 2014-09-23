package com.omnom.android.acquiring.mailru;

import com.omnom.android.acquiring.mailru.response.AcquiringResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringTransactionExtendedResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringTransactionResponse;

import java.util.HashMap;

import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by Ch3D on 23.09.2014.
 */
public interface AcquiringServiceMailRu {
	@FormUrlEncoded
	@POST("/order/pay")
	public Observable<AcquiringResponse> pay(@FieldMap HashMap<String, String> params);

	@FormUrlEncoded
	@POST("/card/register")
	public Observable<AcquiringResponse> registerCard(@FieldMap HashMap<String, String> params);

	@FormUrlEncoded
	@POST("/card/verify")
	public Observable<AcquiringResponse> verifyCard(@FieldMap HashMap<String, String> params);

	@FormUrlEncoded
	@POST("/card/delete")
	public Observable<AcquiringResponse> deleteCard(@FieldMap HashMap<String, String> params);

	@FormUrlEncoded
	@POST("/transaction/check")
	public Observable<AcquiringTransactionResponse> checkTransaction(@FieldMap HashMap<String, String> params);

	@FormUrlEncoded
	@POST("/transaction/extcheck")
	public Observable<AcquiringTransactionExtendedResponse> checkTransactionExtended(@FieldMap HashMap<String, String> params);
}
