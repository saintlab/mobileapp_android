package com.omnom.android.activity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MerchantData;
import com.omnom.android.acquiring.mailru.model.UserData;
import com.omnom.android.acquiring.mailru.response.AcquiringResponse;
import com.omnom.android.acquiring.mailru.response.CardRegisterPollingResponse;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.utils.view.ErrorEdit;
import com.omnom.android.view.HeaderView;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

import static butterknife.ButterKnife.findById;
import static com.omnom.android.utils.utils.AndroidUtils.showToast;

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

	@InjectView(R.id.txt_info)
	protected TextView mTextInfo;

	@Inject
	protected Acquiring mAcquiring;

	private CardInfo mCard;

	private Subscription mCardVerifySubscribtion;

	private UserData mUser;

	private MerchantData mMerchant;

	private View.OnClickListener mVerifyClickListener = new View.OnClickListener() {
		@Override
		public void onClick(final View v) {
			verifyCard();
		}
	};

	private View.OnClickListener mRegisterClickListener = new View.OnClickListener() {
		@Override
		public void onClick(final View v) {
			registerCard();
		}
	};

	private Subscription mCardRegisterSubscription;

	@Override
	protected void handleIntent(final Intent intent) {
		mCard = intent.getParcelableExtra(EXTRA_CARD_DATA);
	}

	@Override
	public void initUi() {
		ViewUtils.setVisible(mTextInfo, false);
		UserProfile mUserProfile = OmnomApplication.get(getActivity()).getUserProfile();
		mUser = UserData.create(mUserProfile.getUser());
		mMerchant = new MerchantData(getActivity());
		mPanelTop.setTitleBig(R.string.card_binding);
		mPanelTop.setButtonRightEnabled(false);
		mPanelTop.setButtonRight(R.string.ready, mVerifyClickListener);
		mPanelTop.setButtonLeft(R.string.cancel, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		registerCard();
	}

	private void registerCard() {
		mPanelTop.showProgress(true);
		final MerchantData merchant = new MerchantData(getActivity());
		com.omnom.android.auth.UserData wicketUser = OmnomApplication.get(getActivity()).getUserProfile().getUser();
		final UserData user = UserData.create(String.valueOf(wicketUser.getId()), wicketUser.getPhone());
		mCardRegisterSubscription = AndroidObservable.bindActivity(this,
		                                                       mAcquiring.registerCard(merchant, user, mCard))
		                                         .subscribe(
				                                         new Action1<CardRegisterPollingResponse>() {
					                                         @Override
					                                         public void call(CardRegisterPollingResponse response) {
						                                         mCard.setCardId(response.getCardId());
						                                         ViewUtils.setVisible(mTextInfo, true);
						                                         mPanelTop.showProgress(false);
					                                         }
				                                         }, new Action1<Throwable>() {
					                                         @Override
					                                         public void call(final Throwable throwable) {
						                                         ViewUtils.setVisible(mTextInfo, false);
						                                         mPanelTop.showProgress(false);
						                                         mEditError.setError(R.string.something_went_wrong_try_agint);
						                                         mPanelTop.setButtonRightEnabled(true);
						                                         mPanelTop.setButtonRight(R.string.repeat, mRegisterClickListener);
					                                         }
				                                         });
	}

	public void verifyCard() {
		if(mCard == null) {
			showToast(getActivity(), "Scan card");
			return;
		}
		mPanelTop.showProgress(true);
		final EditText text = findById(this, R.id.edit_amount);
		final double amount = Double.parseDouble(text.getText().toString());
		mCardVerifySubscribtion = AndroidObservable.bindActivity(this, mAcquiring.verifyCard(mMerchant, mUser, mCard, amount))
		                                           .subscribe(new Action1<AcquiringResponse>() {
			                                           @Override
			                                           public void call(AcquiringResponse response) {
				                                           // TODO:
				                                           // final String cardData = card.toGson(gson);
				                                           // getPreferences().setCardData(getActivity(), cardData);
				                                           mPanelTop.showProgress(false);
				                                           setResult(RESULT_OK);
				                                           showToast(getActivity(), "VERIFIED");
			                                           }
		                                           }, new Action1<Throwable>() {
			                                           @Override
			                                           public void call(Throwable throwable) {
				                                           mPanelTop.showProgress(false);
				                                           mPanelTop.setButtonRight(R.string.repeat, mVerifyClickListener);
				                                           showToast(getActivity(), "VERIFICATION ERROR");
			                                           }
		                                           });
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OmnomObservable.unsubscribe(mCardVerifySubscribtion);
		OmnomObservable.unsubscribe(mCardRegisterSubscription);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_card_confirm;
	}
}
