package com.omnom.android.acquiring.api;

import com.omnom.android.acquiring.mailru.RegisterCardRequest;

/**
 * Created by Ch3D on 23.09.2014.
 */
public interface Acquiring {
	public interface RegisterCardCallback {
		public void onCardRegistered(int response, String cardId);
	}

	public void registerCard(RegisterCardRequest request);
}

