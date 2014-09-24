package com.omnom.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MailRuExtra;
import com.omnom.android.acquiring.mailru.model.MerchantData;
import com.omnom.android.acquiring.mailru.model.PaymentInfoMailRu;
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

		acquiring.registerCard(new MerchantData(this), UserData.createTestUser(), testCard,
		                       new Acquiring.CardRegisterListener<CardRegisterPollingResponse>() {
			                       @Override
			                       public void onCardRegistered(CardRegisterPollingResponse response) {
				                       Log.d(TAG, "status = " + response.getStatus() + " cardId = " + response.getCardId());
				                       testCard.setCardId(response.getCardId());
				                       verifyCard(testCard);
			                       }
		                       });
	}

	private void verifyCard(final CardInfo cardInfo) {
		acquiring.verifyCard(new MerchantData(MainActivity.this), UserData.createTestUser(), cardInfo, 1.20,
		                     new Acquiring.CardVerifyListener<AcquiringResponse>() {
			                     @Override
			                     public void onCardVerified(AcquiringResponse response) {
				                     Log.d(TAG, "url = " + response.getUrl());
				                     pay(cardInfo);
			                     }
		                     });
	}

	private void pay(final CardInfo cardInfo) {
		final PaymentInfoMailRu paymentInfo = PaymentInfoMailRu.create(UserData.createTestUser(),
		                                                               cardInfo, MailRuExtra.create(10, "test_rest_id"),
		                                                               100, "999", "message");
		acquiring.pay(new MerchantData(MainActivity.this), paymentInfo, new Acquiring.PaymentListener<AcquiringPollingResponse>() {
			@Override
			public void onPaymentSettled(AcquiringPollingResponse response) {
				Log.d(TAG, "status = " + response.getStatus());
			}
		});
	}
}
