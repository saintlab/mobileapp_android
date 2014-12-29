package com.omnom.android.mixpanel.model.acquiring;

import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.response.AcquiringResponseError;
import com.omnom.android.auth.UserData;

/**
 * Created by Ch3D on 22.12.2014.
 */
public class CardDeletedMixpanelEvent extends AbstractAcquiringMixpanelEvent {

	public static final String EVENT_TITLE = "card_deleted";
	public static final String FAIL_EVENT_TITLE = "card_deleted_fail";

	public CardDeletedMixpanelEvent(final UserData userData, final CardInfo cardInfo) {
		this(userData, cardInfo, null);
	}

	public CardDeletedMixpanelEvent(final UserData userData, final CardInfo cardInfo,
	                                final AcquiringResponseError error) {
		super(userData, cardInfo, error);
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
