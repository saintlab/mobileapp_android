package com.omnom.android.activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.UserData;
import com.omnom.android.acquiring.mailru.response.AcquiringPollingResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringResponseError;
import com.omnom.android.acquiring.mailru.response.CardRegisterPollingResponse;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.fragment.PayOnceFragment;
import com.omnom.android.mixpanel.model.acquiring.CardAddedMixpanelEvent;
import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.restaurateur.model.config.AcquiringData;
import com.omnom.android.utils.ObservableUtils;
import com.omnom.android.utils.UserHelper;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
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
import static com.omnom.android.mixpanel.MixPanelHelper.Project.OMNOM;
import static com.omnom.android.utils.utils.AndroidUtils.showToast;

public class CardConfirmActivity extends BaseOmnomFragmentActivity
		implements PayOnceFragment.OnPayListener,
		           PayOnceFragment.VisibilityListener {

	private static final String TAG = CardConfirmActivity.class.getSimpleName();
	public static final int TYPE_BIND_CONFIRM = 0;

	public static final int TYPE_CONFIRM = 1;

	@SuppressLint("NewApi")
	public static void startAddConfirm(BaseOmnomActivity activity, final CardInfo card, int code,
	                                   double amount, boolean scanUsed) {
		final Intent intent = new Intent(activity, CardConfirmActivity.class);
		intent.putExtra(EXTRA_CARD_DATA, card);
		intent.putExtra(EXTRA_TYPE, TYPE_BIND_CONFIRM);
		intent.putExtra(EXTRA_ORDER_AMOUNT, amount);
		intent.putExtra(EXTRA_SCAN_USED, scanUsed);
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
	public static void startConfirm(BaseOmnomActivity activity, final CardInfo card, int code,
	                                double amount) {
		final Intent intent = new Intent(activity, CardConfirmActivity.class);
		intent.putExtra(EXTRA_CARD_DATA, card);
		intent.putExtra(EXTRA_TYPE, TYPE_CONFIRM);
		intent.putExtra(EXTRA_ORDER_AMOUNT, amount);
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

	@InjectView(R.id.transparent_panel)
	protected FrameLayout transparentPanel;

	@InjectView(R.id.fragment_container)
	protected FrameLayout fragmentContainer;

	@Inject
	protected Acquiring mAcquiring;

	private CardInfo mCard;

	private Subscription mCardVerifySubscribtion;

	private UserData mUser;

	private AcquiringData mAcquiringData;

	private double mAmount;

	private boolean mScanUsed;

	private boolean isFirstEdit = true;

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

	private Fragment payOnceFragment;

	private boolean isKeyboardVisible = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			payOnceFragment = PayOnceFragment.newInstance(mAmount, mType);
		}
	}

	@Override
	protected void handleIntent(final Intent intent) {
		mCard = intent.getParcelableExtra(EXTRA_CARD_DATA);
		mType = intent.getIntExtra(EXTRA_TYPE, TYPE_BIND_CONFIRM);
		mAmount = intent.getDoubleExtra(EXTRA_ORDER_AMOUNT, 0);
		mScanUsed = intent.getBooleanExtra(EXTRA_SCAN_USED, false);
	}

	@Override
	public void initUi() {
		ViewUtils.setVisible(mTextInfo, false);
		final EditText editText = mEditAmount.getEditText();
		final String text = getString(R.string.hint_confirm_amount);
		editText.setText(text);
		editText.setSelection(0);
		editText.setTextColor(getResources().getColor(R.color.info_hint));

		editText.setEnabled(false);
		UserProfile mUserProfile = OmnomApplication.get(getActivity()).getUserProfile();
		mUser = UserData.create(mUserProfile.getUser());
		mAcquiringData = OmnomApplication.get(getActivity()).getConfig().getAcquiringData();
		mPanelTop.setTitleBig(R.string.card_binding);
		mPanelTop.setButtonRightEnabled(false);
		mPanelTop.setButtonRight(R.string.bind, mVerifyClickListener);
		mPanelTop.setButtonLeftDrawable(R.drawable.btn_previous, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		initAmount();
		if(mType == TYPE_BIND_CONFIRM) {
			registerCard();
		} else {
			// skip and wait until user submit verification amount
			ViewUtils.setVisible(mTextInfo, true);
			editText.setEnabled(true);
			mPanelTop.setButtonRightEnabled(true);
		}
		final View activityRootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
		addKeyboardListener(activityRootView);
		if(mAmount == 0) {
			int height = (int) getResources().getDimension(R.dimen.pay_once_fragment_height_small);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
			                                                                           height);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			fragmentContainer.setLayoutParams(layoutParams);
		}
	}

	private void addKeyboardListener(View activityRootView) {
		ViewTreeObserver.OnGlobalLayoutListener listener =
				AndroidUtils.createKeyboardListener(activityRootView, new AndroidUtils.KeyboardVisibilityListener() {
					@Override
					public void onVisibilityChanged(boolean isVisible) {
						isKeyboardVisible = isVisible;
					}
				});
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(listener);
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
		final int suffixLength = getCurrencySuffix().length();
		editText.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (isFirstEdit) {
					editText.setText(StringUtils.EMPTY_STRING);
					editText.setTextColor(getResources().getColor(android.R.color.black));
					isFirstEdit = false;
				}
				return false;
			}
		});
		editText.addTextChangedListener(new TextWatcher() {
			public boolean hadComma = false;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				hadComma = s.toString().contains(StringUtils.CURRENCY_DELIMITER);
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				editText.removeTextChangedListener(this);
				final String str = s.toString();

				final int separatorIndex = str.indexOf(StringUtils.CURRENCY_DELIMITER);
				String amount = StringUtils.filterAmount(str);
				final int length = amount.length();
				if(length == 2 && separatorIndex == -1 && !hadComma) {
					amount += StringUtils.CURRENCY_DELIMITER;
				}

				if(length == 3 && separatorIndex == -1) {
					final String last = amount.substring(length - 1, length);
					amount = amount.replace(last, StringUtils.CURRENCY_DELIMITER + last);
				}

				final String text = amount + getCurrencySuffix();
				editText.setText(text);
				editText.setSelection(text.length() - suffixLength);
				editText.addTextChangedListener(this);
			}
		});
		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					if (isFirstEdit) {
						editText.setSelection(0);
					} else {
						int length = editText.getText().length();
						if (length >= suffixLength + 1) {
							editText.setSelection(length - suffixLength);
						}
					}
				}
			}
		});
	}

	private String getCurrencySuffix() {
		return getString(R.string.currency_suffix_ruble);
	}

	private void registerCard() {
		if(isBusy()) {
			return;
		}
		mPanelTop.showProgress(true);
		final AcquiringData acquiringData = OmnomApplication.get(getActivity()).getConfig().getAcquiringData();
		com.omnom.android.auth.UserData wicketUser = OmnomApplication.get(getActivity()).getUserProfile().getUser();
		final UserData user = UserData.create(String.valueOf(wicketUser.getId()), wicketUser.getPhone());
		mCardRegisterSubscription = AndroidObservable.bindActivity(this,
		                                                           mAcquiring.registerCard(acquiringData, user, mCard)
		                                                                     .delaySubscription(1000, TimeUnit.MILLISECONDS)
		                                                          )
		                                             .subscribe(
				                                             new Action1<CardRegisterPollingResponse>() {
					                                             @Override
					                                             public void call(CardRegisterPollingResponse response) {
						                                             if(AcquiringPollingResponse.STATUS_OK.equals(response.getStatus())) {
							                                             reportMixPanelSuccess(mCard);
							                                             mCard.setCardId(response.getCardId());
							                                             ViewUtils.setVisible(mTextInfo, true);
							                                             mPanelTop.showProgress(false);
							                                             mPanelTop.setButtonRightEnabled(true);
							                                             mPanelTop.setButtonRight(R.string.bind, mVerifyClickListener);
							                                             final EditText editAmount = mEditAmount.getEditText();
							                                             editAmount.setEnabled(true);
							                                             AndroidUtils.showKeyboard(editAmount);
						                                             } else {

							                                             if (response.getError() != null) {
								                                             reportMixPanelFail(mCard, response.getError());
								                                             processCardRegisterError(response.getError().getDescr());
							                                             } else {
								                                             processCardRegisterError(getString(R.string.something_went_wrong_try_again));
							                                             }
						                                             }
						                                             busy(false);
					                                             }
				                                             }, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
					                                             @Override
					                                             public void onError(Throwable throwable) {
						                                             Log.w(TAG, "registerCard", throwable);
						                                             processCardRegisterError(getString(
								                                             R.string.something_went_wrong_try_again));
					                                             }
				                                             });
	}

	private void processCardRegisterError(final String errorMessage) {
		ViewUtils.setVisible(mTextInfo, false);
		mPanelTop.showProgress(false);
		mEditAmount.setError(errorMessage);
		mPanelTop.setButtonRightEnabled(true);
		mPanelTop.setButtonRightDrawable(
				R.drawable.ic_repeat_small,
				mRegisterClickListener);
		mEditAmount.getEditText().setEnabled(false);
		busy(false);
	}

	private void reportMixPanelSuccess(final CardInfo cardInfo) {
		OmnomApplication.getMixPanelHelper(this).track(OMNOM, new CardAddedMixpanelEvent(UserHelper.getUserData(this), cardInfo, mScanUsed));
	}

	private void reportMixPanelFail(final CardInfo cardInfo, final AcquiringResponseError error) {
		OmnomApplication.getMixPanelHelper(this).track(OMNOM, new CardAddedMixpanelEvent(UserHelper.getUserData(this), cardInfo, mScanUsed, error));
	}

	public void verifyCard() {
		if(isBusy()) {
			return;
		}
		if(mCard == null) {
			showToast(getActivity(), "Scan card");
			return;
		}
		busy(true);
		mPanelTop.showProgress(true);
		final ErrorEdit text = findById(this, R.id.edit_amount);
		final String filterAmount = StringUtils.filterAmount(text.getText());
		double amount;
		try {
			amount = Double.parseDouble(filterAmount);
		} catch(NumberFormatException e) {
			Log.d(TAG, "Invalid double value: \"" + filterAmount + "\"");
			amount = 0;
		}
		mCardVerifySubscribtion = AndroidObservable.bindActivity(this, mAcquiring.verifyCard(mAcquiringData, mUser, mCard, amount))
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
				                                           busy(false);
			                                           }
		                                           }, new Action1<Throwable>() {
			                                           @Override
			                                           public void call(Throwable throwable) {
				                                           Log.w(TAG, "verifyCard", throwable);
				                                           onVerificationError();
				                                           busy(false);
			                                           }
		                                           });
	}

	private void onVerificationError() {
		ViewUtils.setVisible(mTextInfo, false);
		mPanelTop.showProgress(false);
		mPanelTop.setButtonRight(R.string.bind, mVerifyClickListener);
		mEditAmount.setError(getWrongChecksumMessage());
	}

	private SpannableString getWrongChecksumMessage() {
		SpannableString spannableString = new SpannableString(getResources().getString(R.string.wrong_checksum));
		ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.link_color));
		ClickableSpan clickableSpan = new ClickableSpan() {
			@Override
			public void onClick(View widget) {
				if(!isKeyboardVisible) {
					showPayOnceFragment();
				} else {
					AndroidUtils.hideKeyboard(mEditAmount.getEditText(), new ResultReceiver(new Handler()) {
						@Override
						protected void onReceiveResult(int resultCode, Bundle resultData) {
							super.onReceiveResult(resultCode, resultData);
							if(resultCode == InputMethodManager.RESULT_HIDDEN ||
									resultCode == InputMethodManager.RESULT_UNCHANGED_HIDDEN) {
								showPayOnceFragment();
							}
						}
					});
				}
			}
		};
		int noSmsLength = getResources().getString(R.string.no_sms).length();
		spannableString.setSpan(clickableSpan, spannableString.length() - noSmsLength, spannableString.length(),
		                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannableString.setSpan(colorSpan, spannableString.length() - noSmsLength, spannableString.length(),
		                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannableString;
	}

	private void showPayOnceFragment() {
		mEditAmount.getEditText().setEnabled(false);
		getSupportFragmentManager().beginTransaction()
		                           .addToBackStack(null)
		                           .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down,
		                                                R.anim.slide_in_up, R.anim.slide_out_down)
		                           .replace(R.id.fragment_container, payOnceFragment)
		                           .commit();
		AnimationUtils.animateAlpha(transparentPanel, true);
	}

	@Override
	public void pay() {
		if (mType == TYPE_BIND_CONFIRM) {
			Intent intent = new Intent();
			intent.putExtra(EXTRA_CARD_DATA, mCard);
			setResult(CardsActivity.RESULT_PAY, intent);
		} else {
			setResult(CardsActivity.RESULT_ENTER_CARD_AND_PAY);
		}
		finish();
	}

	@Override
	public void onVisibilityChanged(boolean isVisible) {
		if(!isVisible) {
			AndroidUtils.showKeyboard(mEditAmount.getEditText());
		}
	}

	@Override
	public void onBackPressed() {
		if(payOnceFragment != null && payOnceFragment.isVisible()) {
			mEditAmount.getEditText().setEnabled(true);
			AnimationUtils.animateAlpha(transparentPanel, false);
			getSupportFragmentManager().beginTransaction()
			                           .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down,
			                                                R.anim.slide_in_up, R.anim.slide_out_down)
			                           .detach(payOnceFragment)
			                           .commit();
		}
		super.onBackPressed();
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
		AndroidUtils.hideKeyboard(mEditAmount.getEditText());
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_card_confirm;
	}

}
