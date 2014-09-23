package com.omnom.android.acquiring.api;

import java.util.HashMap;

/**
 * Created by Ch3D on 23.09.2014.
 */
public interface Acquiring {
	public interface RegisterCardCallback {
		public void onCardRegistered(int response, String cardId);
	}

	public void registerCard(HashMap<String, String> cardInfo, String user_login, String user_phone);
}

