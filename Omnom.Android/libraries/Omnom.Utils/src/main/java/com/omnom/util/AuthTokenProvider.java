package com.omnom.util;

import android.content.Context;

/**
* Created by Ch3D on 30.09.2014.
*/
public interface AuthTokenProvider {
	public String getAuthToken();
	public Context getContext();
}
