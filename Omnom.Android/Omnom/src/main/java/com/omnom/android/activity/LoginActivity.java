package com.omnom.android.activity;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.utils.ObservableUtils;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.UserDataHolder;
import com.omnom.android.utils.view.ErrorEdit;
import com.omnom.android.view.HeaderView;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

public class LoginActivity extends BaseOmnomActivity {

	private static final String TAG = LoginActivity.class.getSimpleName();

	public static void start(Context context, UserDataHolder dataHolder) {
		throw new RuntimeException("IMPLEMENT");
	}

	@InjectView(R.id.edit_phone)
	protected ErrorEdit editPhone;

	@InjectView(R.id.panel_top)
	protected HeaderView topPanel;

	@Inject
	protected AuthService authenticator;

	private boolean mFirstStart = true;

	private Subscription mProceedSubscription;

	@Override
	public void initUi() {
		final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		final String mPhoneNumber = telephonyManager.getLine1Number();
		if(!TextUtils.isEmpty(mPhoneNumber)) {
			final EditText editText = editPhone.getEditText();
			editText.setText(mPhoneNumber);
			editText.setSelection(editText.getText().length());
		}

		topPanel.setTitle(R.string.enter);
		topPanel.setButtonRight(R.string.proceed, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doProceed(v);
			}
		});
		topPanel.setPaging(UserRegisterActivity.FAKE_PAGE_COUNT, 0);
		topPanel.setContentVisibility(false, true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(topPanel.isAlphaVisible()) {
			topPanel.postDelayed(new Runnable() {
				@Override
				public void run() {
					topPanel.setContentVisibility(true, false);
				}

			}, mFirstStart ? getResources().getInteger(android.R.integer.config_longAnimTime) :
					                     getResources().getInteger(android.R.integer.config_mediumAnimTime));
		}
		mFirstStart = false;
	}

	@Override
	protected void handleIntent(Intent intent) {
	}

	public void doProceed(final View view) {
		if(!validate()) {
			return;
		}
		topPanel.showProgress(true);
		mProceedSubscription = AndroidObservable.bindActivity(this, authenticator.authorizePhone(editPhone.getText(),
		                                                                                         StringUtils.EMPTY_STRING))
		                                        .subscribe(new Action1<AuthResponse>() {
			                                        @Override
			                                        public void call(AuthResponse authResponse) {
				                                        if(!authResponse.hasError()) {
					                                        topPanel.setContentVisibility(false, false);
					                                        postDelayed(getResources().getInteger(
							                                        R.integer.default_animation_duration_short), new Runnable() {
						                                        @Override
						                                        public void run() {
							                                        final Intent intent = new Intent(LoginActivity.this,
							                                                                         ConfirmPhoneActivity.class);
							                                        intent.putExtra(EXTRA_PHONE, editPhone.getText());
							                                        intent.putExtra(EXTRA_CONFIRM_TYPE, ConfirmPhoneActivity.TYPE_LOGIN);
							                                        startActivity(intent, R.anim.slide_in_right, R.anim.slide_out_left,
							                                                      false);
							                                        topPanel.showProgress(false);
						                                        }
					                                        });
				                                        } else {
					                                        editPhone.setError(authResponse.getError().getMessage());
					                                        topPanel.showProgress(false);
				                                        }
			                                        }
		                                        }, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
			                                        @Override
			                                        public void onError(Throwable throwable) {
				                                        topPanel.showProgress(false);
				                                        Log.e(TAG + ":authorizePhone", "doProceed", throwable);
			                                        }
		                                        });
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.fake_fade_out_long, R.anim.slide_out_down);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OmnomObservable.unsubscribe(mProceedSubscription);
	}

	private boolean validate() {
		// TODO: check
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_login;
	}
}
