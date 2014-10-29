package com.omnom.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MerchantData;
import com.omnom.android.acquiring.mailru.model.UserData;
import com.omnom.android.acquiring.mailru.response.CardRegisterPollingResponse;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.utils.CardExpirationTextWatcher;
import com.omnom.android.utils.CardNumberTextWatcher;
import com.omnom.android.utils.CardUtils;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.view.LoginPanelTop;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import rx.functions.Action1;

public class AddCardActivity extends BaseOmnomActivity implements TextListener {

	private static final int REQUEST_CODE_CARD_IO = 101;

	@SuppressLint("NewApi")
	public static void start(Activity activity) {
		final Intent intent = new Intent(activity, AddCardActivity.class);
		if(AndroidUtils.isJellyBean()) {
			Bundle extras = ActivityOptions.makeCustomAnimation(activity,
			                                                    R.anim.slide_in_right,
			                                                    R.anim.slide_out_left).toBundle();
			activity.startActivity(intent, extras);
		} else {
			activity.startActivity(intent);
		}
	}

	@Inject
	protected Acquiring mAcquiring;

	@InjectView(R.id.panel_top)
	protected LoginPanelTop mPanelTop;

	@InjectView(R.id.txt_card_number)
	protected EditText mEditCardNumber;

	@InjectView(R.id.txt_exp_date)
	protected EditText mEditCardExpDate;

	@InjectView(R.id.txt_cvv)
	protected EditText mEditCardCvv;

	@InjectView(R.id.img_camera)
	protected ImageView mImgCamera;

	@InjectView(R.id.panel_card)
	protected View mPanelCard;

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
		mPanelTop.setButtonRight(R.string.ready, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				doProceed();
			}
		});
		mPanelTop.setButtonLeft(R.string.cancel, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				finish();
			}
		});

		mEditCardExpDate.addTextChangedListener(new CardExpirationTextWatcher(mEditCardExpDate, this));
		mEditCardNumber.addTextChangedListener(new CardNumberTextWatcher(mEditCardNumber, this));

		mEditCardCvv.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
			}

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
			}

			@Override
			public void afterTextChanged(final Editable s) {
				AddCardActivity.this.onTextChanged(s.toString());
			}
		});
	}

	@Override
	public void onTextChanged(final CharSequence s) {
		animteCamera(s.length() > 0);
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
				mImgCamera.animate().translationYBy(v).start();
				mEditCardCvv.animate().translationY(v).start();
				mEditCardExpDate.animate().translationY(v).start();
				mEditCardNumber.animate().translationY(v).start();
				mImgCamera.setBackgroundDrawable(getResources().getDrawable(R.drawable.scan_frame));
			}
			mMinimized = minimize;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_CARD_IO) {
			if(data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
				final CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
				postDelayed(350, new Runnable() {
					@Override
					public void run() {
						mEditCardNumber.setText(scanResult.cardNumber);
					}
				});
			}
		}
	}

	private void doProceed() {
		final String pan = CardUtils.preparePan(mEditCardNumber.getText().toString());
		final String expDate = CardUtils.prepareExpDare(mEditCardExpDate.getText().toString());
		final String cvv = mEditCardCvv.getText().toString();

		final CardInfo card = CardInfo.create(pan, expDate, cvv);

		final MerchantData merchant = new MerchantData(this);
		com.omnom.android.auth.UserData wicketUser = OmnomApplication.get(this).getUserProfile().getUser();
		final UserData user = UserData.create(String.valueOf(wicketUser.getId()), wicketUser.getPhone());
		mAcquiring.registerCard(merchant, user, card).subscribe(new Action1<CardRegisterPollingResponse>() {
			@Override
			public void call(CardRegisterPollingResponse response) {
				card.setCardId(response.getCardId());
				// verifyCard(card, user, merchant);
				// TODO: start ConfirmCardActivity(cardInfo)
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(final Throwable throwable) {

			}
		});
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
