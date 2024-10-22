package com.omnom.android.mixpanel.model.acquiring;

import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.response.AcquiringResponseError;
import com.omnom.android.auth.UserData;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.mixpanel.model.SplitWay;
import com.omnom.android.mixpanel.model.TipsWay;
import com.omnom.android.utils.utils.AmountHelper;

/**
 * Created by Ch3D on 22.12.2014.
 */
public class PaymentMixpanelEvent extends AbstractAcquiringMixpanelEvent {

	public static final String EVENT_TITLE = "payment_success";
	public static final String FAIL_EVENT_TITLE = "ERROR_MAIL_CARD_PAY";

	private final String orderId;

	private final String tableId;

	private final String restaurantId;

	private final int tipsSum;

	private final String tipsWay;

	private final String split;

	private final double billSum;

	private final int percent;

	private final int totalAmount;

	private final int billId;

	public PaymentMixpanelEvent(UserData userData, final OrderFragment.PaymentDetails details,
	                            final int billId, final CardInfo cardInfo) {
		this(userData, details, billId, cardInfo, null);
	}

	public PaymentMixpanelEvent(UserData userData, final OrderFragment.PaymentDetails details,
	                            final int billId, final CardInfo cardInfo, final AcquiringResponseError error) {
		super(userData, cardInfo, error);
		this.billId = billId;
		orderId = details.getOrderId();
		tableId = details.getTableId();
		restaurantId = details.getRestaurantName();
		billSum = AmountHelper.toInt(details.getAmount()) - details.getTip();
        tipsSum = details.getTip();
		percent = details.getTipValue();
		tipsWay = TipsWay.values()[details.getTipsWay()].name().toLowerCase();
		split = SplitWay.values()[details.getSplitWay()].name().toLowerCase();
		totalAmount = AmountHelper.toInt(details.getAmount());
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
