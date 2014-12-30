package com.omnom.android.restaurateur.retrofit;

import com.omnom.android.protocol.Protocol;
import com.omnom.android.restaurateur.model.ResponseBase;

import retrofit.RequestInterceptor;
import retrofit.ResponseWrapper;
import retrofit.RxSupport;
import retrofit.client.Header;
import retrofit.client.Response;

/**
 * Created by mvpotter on 12/5/2014.
 */
public class RestaurateurRxSupport extends RxSupport {

	public RestaurateurRxSupport(RequestInterceptor requestInterceptor) {
		super(requestInterceptor);
	}

	@Override
	protected Object modifyResponse(ResponseWrapper wrapper) {
		Response response = wrapper.getResponse();
		Object body = wrapper.getResponseBody();
		String requestId = getHeader(response, Protocol.X_REQUEST_ID);
		if (requestId != null && body instanceof ResponseBase) {
			((ResponseBase) body).setRequestId(requestId);
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
