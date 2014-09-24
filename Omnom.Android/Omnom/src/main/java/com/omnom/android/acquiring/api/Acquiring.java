package com.omnom.android.acquiring.api;

import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MerchantData;
import com.omnom.android.acquiring.mailru.model.UserData;

/**
 * Created by Ch3D on 23.09.2014.
 */
public interface Acquiring {
	public void registerCard(final MerchantData merchant, UserData user, final CardInfo cardInfo);
}

