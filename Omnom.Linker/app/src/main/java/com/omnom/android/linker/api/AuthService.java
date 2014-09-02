package com.omnom.android.linker.api;

import com.omnom.android.linker.model.UserProfile;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Ch3D on 02.09.2014.
 */
public interface AuthService {
	@FormUrlEncoded
	@POST("/login")
	Observable<String> authenticate(@Field(Protocol.FIELD_LOGIN) String username,
	                                @Field(Protocol.FIELD_PASSWORD) String password);

	@FormUrlEncoded
	@POST("/recover")
	Observable<String> remindPassword(@Field(Protocol.FIELD_EMAIL) String email);

	@GET("/user")
	Observable<UserProfile> getUserProfile(@Query(Protocol.HEADER_AUTH_TOKEN) String authToken);
}
