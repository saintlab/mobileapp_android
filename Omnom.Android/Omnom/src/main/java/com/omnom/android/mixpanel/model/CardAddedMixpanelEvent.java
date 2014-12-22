package com.omnom.android.mixpanel.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 22.12.2014.
 */
public class CardAddedMixpanelEvent implements MixpanelEvent {

	public static final String EVENT_TITLE = "card_added";

	@Expose
	private final boolean scanUsed;

	@Expose
	private final boolean cardSaved;

	public CardAddedMixpanelEvent(final boolean scanUsed, final boolean cardSaved) {
		this.scanUsed = scanUsed;
		this.cardSaved = cardSaved;
	}

	@Override
	public String getName() {
		return EVENT_TITLE;
	}
}
