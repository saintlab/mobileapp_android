package com.omnom.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.response.AcquiringResponse;
import com.omnom.android.activity.animation.AddCardTransitionController;
import com.omnom.android.activity.base.BaseOmnomModeSupportActivity;
import com.omnom.android.auth.UserData;
import com.omnom.android.entrance.EntranceData;
import com.omnom.android.restaurateur.model.config.AcquiringData;
import com.omnom.android.utils.CardDataTextWatcher;
import com.omnom.android.utils.CardExpirationTextWatcher;
import com.omnom.android.utils.CardNumberTextWatcher;
import com.omnom.android.utils.CardUtils;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.utils.view.ErrorEditText;
import com.omnom.android.validator.Validator;
import com.omnom.android.validator.card.CvvValidator;
import com.omnom.android.validator.card.ExpirationDateValidator;
import com.omnom.android.validator.card.PanValidator;
import com.omnom.android.view.HeaderView;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import rx.functions.Action1;

import static com.omnom.android.utils.utils.AndroidUtils.showToast;

public class CardAddActivity extends BaseOmnomModeSupportActivity implements TextListener {

	public static final int TYPE_BIND = 0;

	public static final int TYPE_BIND_OR_PAY = 1;

	public static final int TYPE_ENTER_AND_PAY = 2;

	private static final int REQUEST_CODE_CARD_IO = 101;

	private static final int REQUEST_CODE_CARD_REGISTER = 102;

	private static final String TAG = CardAddActivity.class.getSimpleName();

	private class OnFocusChangeListener implements View.OnFocusChangeListener {

		private final Validator validator;

