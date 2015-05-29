package com.omnom.android.auth;

import com.omnom.android.auth.request.AuthRegisterRequest;
import com.omnom.android.auth.response.AuthRegisterResponse;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.auth.response.UserResponse;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
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

	@POST("/logout")
	public Observable<AuthResponse> logout(@Header(com.omnom.android.protocol.Protocol.HEADER_AUTH_TOKEN) String token);

	@POST("/register")
	public Observable<AuthRegisterResponse> register(@Body AuthRegisterRequest request);

	@FormUrlEncoded
	@POST("/confirm/phone")
	public Observable<AuthResponse> confirm(@Field(Protocol.FIELD_PHONE) String phone,
	                                        @Field(Protocol.FIELD_CODE) String code);

	@FormUrlEncoded
	@POST("/confirm/phone/resend")
	public Observable<AuthResponse> confirmResend(@Field(Protocol.FIELD_PHONE) String phone);

	@GET("/user")
	public Observable<UserResponse> getUser(@Header(com.omnom.android.protocol.Protocol.HEADER_AUTH_TOKEN) String token);

	@FormUrlEncoded
	@POST("/user/geo")
	public Observable<AuthResponse> logLocation(@Field(Protocol.FIELD_LONGITUDE) double longitude,
	                                            @Field(Protocol.FIELD_LATITUDE) double latitude,
	                                            @Field(Protocol.FIELD_TOKEN) String token);

	@FormUrlEncoded
	@POST("/login/simple")
	Observable<AuthResponse> authenticate(@Field(Protocol.FIELD_LOGIN) String username,
	                                      @Field(Protocol.FIELD_PASSWORD) String password);

	@FormUrlEncoded
	@POST("/recover")
	Observable<AuthResponse> remindPassword(@Field(Protocol.FIELD_EMAIL) String email);

	@FormUrlEncoded
	@POST("/recover")
	Observable<AuthResponse> changePhone(@Field(Protocol.FIELD_PHONE) String phone);

	@FormUrlEncoded
	@POST("/user")
	public Observable<UserResponse> updateUser(@Field(Protocol.FIELD_TOKEN) String token, @Field(Protocol.FIELD_NAME) String name,
	                                           @Field(Protocol.FIELD_EMAIL) String email, @Field(Protocol.FIELD_BIRTH) String birth,
	                                           @Field(Protocol.FIELD_AVATAR) String avaUrl);
}
