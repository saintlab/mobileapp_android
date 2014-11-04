package com.omnom.android.activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.utils.view.ErrorEdit;
import com.omnom.android.view.HeaderView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

import static butterknife.ButterKnife.findById;
import static com.omnom.android.utils.utils.AndroidUtils.showToast;

public class CardConfirmActivity extends BaseOmnomActivity {
	private static final int TYPE_ADD_CONFIRM = 0;

	private static final int TYPE_CONFIRM = 1;

	@SuppressLint("NewApi")
	public static void startAddConfirm(BaseOmnomActivity activity, final CardInfo card, int code) {
		final Intent intent = new Intent(activity, CardConfirmActivity.class);
		intent.putExtra(EXTRA_CARD_DATA, card);
		intent.putExtra(EXTRA_TYPE, TYPE_ADD_CONFIRM);
		if(AndroidUtils.isJellyBean()) {
			 Bundle extras = ActivityOptions.makeCustomAnimation(activity,
			                                                    R.anim.slide_in_right,
			                                                    R.anim.slide_out_left).toBundle();
			activity.startActivityForResult(intent, code, extras);
		} else {
			activity.startActivityForResult(intent, code);
		}
	}

	@SuppressLint("NewApi")
	public static void startConfirm(BaseOmnomActivity activity, final CardInfo card, int code) {
		final Intent intent = new Intent(activity, CardConfirmActivity.class);
		intent.putExtra(EXTRA_CARD_DATA, card);
		intent.putExtra(EXTRA_TYPE, TYPE_CONFIRM);
		if(AndroidUtils.isJellyBean()) {
			Bundle extras = ActivityOptions.makeCustomAnimation(activity,
			                                                    R.anim.slide_in_right,
			                                                    R.anim.slide_out_left).toBundle();
			activity.startActivityForResult(intent, code, extras);
		} else {
			activity.startActivityForResult(intent, code);
		}
	}

	@InjectView(R.id.panel_top)
	protected HeaderView mPanelTop;

	@InjectView(R.id.edit_amount)
	protected ErrorEdit mEditAmount;

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

	private int mType;

	@Override
	protected void handleIntent(final Intent intent) {
		mCard = intent.getParcelableExtra(EXTRA_CARD_DATA);
		mType = intent.getIntExtra(EXTRA_TYPE, TYPE_ADD_CONFIRM);
	}

	@Override
	public void initUi() {
		ViewUtils.setVisible(mTextInfo, false);
		mEditAmount.getEditText().setEnabled(false);
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
		initAmount();
		if(mType == TYPE_ADD_CONFIRM) {
			registerCard();
		} else {
			// skip and wait until user submit verification amount
			mEditAmount.getEditText().setEnabled(true);
			mPanelTop.setButtonRightEnabled(true);
		}
	}

	private void initAmount() {
		final EditText editText = mEditAmount.getEditText();
		editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					verifyCard();
					return true;
				}
				return false;
			}
		});
		editText.addTextChangedListener(new TextWatcher() {
			public boolean hadComma = false;

			public String delimiter = StringUtils.getCurrencyDelimiter();

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				hadComma = s.toString().contains(delimiter);
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				editText.removeTextChangedListener(this);
				final String str = s.toString();

				final int separatorIndex = str.indexOf(delimiter);
				String amount = StringUtils.filterAmount(str);
				final int length = amount.length();
				if(length == 2 && separatorIndex == -1 && !hadComma) {
					amount += delimiter;
				}

				if(length == 3 && separatorIndex == -1) {
					final String last = amount.substring(length - 1, length);
					amount = amount.replace(last, delimiter + last);
				}

				final String text = amount + getCurrencySuffix();
				editText.setText(text);
				editText.setSelection(text.length() - 1);
				editText.addTextChangedListener(this);
			}
		});
		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					int length = editText.getText().length();
					if(length >= 2) {
						editText.setSelection(length - 2);
					}
				}
			}
		});
	}

	private String getCurrencySuffix() {
		return getString(R.string.currency_ruble);
	}

	private void registerCard() {
		mPanelTop.showProgress(true);
		final MerchantData merchant = new MerchantData(getActivity());
		com.omnom.android.auth.UserData wicketUser = OmnomApplication.get(getActivity()).getUserProfile().getUser();
		final UserData user = UserData.create(String.valueOf(wicketUser.getId()), wicketUser.getPhone());
		mCardRegisterSubscription = AndroidObservable.bindActivity(this,
		                                                           mAcquiring.registerCard(merchant, user, mCard)
		                                                                     .delaySubscription(1000, TimeUnit.MILLISECONDS)
		                                                          )
		                                             .subscribe(
				                                             new Action1<CardRegisterPollingResponse>() {
					                                             @Override
					                                             public void call(CardRegisterPollingResponse response) {
						                                             mCard.setCardId(response.getCardId());
						                                             ViewUtils.setVisible(mTextInfo, true);
						                                             mPanelTop.showProgress(false);
						                                             mPanelTop.setButtonRightEnabled(true);
						                                             mPanelTop.setButtonRight(R.string.ready, mVerifyClickListener);
						                                             final EditText editAmount = mEditAmount.getEditText();
						                                             editAmount.setEnabled(true);
						                                             AndroidUtils.showKeyboard(editAmount);
					                                             }
				                                             }, new Action1<Throwable>() {
					                                             @Override
					                                             public void call(final Throwable throwable) {
						                                             ViewUtils.setVisible(mTextInfo, false);
						                                             mPanelTop.showProgress(false);
						                                             mEditAmount.setError(R.string.something_went_wrong_try_agint);
						                                             mPanelTop.setButtonRightEnabled(true);
						                                             mPanelTop.setButtonRightDrawable(
								                                             R.drawable.ic_repeat_small,
								                                             mRegisterClickListener);
						                                             mEditAmount.getEditText().setEnabled(false);
					                                             }
				                                             });
	}

	public void verifyCard() {
		if(mCard == null) {
			showToast(getActivity(), "Scan card");
			return;
		}
		mPanelTop.showProgress(true);
		final ErrorEdit text = findById(this, R.id.edit_amount);
		final String filterAmount = StringUtils.filterAmount(text.getText());
		final double amount = Double.parseDouble(filterAmount);
		mCardVerifySubscribtion = AndroidObservable.bindActivity(this, mAcquiring.verifyCard(mMerchant, mUser, mCard, amount))
		                                           .subscribe(new Action1<AcquiringResponse>() {
			                                           @Override
			                                           public void call(AcquiringResponse response) {
				                                           if(response.getError() != null) {
					                                           onVerificationError();
				                                           } else {
					                                           mPanelTop.showProgress(false);
					                                           setResult(RESULT_OK);
					                                           finish();
				                                           }
			                                           }
		                                           }, new Action1<Throwable>() {
			                                           @Override
			                                           public void call(Throwable throwable) {
				                                           onVerificationError();
			                                           }
		                                           });
	}

	private void onVerificationError() {
		mPanelTop.showProgress(false);
		mPanelTop.setButtonRight(R.string.repeat, mVerifyClickListener);
		showToast(getActivity(), "VERIFICATION ERROR");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OmnomObservable.unsubscribe(mCardVerifySubscribtion);
		OmnomObservable.unsubscribe(mCardRegisterSubscription);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_card_confirm;
	}
}
