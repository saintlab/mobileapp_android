package com.omnom.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.omnom.android.R;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MerchantData;
import com.omnom.android.acquiring.mailru.model.UserData;
import com.omnom.android.acquiring.mailru.response.CardRegisterPollingResponse;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.utils.CardExpirationTextWatcher;
import com.omnom.android.utils.CardNumberTextWatcher;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.view.LoginPanelTop;

import javax.inject.Inject;

import butterknife.InjectView;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import rx.functions.Action1;

public class AddCardActivity extends BaseOmnomActivity {

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

	private CardInfo card;

	private TextWatcher mCardNumberWatcher;

	@Override
	public void initUi() {
		mPanelTop.setButtonRight(R.string.ready, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
			}
		});
		mPanelTop.setButtonLeft(R.string.cancel, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				finish();
			}
		});

		mEditCardExpDate.addTextChangedListener(new CardExpirationTextWatcher(mEditCardExpDate));
		mEditCardNumber.addTextChangedListener(new CardNumberTextWatcher(mEditCardNumber));
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

	public void startCardIo() {
		Intent scanIntent = new Intent(this, CardIOActivity.class);
		scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN, getString(R.string.cardio_app_token));
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true);
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
