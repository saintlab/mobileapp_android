package com.omnom.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.utils.CardDataTextWatcher;
import com.omnom.android.utils.CardExpirationTextWatcher;
import com.omnom.android.utils.CardNumberTextWatcher;
import com.omnom.android.utils.CardUtils;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.view.ErrorEditText;
import com.omnom.android.view.HeaderView;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class CardAddActivity extends BaseOmnomActivity implements TextListener {

	private static final int REQUEST_CODE_CARD_IO = 101;

	private static final int REQUEST_CODE_CARD_REGISTER = 102;

	@SuppressLint("NewApi")
	public static void start(Activity activity, int code) {
		final Intent intent = new Intent(activity, CardAddActivity.class);
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

	private boolean mMinimized = false;

	private int panelY;

	private int cameraY;

	private int cameraX;

	private int panelX;

	@Override
	public void initUi() {
		mCheckSaveCard.setChecked(true);
		mPanelTop.setButtonRightEnabled(false);
		mPanelTop.setButtonLeft(R.string.cancel, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				finish();
			}
		});

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
	}

	@Override
	public void onTextChanged(final CharSequence s) {
		animteCamera(mEditCardCvv.length() > 0 || mEditCardExpDate.length() > 0 || mEditCardNumber.length() > 0);
		mPanelTop.setButtonRightEnabled(validate(false));
	}

	private void animteCamera(final boolean minimize) {
		int[] sp = new int[2];
		mPanelCard.getLocationOnScreen(sp);
		if(panelY == 0) {
			panelY = sp[1];
		}
		if(panelX == 0) {
			panelX = sp[0];
		}

		mImgCamera.getLocationOnScreen(sp);
		if(cameraY == 0) {
			cameraY = sp[1];
		}
		if(cameraX == 0) {
			cameraX = sp[0];
		}

		if(mMinimized != minimize) {
			final int v = (int) ((cameraY - panelY) / 1.5f);
			if(minimize) {
				mImgCamera.setBackgroundDrawable(null);
				AnimationUtils.animateAlpha(mTextCamera, false);
				mImgCamera.animate().x(mPanelCard.getMeasuredWidth() - mImgCamera.getMeasuredWidth()).start();
				mImgCamera.animate().translationYBy(-v).start();
				mEditCardCvv.animate().translationY(-v).start();
				mEditCardExpDate.animate().translationY(-v).start();
				mEditCardNumber.animate().translationY(-v).start();
			} else {
				AnimationUtils.animateAlpha(mTextCamera, true);
				mImgCamera.animate().x(cameraX - panelX).start();
				mImgCamera.animate().translationY(0).start();
				mEditCardCvv.animate().translationY(0).start();
				mEditCardExpDate.animate().translationY(0).start();
				mEditCardNumber.animate().translationY(0).start();
				mImgCamera.setBackgroundDrawable(getResources().getDrawable(R.drawable.scan_frame));
			}
			mMinimized = minimize;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_CARD_REGISTER && resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
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
		final String expDate = CardUtils.prepareExpDare(mEditCardExpDate.getText().toString());
		final String cvv = mEditCardCvv.getText().toString();
		final String holder = OmnomApplication.get(getActivity()).getConfig().getAcquiringData().getCardHolder();
		return CardInfo.create(pan, expDate, cvv, holder);
	}

	private void doBind() {
		if(!validate(true)) {
			return;
		}
		CardConfirmActivity.startAddConfirm(this, createCardInfo(), REQUEST_CODE_CARD_REGISTER);
	}

	private void doPay() {
		if(!validate(true)) {
			return;
		}
		Intent intent = new Intent();
		intent.putExtra(EXTRA_CARD_DATA, createCardInfo());
		setResult(RESULT_OK, intent);
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	private boolean validate(boolean showErrors) {
		final String pan = mEditCardNumber.getText().toString();
		final String expDate = mEditCardExpDate.getText().toString();
		final String cvv = mEditCardCvv.getText().toString();
		boolean hasErrors = false;

		if(pan.length() < 13) {
			if(showErrors) {
				mEditCardNumber.setError(true);
			}
			hasErrors |= true;
		}
		if(expDate.length() < 5) {
			if(showErrors) {
				mEditCardExpDate.setError(true);
			}
			hasErrors |= true;
		}
		if(cvv.length() < 3) {
			if(showErrors) {
				mEditCardCvv.setError(true);
			}
			hasErrors |= true;
		}
		return !hasErrors;
	}

	@OnClick(R.id.img_camera)
	public void startCardIo(View view) {
		Intent scanIntent = new Intent(this, CardIOActivity.class);
		scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN, getString(R.string.cardio_app_token));
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, false); //true
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); //true
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false);
		scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true);
		startActivityForResult(scanIntent, REQUEST_CODE_CARD_IO);
	}

	@OnCheckedChanged(R.id.check_save_card)
	public void onCheckSaveCard(final boolean checked) {
		if (checked) {
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

}
