package com.omnom.android.activity;

import android.content.Intent;
import android.view.View;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MerchantData;
import com.omnom.android.acquiring.mailru.model.UserData;
import com.omnom.android.acquiring.mailru.response.CardRegisterPollingResponse;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.utils.view.ErrorEdit;
import com.omnom.android.view.HeaderView;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.functions.Action1;

public class CardConfirmActivity extends BaseOmnomActivity {

	public static void start(BaseOmnomActivity activity, final CardInfo card, int code) {
		final Intent intent = new Intent(activity, CardConfirmActivity.class);
		intent.putExtra(EXTRA_CARD_DATA, card);
		activity.startActivityForResult(intent, code);
	}

	@InjectView(R.id.panel_top)
	protected HeaderView mPanelTop;

	@InjectView(R.id.edit_amount)
	protected ErrorEdit mEditError;

	@Inject
	protected Acquiring mAcquiring;

	private CardInfo mCard;

	@Override
	protected void handleIntent(final Intent intent) {
		mCard = intent.getParcelableExtra(EXTRA_CARD_DATA);
	}

	@Override
	public void initUi() {
		mPanelTop.setTitleBig(R.string.card_binding);
		mPanelTop.setButtonRight(R.string.ready, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final MerchantData merchant = new MerchantData(getActivity());
				com.omnom.android.auth.UserData wicketUser = OmnomApplication.get(getActivity()).getUserProfile().getUser();
				final UserData user = UserData.create(String.valueOf(wicketUser.getId()), wicketUser.getPhone());
				mAcquiring.registerCard(merchant, user, mCard).subscribe(new Action1<CardRegisterPollingResponse>() {
					@Override
					public void call(CardRegisterPollingResponse response) {
						mCard.setCardId(response.getCardId());
						// verifyCard(card, user, merchant);
						// TODO: start ConfirmCardActivity(cardInfo)
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(final Throwable throwable) {
					}
				});
			}
		});

		mPanelTop.setButtonLeft(R.string.cancel, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
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
		return R.layout.activity_card_confirm;
	}
}
