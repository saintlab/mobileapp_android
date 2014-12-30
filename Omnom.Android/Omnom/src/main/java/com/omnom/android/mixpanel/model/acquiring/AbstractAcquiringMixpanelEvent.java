package com.omnom.android.mixpanel.model.acquiring;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.response.AcquiringResponseError;
import com.omnom.android.auth.UserData;
import com.omnom.android.mixpanel.model.AbstractBaseMixpanelEvent;
import com.omnom.android.utils.CardUtils;
import com.omnom.android.utils.utils.StringUtils;

/**
 * Created by mvpotter on 12/29/2014.
 */
abstract class AbstractAcquiringMixpanelEvent extends AbstractBaseMixpanelEvent {

	private transient String title;

	@Expose
	protected final String cardId;

	@Expose
	protected final String pan;

	@Expose
	private final String errorCode;

	@Expose
	private final String errorDescr;

	public AbstractAcquiringMixpanelEvent(final @Nullable UserData userData, final CardInfo cardInfo) {
		this(userData, cardInfo, null);
	}

	public AbstractAcquiringMixpanelEvent(final @Nullable UserData userData, final CardInfo cardInfo,
	                                      final AcquiringResponseError error) {
		super(userData);
		this.cardId = StringUtils.EMPTY_STRING.equals(cardInfo.getCardId()) ? null : cardInfo.getCardId();
		this.pan = StringUtils.EMPTY_STRING.equals(cardInfo.getPan()) ? null : CardUtils.maskPan(cardInfo.getPan());
		if (error != null) {
			title = getFailName();
			this.errorCode = error.getCode();
			this.errorDescr = error.getDescr();
		} else {
			title = getSuccessName();
			this.errorCode = null;
			this.errorDescr = null;
		}
	}

	@Override
	public String getName() {
		return title;
	}

	abstract String getSuccessName();

	abstract String getFailName();

}
