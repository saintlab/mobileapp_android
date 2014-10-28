package com.omnom.android.activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MerchantData;
import com.omnom.android.acquiring.mailru.model.UserData;
import com.omnom.android.acquiring.mailru.response.CardRegisterPollingResponse;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.adapter.CardsAdapter;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.cards.Card;
import com.omnom.android.restaurateur.model.cards.CardsResponse;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.preferences.PreferenceProvider;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.view.LoginPanelTop;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.InjectView;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

public class CardsActivity extends BaseOmnomActivity {
	private static final int REQUEST_CODE_CARD_IO = 101;

	@SuppressLint("NewApi")
	public static void start(final Context activity, final double amount, final int accentColor) {
		final Intent intent = new Intent(activity, CardsActivity.class);
		intent.putExtra(Extras.EXTRA_ORDER_AMOUNT_TEXT, amount);
		intent.putExtra(Extras.EXTRA_ACCENT_COLOR, accentColor);

		if(AndroidUtils.isJellyBean()) {
			Bundle extras = ActivityOptions.makeCustomAnimation(activity,
			                                                    R.anim.slide_in_up,
			                                                    R.anim.fake_fade_out_long).toBundle();
			activity.startActivity(intent, extras);
		} else {
			activity.startActivity(intent);
		}
	}

	@Inject
	protected Acquiring mAcquiring;

	@Inject
	protected RestaurateurObeservableApi api;

	@InjectView(R.id.panel_top)
	protected LoginPanelTop mPanelTop;

	@InjectView(R.id.btn_pay)
	protected Button mBtnPay;

	@InjectView(R.id.list)
	protected ListView mList;

	private CardInfo card;

	private double mAmount;

	private int mAccentColor;

	private Subscription mCardsSubscription;

	private PreferenceProvider mPreferences;

	@Override
	public void initUi() {
		mPreferences = OmnomApplication.get(getActivity()).getPreferences();

		mPanelTop.setButtonLeft(R.string.cancel, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				finish();
			}
		});
		mPanelTop.setButtonRight(R.string.add_card, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				onAdd();
			}
		});

		mPanelTop.showProgress(true);
		mPanelTop.showButtonRight(false);
		mList.setAdapter(new CardsAdapter(this, Collections.EMPTY_LIST));

		mCardsSubscription = AndroidObservable.bindActivity(this, api.getCards().delaySubscription(1000, TimeUnit.MILLISECONDS)).subscribe(
				new Action1<CardsResponse>() {
					@Override
					public void call(final CardsResponse cards) {
						mList.setAdapter(new CardsAdapter(getActivity(), cards.getCards()));
						mPanelTop.showProgress(false);
						mPanelTop.showButtonRight(true);
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(final Throwable throwable) {
						mPanelTop.showProgress(false);
						mPanelTop.showButtonRight(true);
					}
				});

		final String text = StringUtils.formatCurrency(mAmount) + getString(R.string.currency_ruble);
		mBtnPay.setText(getString(R.string.pay_amount, text));

		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				CardsAdapter adapter = (CardsAdapter) mList.getAdapter();
				Card card = (Card) adapter.getItem(position);
				mPreferences.setCardId(getActivity(), card.getExternalCardId());
				adapter.notifyDataSetChanged();
			}
		});

		GradientDrawable sd = (GradientDrawable) mBtnPay.getBackground();
		sd.setColor(mAccentColor);
		sd.invalidateSelf();
	}

	//@OnClick(R.id.btn_verify)
	//public void verifyCard() {
	//	if(card == null) {
	//		showToast(getActivity(), "Scan card");
	//		return;
	//	}
	//	final UserData user = UserData.createTestUser();
	//	final MerchantData merchant = new MerchantData(getActivity());
	//
	//	final EditText text = findById(this, R.id.edit_amount);
	//	mCardVerifySubscribtion = AndroidObservable
	//			.bindActivity(this, mAcquiring.verifyCard(merchant, user, card, Double.parseDouble(text.getText().toString())))
	//			.subscribe(new Action1<AcquiringResponse>() {
	//				@Override
	//				public void call(AcquiringResponse response) {
	//					final String cardData = card.toGson(gson);
	//					getPreferences().setCardData(getActivity(), cardData);
	//					showToast(getActivity(), "VERIFIED");
	//				}
	//			}, new Action1<Throwable>() {
	//				@Override
	//				public void call(Throwable throwable) {
	//					showToast(getActivity(), "VERIFICATION ERROR");
	//				}
	//			});
	//}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_cards;
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.fake_fade_out_long, R.anim.slide_out_down);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OmnomObservable.unsubscribe(mCardsSubscription);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_CARD_IO) {
			if(data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
				final CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
				card = CardInfo.createTestCard(this, scanResult);
				final MerchantData merchant = new MerchantData(this);
				final UserData user = UserData.createTestUser();
				mAcquiring.registerCard(merchant, user, card).subscribe(new Action1<CardRegisterPollingResponse>() {
					@Override
					public void call(CardRegisterPollingResponse response) {
						card.setCardId(response.getCardId());
						// verifyCard(card, user, merchant);
					}
				});
			} else {
				finish();
			}
		}
	}

	public void onAdd() {
		Intent scanIntent = new Intent(this, CardIOActivity.class);
		scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN, getString(R.string.cardio_app_token));
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true);
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false);
		scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true);
		startActivityForResult(scanIntent, REQUEST_CODE_CARD_IO);
	}

	@Override
	protected void handleIntent(final Intent intent) {
		mAmount = intent.getDoubleExtra(Extras.EXTRA_ORDER_AMOUNT_TEXT, 0);
		mAccentColor = intent.getIntExtra(Extras.EXTRA_ACCENT_COLOR, Color.WHITE);
	}
}
