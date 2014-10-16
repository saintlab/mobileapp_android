package com.omnom.android.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.omnom.android.R;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MerchantData;
import com.omnom.android.acquiring.mailru.model.UserData;
import com.omnom.android.acquiring.mailru.response.CardRegisterPollingResponse;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.request.AuthRegisterRequest;
import com.omnom.android.auth.response.AuthRegisterResponse;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.auth.response.UserResponse;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.functions.Action1;

public class MainActivity extends BaseOmnomActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	@Inject
	protected Acquiring acquiring;

	@Inject
	protected AuthService authenticator;

	@InjectView(android.R.id.button1)
	protected Button btnConfirm;

	@InjectView(android.R.id.button2)
	protected Button btnAuth;

	@InjectView(android.R.id.edit)
	protected EditText editCode;

	@Override
	public void initUi() {
		//		final CardInfo testCard = CardInfo.createTestCard(this);
		//		testCard.setCardData("30008685803965102459");
		//		acquiring.deleteCard(new MerchantData(this), UserData.createTestUser(), testCard,
		// new Acquiring.CardDeleteListener<AcquiringResponse>() {
		//			@Override
		//			public void onCardDeleted(AcquiringResponse response) {
		//				Log.d(TAG, ">>> url = " + response.getUrl());
		//			}
		//		});

		//				acquiring.registerCard(new MerchantData(this), UserData.createTestUser(), testCard,
		//				                       new Acquiring.CardRegisterListener<CardRegisterPollingResponse>() {
		//					                       @Override
		//					                       public void onCardRegistered(CardRegisterPollingResponse response) {
		//						                       Log.d(TAG, "status = " + response.getStatus() + " cardId = " + response.getCardData
		// ());
		//						                       testCard.setCardData(response.getCardData());
		//						                       verifyCard(testCard);
		//					                       }
		//				                       });

		//		{
		//			"card_id":"30008685803965102459", "url":
		//			"https:\/\/test-cpg.money.mail.ru\/api\/transaction\/check\/?card_id=30008685803965102459&id=10004247505708511228
		// &signature=169f12bc3bd574a607cf17dc850b0075def10119&status=OK_CHECK"
		//		}
		//		testCard.setCardData("30008685803965102459");
		//		verifyCard(testCard);

		final AuthRegisterRequest request = AuthRegisterRequest.create("1", "Dmitry Chertenko", "Ch3D", "ch3dee@gmail.com",
		                                                               "+79133952320",
		                                                               "1987-06-14");

		// register(request, authenticator);
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				confirm(request);
			}
		});

		btnAuth.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				authPhone(request);
			}
		});

		track(TAG + ":initUi", request);
	}

	@OnClick(android.R.id.button3)
	public void newUser() {
		startActivity(UserRegisterActivity.class);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_main;
	}

	private void registerCard() {
		final CardInfo testCard = CardInfo.createTestCard(this);
		acquiring.registerCard(new MerchantData(this), UserData.createTestUser(), testCard).subscribe(new Action1
				<CardRegisterPollingResponse>() {
			@Override
			public void call(CardRegisterPollingResponse response) {
				Log.d(TAG, "status = " + response.getStatus() + " cardId = " + response.getCardId());
				testCard.setCardId(response.getCardId());
				verifyCard(testCard);
			}
		});
	}

	private void authPhone(AuthRegisterRequest request) {
		authenticator.authorizePhone(request.getPhone(), editCode.getText().toString()).subscribe(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				Log.d(TAG, ">>> authPhone = " + response.getStatus());
				String token = response.getToken();
				if(!TextUtils.isEmpty(token)) {
					authenticator.getUser(token).subscribe(new Action1<UserResponse>() {
						@Override
						public void call(final UserResponse userResponse) {
							Log.d(TAG, ">>> user = " + userResponse);
						}
					}, new Action1<Throwable>() {
						@Override
						public void call(Throwable throwable) {
							Log.d(TAG, "getUser", throwable);
						}
					});
				}
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				Log.d(TAG, "authPhone", throwable);
			}
		});
	}

	private void authEmail(AuthRegisterRequest request) {
		authenticator.authorizeEmail(request.getEmail(), editCode.getText().toString()).subscribe(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse response) {
				Log.d(TAG, ">>> authEmail = " + response.getStatus());
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				Log.d(TAG, "authEmail", throwable);
			}
		});
	}

	private void register(AuthRegisterRequest request) {
		authenticator.register(request).subscribe(new Action1<AuthRegisterResponse>() {
			@Override
			public void call(AuthRegisterResponse response) {
				Log.d(TAG, ">>> register = " + response.getStatus());
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				Log.d(TAG, "register", throwable);
			}
		});
	}

	private void confirm(AuthRegisterRequest request) {
		authenticator.confirm(request.getPhone(), editCode.getText().toString())
		             .subscribe(new Action1<AuthResponse>() {
			             @Override
			             public void call(AuthResponse response) {
				             Log.d(TAG, ">>> confirm = " + response.getStatus());
			             }
		             }, new Action1<Throwable>() {
			             @Override
			             public void call(Throwable throwable) {
				             Log.d(TAG, "confirm", throwable);
			             }
		             });
	}

	private void verifyCard(final CardInfo cardInfo) {
//		acquiring.verifyCard(new MerchantData(MainActivity.this), UserData.createTestUser(), cardInfo, 1.4,
//		                     new Acquiring.CardVerifyListener() {
//			                     @Override
//			                     public void onCardVerified(AcquiringResponse response) {
//				                     Log.d(TAG, "url = " + response.getUrl());
//				                     pay(cardInfo);
//			                     }
//
//			                     @Override
//			                     public void onError(Throwable throwable) {
//
//			                     }
//		                     });
	}

	private void pay(final CardInfo cardInfo) {
//		final ExtraData extra = MailRuExtra.create(10, "test_rest_id");
//		final OrderInfo order = OrderInfoMailRu.create(100, "999", "message");
//		final PaymentInfo paymentInfo = PaymentInfoFactory.create(AcquiringType.MAIL_RU,
//		                                                          UserData.createTestUser(), cardInfo, extra, order);
//
//		acquiring.pay(new MerchantData(MainActivity.this), paymentInfo, new Acquiring.PaymentListener() {
//			@Override
//			public void onPaymentSettled(AcquiringPollingResponse response) {
//				Log.d(TAG, "status = " + response.getStatus());
//			}
//
//			@Override
//			public void onError(Throwable throwable) {
//			}
//		});
	}
}
