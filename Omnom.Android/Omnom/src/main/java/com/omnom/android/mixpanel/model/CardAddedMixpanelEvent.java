package com.omnom.android.mixpanel.model;

import com.google.gson.annotations.Expose;
import com.omnom.android.auth.UserData;

/**
 * Created by Ch3D on 22.12.2014.
 */
public class CardAddedMixpanelEvent extends BaseMixpanelEvent {

	public static final String EVENT_TITLE = "card_added";

	@Expose
	private final boolean scanUsed;

	@Expose
	private final boolean cardSaved;

	public CardAddedMixpanelEvent(UserData userData, final boolean scanUsed, final boolean cardSaved) {
		super(userData);
		this.scanUsed = scanUsed;
		this.cardSaved = cardSaved;
	}

	@Override
	public String getName() {
		return EVENT_TITLE;
	}
}
