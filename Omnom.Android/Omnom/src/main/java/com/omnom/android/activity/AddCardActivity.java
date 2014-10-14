package com.omnom.android.activity;

import android.content.Intent;
import android.widget.EditText;

import com.omnom.android.R;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MerchantData;
import com.omnom.android.acquiring.mailru.model.UserData;
import com.omnom.android.acquiring.mailru.response.CardRegisterPollingResponse;
import com.omnom.android.activity.base.BaseOmnomActivity;

import javax.inject.Inject;

import butterknife.OnClick;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import static butterknife.ButterKnife.findById;
import static com.omnom.android.utils.utils.AndroidUtils.showToast;

public class AddCardActivity extends BaseOmnomActivity {
	private static final int REQUEST_CODE_CARD_IO = 101;

	@Inject
	protected Acquiring mAcquiring;
	private CardInfo card;

	@Override
	public void initUi() {

	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_add_card;
	}

	@OnClick(R.id.btn_verify)
	public void verifyCard() {
		final UserData user = UserData.createTestUser();
		final MerchantData merchant = new MerchantData(getActivity());

  		final EditText text = findById(this, R.id.edit_amount);
		mAcquiring.verifyCard(merchant, user, card, Double.parseDouble(text.getText().toString()), new Acquiring.CardVerifyListener() {
			@Override
			public void onCardVerified(Object response) {
				showToast(getActivity(), "VERIFIED");
			}
		});
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
				mAcquiring.registerCard(merchant, user, card,
				                        new Acquiring.CardRegisterListener<CardRegisterPollingResponse>() {
					                        @Override
					                        public void onCardRegistered(CardRegisterPollingResponse response) {
						                        card.setCardId(response.getCardId());
						                        // verifyCard(card, user, merchant);
					                        }
				                        });
			} else {
				finish();
			}
		}
	}

//	private void verifyCard(final CardInfo cardInfo, UserData user, final MerchantData merchant) {
//		mAcquiring.verifyCard(merchant, user, cardInfo, 1.4,
//		                      new Acquiring.CardVerifyListener<AcquiringResponse>() {
//			                      @Override
//			                      public void onCardVerified(AcquiringResponse response) {
//				                      if(response.getError() == null) {
//					                      OmnomApplication.get(getActivity()).getPreferences().setCardId(getActivity(),
//					                                                                                     cardInfo.getCardId());
//					                      showToast(getActivity(), "Card verified");
//					                      finish();
//				                      } else {
//					                      showToast(getActivity(), "Cannot verify card");
//					                      finish();
//				                      }
//			                      }
//		                      });
//	}

	public void onScanPress() {
		Intent scanIntent = new Intent(this, CardIOActivity.class);
		scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN, "e041fdfc4e9c4c6ba1dbd26969a98d92");
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: true
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
		startActivityForResult(scanIntent, REQUEST_CODE_CARD_IO);
	}
}
