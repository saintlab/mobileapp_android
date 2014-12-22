package com.omnom.android.mixpanel.model;

import com.omnom.android.auth.UserData;

/**
 * Created by mvpotter on 21/11/14.
 */
public interface MixpanelEvent {
	UserData getUser();

	String getName();
}
