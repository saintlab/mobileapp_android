package com.omnom.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.omnom.android.acquiring.AcquiringType;
import com.omnom.android.acquiring.ExtraData;
import com.omnom.android.acquiring.OrderInfo;
import com.omnom.android.acquiring.PaymentInfoFactory;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.api.PaymentInfo;
import com.omnom.android.acquiring.mailru.OrderInfoMailRu;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MailRuExtra;
import com.omnom.android.acquiring.mailru.model.MerchantData;
import com.omnom.android.acquiring.mailru.model.UserData;
import com.omnom.android.acquiring.mailru.response.AcquiringPollingResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringResponse;
import com.omnom.android.acquiring.mailru.response.CardRegisterPollingResponse;

import javax.inject.Inject;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	@Inject
	protected Acquiring acquiring;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		OmnomApplication.get(this).inject(this);

		final CardInfo testCard = CardInfo.createTestCard(this);
//		testCard.setCardId("30008685803965102459");
//		acquiring.deleteCard(new MerchantData(this), UserData.createTestUser(), testCard, new Acquiring.CardDeleteListener<AcquiringResponse>() {
//			@Override
//			public void onCardDeleted(AcquiringResponse response) {
//				Log.d(TAG, ">>> url = " + response.getUrl());
//			}
//		});

				acquiring.registerCard(new MerchantData(this), UserData.createTestUser(), testCard,
				                       new Acquiring.CardRegisterListener<CardRegisterPollingResponse>() {
					                       @Override
					                       public void onCardRegistered(CardRegisterPollingResponse response) {
						                       Log.d(TAG, "status = " + response.getStatus() + " cardId = " + response.getCardId());
						                       testCard.setCardId(response.getCardId());
						                       verifyCard(testCard);
					                       }
				                       });

//		{
//			"card_id":"30008685803965102459", "url":
//			"https:\/\/test-cpg.money.mail.ru\/api\/transaction\/check\/?card_id=30008685803965102459&id=10004247505708511228&signature=169f12bc3bd574a607cf17dc850b0075def10119&status=OK_CHECK"
//		}
//		testCard.setCardId("30008685803965102459");
//		verifyCard(testCard);
	}

	private void verifyCard(final CardInfo cardInfo) {
		acquiring.verifyCard(new MerchantData(MainActivity.this), UserData.createTestUser(), cardInfo, 1.4,
		                     new Acquiring.CardVerifyListener<AcquiringResponse>() {
			                     @Override
			                     public void onCardVerified(AcquiringResponse response) {
				                     Log.d(TAG, "url = " + response.getUrl());
				                     pay(cardInfo);
			                     }
		                     });
	}

	private void pay(final CardInfo cardInfo) {
		final ExtraData extra = MailRuExtra.create(10, "test_rest_id");
		final OrderInfo order = OrderInfoMailRu.create(100, "999", "message");
		final PaymentInfo paymentInfo = PaymentInfoFactory.create(AcquiringType.MAIL_RU,
		                                                          UserData.createTestUser(), cardInfo, extra, order);

		acquiring.pay(new MerchantData(MainActivity.this), paymentInfo, new Acquiring.PaymentListener<AcquiringPollingResponse>() {
			@Override
			public void onPaymentSettled(AcquiringPollingResponse response) {
				Log.d(TAG, "status = " + response.getStatus());
			}
		});
	}
}
