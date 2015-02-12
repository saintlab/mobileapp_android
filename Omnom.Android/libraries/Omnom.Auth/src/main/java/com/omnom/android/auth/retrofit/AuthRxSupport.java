package com.omnom.android.auth.retrofit;

import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.protocol.Protocol;

import retrofit.RequestInterceptor;
import retrofit.ResponseWrapper;
import retrofit.RxSupport;
import retrofit.client.Header;
import retrofit.client.Response;

/**
 * Created by mvpotter on 12/5/2014.
 */
public class AuthRxSupport extends RxSupport {

	public AuthRxSupport() {
		super();
	}

	public AuthRxSupport(RequestInterceptor requestInterceptor) {
		super(requestInterceptor);
	}

	@Override
	protected Object modifyResponse(ResponseWrapper wrapper) {
		Response response = wrapper.getResponse();
		Object body = wrapper.getResponseBody();
		String requestId = getHeader(response, Protocol.X_REQUEST_ID);
		if (requestId != null && body instanceof AuthResponse) {
			((AuthResponse) body).setRequestId(requestId);
		}
		return body;
	}

	private String getHeader(Response response, String headerName) {
		for (Header header: response.getHeaders()) {
			if (headerName.equals(header.getName())) {
				return header.getValue();
			}
		}
		return null;
	}

}
