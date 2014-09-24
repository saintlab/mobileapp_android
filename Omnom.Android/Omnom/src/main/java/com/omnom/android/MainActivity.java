package com.omnom.android;

import android.app.Activity;
import android.os.Bundle;

import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.mailru.AcquiringMailRu;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MailRuExtra;
import com.omnom.android.acquiring.mailru.model.MerchantData;
import com.omnom.android.acquiring.mailru.model.PaymentInfo;
import com.omnom.android.acquiring.mailru.model.UserData;
import com.omnom.android.acquiring.mailru.response.AcquiringPollingResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringResponse;

public class MainActivity extends Activity {

	private AcquiringMailRu acquiring;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		acquiring = new AcquiringMailRu(this);
		final CardInfo testCard = CardInfo.createTestCard(this);

		acquiring.registerCard(new MerchantData(this), UserData.createTestUser(), testCard,
		                       new Acquiring.CardRegisterListener() {
			                       @Override
			                       public void onCardRegistered(String status, String cardId) {
				                       System.err.println("status = " + status + " cardId = " + cardId);
				                       testCard.setCardId(cardId);
				                       verifyCard(testCard);
			                       }
		                       });

	}

	private void verifyCard(final CardInfo cardInfo) {
		acquiring.verifyCard(new MerchantData(MainActivity.this), UserData.createTestUser(), cardInfo, 1.20,
		                     new Acquiring.CardVerifyListener() {
			                     @Override
			                     public void onCardVerified(AcquiringResponse response) {
				                     System.err.println("status = " + response.getUrl());
				                     pay(cardInfo);
			                     }
		                     });
	}

	private void pay(final CardInfo cardInfo) {
		final PaymentInfo paymentInfo = PaymentInfo.create(UserData.createTestUser(),
		                                                   cardInfo, MailRuExtra.create(10, "test_rest_id"),
		                                                   100, "999", "message");

		acquiring.pay(new MerchantData(MainActivity.this), paymentInfo, new Acquiring.PaymentListener() {
			@Override
			public void onPaymentSettled(AcquiringPollingResponse response) {
				System.err.println("status = " + response.getStatus());
			}
		});
	}
}