		private OnFocusChangeListener(final Validator validator) {
			this.validator = validator;
		}

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			ErrorEditText editText = (ErrorEditText) v;
			if(!hasFocus) {
				final String value = editText.getText().toString();
				editText.setError(!validator.validate(value));
			} else {
				editText.setError(false);
			}
		}
	}

	@SuppressLint("NewApi")
	public static void start(Activity activity, double amount, int type, EntranceData entranceData, int code) {
		final Intent intent = new Intent(activity, CardAddActivity.class);
		intent.putExtra(EXTRA_ORDER_AMOUNT, amount);
		intent.putExtra(EXTRA_TYPE, type);
		intent.putExtra(EXTRA_ENTRANCE_DATA, entranceData);
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

	@InjectView(R.id.txt_card_number)
	protected ErrorEditText mEditCardNumber;

	@InjectView(R.id.txt_exp_date)
	protected ErrorEditText mEditCardExpDate;

	@InjectView(R.id.txt_cvv)
	protected ErrorEditText mEditCardCvv;

	@InjectView(R.id.img_camera)
	protected ImageView mImgCamera;

	@InjectView(R.id.panel_card)
	protected View mPanelCard;

	@InjectView(R.id.check_save_card)
	protected CheckBox mCheckSaveCard;

	@InjectView(R.id.panel_camera)
	protected View mPanelCamera;

	@InjectView(R.id.txt_camera)
	protected TextView mTextCamera;

	@Inject
	protected Acquiring mAcquiring;

	private Validator panValidator;

	private Validator expDateValidator;

	private Validator cvvValidator;

	private int mType;

	private AddCardTransitionController mTransitionController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		panValidator = new PanValidator();
		expDateValidator = new ExpirationDateValidator();
		cvvValidator = new CvvValidator();
	}

	@Override
	public void initUi() {
		mTransitionController = new AddCardTransitionController(new WeakReference<Activity>(this));

		mCheckSaveCard.setChecked(true);
		mPanelTop.setButtonRightEnabled(false)
		         .setButtonLeft(R.string.cancel, new View.OnClickListener() {
			         @Override
			         public void onClick(final View v) {
				         AndroidUtils.hideKeyboard(mEditCardExpDate);
				         finish();
			         }
		         });
		mCheckSaveCard.setChecked(mType != TYPE_ENTER_AND_PAY);
		ViewUtils.setVisibleGone(mCheckSaveCard, mType == TYPE_BIND_OR_PAY);
		setUpCardEditFields();
	}

	private void setUpCardEditFields() {
		mEditCardExpDate.addTextChangedListener(new CardExpirationTextWatcher(mEditCardExpDate, this));
		mEditCardNumber.addTextChangedListener(new CardNumberTextWatcher(mEditCardNumber, this));
		mEditCardCvv.addTextChangedListener(new CardDataTextWatcher(mEditCardCvv) {
			@Override
			public int getMaxLength() {
				return 3;
			}

			@Override
			public int getDelimiterLength() {
				return 0;
			}

			@Override
			public void afterTextChanged(final Editable s) {
				if(s.length() == 0) {
					focusPrevView();
				}
				CardAddActivity.this.onTextChanged(s.toString());
			}
		});

		mEditCardNumber.setOnFocusChangeListener(new OnFocusChangeListener(panValidator));
		mEditCardExpDate.setOnFocusChangeListener(new OnFocusChangeListener(expDateValidator));
		mEditCardCvv.setOnFocusChangeListener(new OnFocusChangeListener(cvvValidator));

		mEditCardNumber.setError(false);
		mEditCardExpDate.setError(false);
		mEditCardCvv.setError(false);

		mEditCardCvv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					mPanelTop.getBtnRight().callOnClick();
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void onTextChanged(final CharSequence s) {
		final boolean minimize = mEditCardCvv.length() > 0 || mEditCardExpDate.length() > 0 || mEditCardNumber.length() > 0;
		mTransitionController.animteCamera(minimize);
		mPanelTop.setButtonRightEnabled(validate());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_CARD_REGISTER) {
			if(resultCode == CardsActivity.RESULT_PAY) {
				doPay();
			} else if(resultCode == RESULT_OK) {
				setResult(RESULT_OK);
				finish();
			}
		}
		if(requestCode == REQUEST_CODE_CARD_IO) {
			if(data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
				final CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
				postDelayed(350, new Runnable() {
					@Override
					public void run() {
						mEditCardNumber.setText(scanResult.cardNumber);
						AndroidUtils.showKeyboard(mEditCardExpDate);
					}
				});
			}
		}
	}

	private CardInfo createCardInfo() {
		final String pan = CardUtils.preparePan(mEditCardNumber.getText().toString());
		final String expDate = CardUtils.prepareExpDate(mEditCardExpDate.getText().toString());
		final String cvv = mEditCardCvv.getText().toString();
		final String holder = getApp().getConfig().getAcquiringData().getCardHolder();
		return new CardInfo.Builder().pan(pan)
		                             .mixpanelPan(pan)
		                             .expDate(expDate)
		                             .cvv(cvv)
		                             .holder(holder)
		                             .addCard(true)
		                             .build();
	}

	private void doBind() {
		if(!validate()) {
			return;
		}

		mPanelTop.showProgress(true);

		final OmnomApplication app = getApp();
		final AcquiringData acquiringData = app.getConfig().getAcquiringData();
		UserData wicketUser = app.getUserProfile().getUser();

		subscribe(mAcquiring.addCard(acquiringData, wicketUser, createCardInfo()), new Action1<AcquiringResponse>() {
			@Override
			public void call(final AcquiringResponse acquiringResponse) {
				if(acquiringResponse.getError() != null) {
					showToast(getActivity(), acquiringResponse.getError().getDescr());
				} else {
					setResult(RESULT_OK);
					finish();
				}
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(final Throwable throwable) {
				showToast(getActivity(), R.string.unable_to_bind_card);
				Log.e(TAG, "doBind", throwable);
			}
		});
	}

	private void doPay() {
		if(!validate()) {
			return;
		}
		Intent intent = new Intent();
		intent.putExtra(EXTRA_CARD_DATA, createCardInfo());
		setResult(CardsActivity.RESULT_PAY, intent);
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	private boolean validate() {
		final String pan = mEditCardNumber.getText().toString();
		final String expDate = mEditCardExpDate.getText().toString();
		final String cvv = mEditCardCvv.getText().toString();
		return panValidator.validate(pan) &&
				expDateValidator.validate(expDate) &&
				cvvValidator.validate(cvv);
	}

	@OnClick({R.id.img_camera, R.id.txt_camera})
	public void onImgCamera(View view) {
		Intent scanIntent = new Intent(this, CardIOActivity.class);
		scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN, getString(R.string.cardio_app_token));
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, false); //true
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); //true
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false);
		scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true);
		scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, true);
		scanIntent.putExtra(CardIOActivity.EXTRA_USE_CARDIO_LOGO, true);
		startActivityForResult(scanIntent, REQUEST_CODE_CARD_IO);
	}

	@OnCheckedChanged(R.id.check_save_card)
	public void onCheckSaveCard(final boolean checked) {
		if(checked) {
			bindMode();
		} else {
			payMode();
		}
	}

	private void bindMode() {
		mPanelTop.setButtonRight(R.string.bind, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				doBind();
			}
		});
	}

	private void payMode() {
		mPanelTop.setButtonRight(R.string.pay, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				doPay();
			}
		});
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_card_add;
	}

	@Override
	protected void handleIntent(Intent intent) {
		super.handleIntent(intent);
		mType = intent.getIntExtra(EXTRA_TYPE, TYPE_BIND);
	}

}
