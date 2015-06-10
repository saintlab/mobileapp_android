package com.omnom.android.auth;

import com.omnom.android.auth.request.AuthRegisterRequest;
import com.omnom.android.auth.request.UserAuthLoginPassRequest;
import com.omnom.android.auth.request.UserAuthorizeByPhoneRequest;
import com.omnom.android.auth.request.UserAuthorizeEmailByRequest;
import com.omnom.android.auth.request.UserConfirmPhoneRequest;
import com.omnom.android.auth.request.UserLogLocationRequest;
import com.omnom.android.auth.request.UserPhoneConfirmResendRequest;
import com.omnom.android.auth.request.UserRecoverPhoneRequest;
import com.omnom.android.auth.request.UserRemindEmailRequest;
import com.omnom.android.auth.request.UserUpdateRequest;
import com.omnom.android.auth.response.AuthRegisterResponse;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.auth.response.UserResponse;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by Ch3D on 25.09.2014.
 */
public interface AuthService {
	@POST("/authorization")
	public Observable<AuthResponse> authorizePhone(@Body UserAuthorizeByPhoneRequest userAuthorizeByPhoneRequest);

	@POST("/authorization")
	public Observable<AuthResponse> authorizeEmail(@Body UserAuthorizeEmailByRequest request);

	@POST("/logout")
	public Observable<AuthResponse> logout(@Header(com.omnom.android.protocol.Protocol.HEADER_AUTH_TOKEN) String token);

	@POST("/login")
	public Observable<AuthRegisterResponse> register(@Body AuthRegisterRequest request);

	@POST("/confirm/phone")
	public Observable<AuthResponse> confirm(@Body UserConfirmPhoneRequest userConfirmPhoneRequest);

	@POST("/confirm/phone/resend")
	public Observable<AuthResponse> confirmResend(@Body UserPhoneConfirmResendRequest userPhoneConfirmResendRequest);

	@GET("/user")
	public Observable<UserResponse> getUser(@Header(com.omnom.android.protocol.Protocol.HEADER_AUTH_TOKEN) String token);

	@POST("/user/geo")
	public Observable<AuthResponse> logLocation(@Body UserLogLocationRequest userLogLocationRequest);

	@POST("/login/simple")
	Observable<AuthResponse> authenticate(@Body UserAuthLoginPassRequest userAuthLoginPassRequest);

	@POST("/recover")
	Observable<AuthResponse> remindPassword(@Body UserRemindEmailRequest userRemindEmailRequest);

	@POST("/recover")
	Observable<AuthResponse> changePhone(@Body UserRecoverPhoneRequest userRecoverPhoneRequest);

	@POST("/user")
	public Observable<UserResponse> updateUser(@Body UserUpdateRequest userUpdateRequest);
}
