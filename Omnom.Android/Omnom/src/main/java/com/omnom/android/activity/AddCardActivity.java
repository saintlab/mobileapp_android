package com.omnom.android.activity;

import android.content.Intent;
import android.widget.EditText;

import com.google.gson.Gson;
import com.omnom.android.R;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MerchantData;
import com.omnom.android.acquiring.mailru.model.UserData;
import com.omnom.android.acquiring.mailru.response.AcquiringResponse;
import com.omnom.android.acquiring.mailru.response.CardRegisterPollingResponse;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.utils.observable.OmnomObservable;

import javax.inject.Inject;

import butterknife.OnClick;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

import static butterknife.ButterKnife.findById;
import static com.omnom.android.utils.utils.AndroidUtils.showToast;

public class AddCardActivity extends BaseOmnomActivity {
	private static final int REQUEST_CODE_CARD_IO = 101;

	@Inject
	protected Acquiring mAcquiring;
	private CardInfo card;
	private Gson gson;
	private Subscription mCardVerifySubscribtion;

	@Override
	public void initUi() {
		gson = new Gson();
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_add_card;
	}

	@OnClick(R.id.btn_verify)
	public void verifyCard() {
		if(card == null) {
			showToast(getActivity(), "Scan card");
			return;
		}
		final UserData user = UserData.createTestUser();
		final MerchantData merchant = new MerchantData(getActivity());

		final EditText text = findById(this, R.id.edit_amount);
		mCardVerifySubscribtion = AndroidObservable
				.bindActivity(this, mAcquiring.verifyCard(merchant, user, card, Double.parseDouble(text.getText().toString())))
				.subscribe(new Action1<AcquiringResponse>() {
					@Override
					public void call(AcquiringResponse response) {
						final String cardData = card.toGson(gson);
						getPreferences().setCardData(getActivity(), cardData);
						showToast(getActivity(), "VERIFIED");
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						showToast(getActivity(), "VERIFICATION ERROR");
					}
				});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OmnomObservable.unsubscribe(mCardVerifySubscribtion);
	}

	@OnClick(R.id.btn_add_card)
	public void addCard() {
		onScanPress();
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

	public void onScanPress() {
		Intent scanIntent = new Intent(this, CardIOActivity.class);
		scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN, "e041fdfc4e9c4c6ba1dbd26969a98d92");
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: true
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
		startActivityForResult(scanIntent, REQUEST_CODE_CARD_IO);
	}
}
