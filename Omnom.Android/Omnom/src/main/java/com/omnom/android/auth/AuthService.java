package com.omnom.android.auth;

import com.omnom.android.auth.request.AuthRegisterRequest;
import com.omnom.android.auth.response.AuthRegisterResponse;
import com.omnom.android.auth.response.AuthResponse;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by Ch3D on 25.09.2014.
 */
public interface AuthService {
	@FormUrlEncoded
	@POST("/authorization")
	public Observable<AuthResponse> authorizePhone(@Field(Protocol.FIELD_PHONE) String phone,
	                                               @Field(Protocol.FIELD_CODE) String code);

	@FormUrlEncoded
	@POST("/authorization")
	public Observable<AuthResponse> authorizeEmail(@Field(Protocol.FIELD_EMAIL) String email,
	                                               @Field(Protocol.FIELD_CODE) String code);

	@FormUrlEncoded
	@POST("/logout")
	public Observable<AuthResponse> logout(@Field(Protocol.FIELD_TOKEN) String token);

	@POST("/register")
	public Observable<AuthRegisterResponse> register(@Body AuthRegisterRequest request);

	@FormUrlEncoded
	@POST("/confirm/phone")
	public Observable<AuthResponse> confirm(@Field(Protocol.FIELD_PHONE) String phone, @Field(Protocol.FIELD_CODE) String code);
}
