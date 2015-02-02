package com.omnom.android.mixpanel.model.acquiring;

import com.google.gson.annotations.Expose;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.response.AcquiringResponseError;
import com.omnom.android.auth.UserData;

/**
 * Created by Ch3D on 22.12.2014.
 */
public class CardAddedMixpanelEvent extends AbstractAcquiringMixpanelEvent {

	public static final String EVENT_TITLE = "card_added";
	public static final String FAIL_EVENT_TITLE = "card_added_fail";

	@Expose
	private final boolean scannerUsed;

	@Expose
	private final boolean cardSaved;

	public CardAddedMixpanelEvent(final UserData userData, final CardInfo cardInfo,
	                              final boolean scannerUsed) {
		this(userData, cardInfo, scannerUsed, null);
	}

	public CardAddedMixpanelEvent(final UserData userData, final CardInfo cardInfo,
	                              final boolean scannerUsed, final AcquiringResponseError error) {
		super(userData, cardInfo, error);
		this.scannerUsed = scannerUsed;
		this.cardSaved = error == null;
	}


	@Override
	String getSuccessName() {
		return EVENT_TITLE;
	}

	@Override
	String getFailName() {
		return FAIL_EVENT_TITLE;
	}

}
