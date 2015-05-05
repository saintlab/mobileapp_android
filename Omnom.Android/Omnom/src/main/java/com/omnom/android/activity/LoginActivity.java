package com.omnom.android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.auth.AuthError;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.request.AuthRegisterRequest;
import com.omnom.android.auth.response.AuthRegisterResponse;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.utils.ObservableUtils;
import com.omnom.android.utils.activity.OmnomActivity;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.ClickSpan;
import com.omnom.android.utils.utils.ErrorUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.view.ErrorEdit;
import com.omnom.android.view.HeaderView;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action1;

public class LoginActivity extends BaseOmnomActivity {

	private static final String TAG = LoginActivity.class.getSimpleName();

	public static void start(OmnomActivity activity, String phone) {
		final Intent intent = new Intent(activity.getActivity(), LoginActivity.class);
		intent.putExtra(EXTRA_PHONE, phone);
		activity.start(intent, R.anim.slide_in_right, R.anim.slide_out_left, true);
	}

	public static void start(OmnomActivity activity, String phone, int requestCode) {
		final Intent intent = new Intent(activity.getActivity(), LoginActivity.class);
		intent.putExtra(EXTRA_PHONE, phone);
		activity.startForResult(intent, R.anim.slide_in_right, R.anim.slide_out_left, requestCode);
	}

	@InjectView(R.id.edit_phone)
	protected ErrorEdit editPhone;

	@InjectView(R.id.panel_top)
	protected HeaderView topPanel;

	@InjectView(R.id.txt_info)
	protected TextView txtInfo;

	@Inject
	protected AuthService authenticator;

	private boolean mFirstStart = true;

	private String mPhone;

	private Subscription mProceedSubscription;

	private Subscription mRegisterSubscription;

	@Override
	public void initUi() {
		topPanel.setBackgroundColor(Color.WHITE);
		topPanel.setTitle(R.string.enter);
		topPanel.setButtonRight(R.string.proceed, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doProceed();
			}
		});
		topPanel.setButtonLeftDrawable(R.drawable.ic_cross_black, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				onBackPressed();
			}
		});
		topPanel.setPaging(UserRegisterActivity.FAKE_PAGE_COUNT, 0);
		topPanel.setContentVisibility(false, true);
		editPhone.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					doProceed();
					return true;
				}
				return false;
			}
		});

		AndroidUtils.clickify(txtInfo, false, getString(R.string.license_agreement_clickable), new ClickSpan.OnClickListener() {
			@Override
			public void onClick() {
				AndroidUtils.openBrowser(getActivity(),
				                         getString(R.string.register_agreement_url),
				                         getString(R.string.register_agreement_fail));
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(mFirstStart) {
			postDelayed(getResources().getInteger(android.R.integer.config_longAnimTime) + 200, new Runnable() {
				@Override
				public void run() {
					final EditText editText = editPhone.getEditText();
					String value = mPhone;
					if(value == null) {
						value = AndroidUtils.getDevicePhoneNumber(getActivity(), R.string.phone_country_code);
					}
					editText.setText(value);
					AndroidUtils.moveCursorEnd(editText);
					AndroidUtils.showKeyboard(editText);
				}
			});
		}

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
	protected void onPause() {
		super.onPause();
		AndroidUtils.hideKeyboard(this);
	}

	@Override
	protected void handleIntent(Intent intent) {
		mPhone = intent.getStringExtra(EXTRA_PHONE);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_LOGIN_CONFIRM && resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}

	private void doProceed() {
		if(!validate() || isBusy()) {
			return;
		}

		doLogin();
	}

	private void doLogin() {
		busy(true);
		topPanel.showProgress(true);

		mProceedSubscription = AppObservable.bindActivity(this, authenticator.authorizePhone(editPhone.getText(),
		                                                                                     StringUtils.EMPTY_STRING))
		                                    .subscribe(new Action1<AuthResponse>() {
			                                    @Override
			                                    public void call(AuthResponse authResponse) {
				                                    topPanel.showProgress(false);
				                                    busy(false);

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
							                                    startForResult(intent,
							                                                   R.anim.slide_in_right,
							                                                   R.anim.slide_out_left,
							                                                   REQUEST_CODE_LOGIN_CONFIRM);
						                                    }
					                                    });
				                                    } else {
					                                    doRegister();
					                                    //					                                    final AuthError error =
					                                    // authResponse.getError();
					                                    //					                                    if(error != null) {
					                                    //						                                    showError(error
					                                    // .getMessage());
					                                    //					                                    }
				                                    }
			                                    }
		                                    }, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
			                                    @Override
			                                    public void onError(Throwable throwable) {
				                                    Log.e(TAG, ":authorizePhone doProceed ", throwable);
				                                    if(ErrorUtils.isConnectionError(throwable)) {
					                                    showError(getString(R.string.err_no_internet));
				                                    } else {
					                                    showError(getString(R.string.something_went_wrong));
				                                    }
			                                    }
		                                    });
	}

	private void doRegister() {
		busy(true);
		topPanel.showProgress(true);

		final AuthRegisterRequest request = AuthRegisterRequest.create(AndroidUtils.getInstallId(this),
		                                                               StringUtils.EMPTY_STRING,
		                                                               StringUtils.EMPTY_STRING,
		                                                               StringUtils.EMPTY_STRING,
		                                                               editPhone.getText(),
		                                                               StringUtils.EMPTY_STRING);
		mRegisterSubscription =
				AppObservable.bindActivity(this, authenticator.register(request)).subscribe(new Action1<AuthRegisterResponse>() {
					@Override
					public void call(final AuthRegisterResponse authRegisterResponse) {
						busy(false);
						if(!authRegisterResponse.hasError()) {
							topPanel.setContentVisibility(false, false);
							postDelayed(getResources().getInteger(R.integer.default_animation_duration_short), new Runnable() {
								@Override
								public void run() {
									final Intent intent = new Intent(LoginActivity.this,
									                                 ConfirmPhoneActivity.class);
									intent.putExtra(EXTRA_PHONE, editPhone.getText());
									intent.putExtra(EXTRA_CONFIRM_TYPE, ConfirmPhoneActivity.TYPE_REGISTER);
									startForResult(intent,
									               R.anim.slide_in_right,
									               R.anim.slide_out_left,
									               REQUEST_CODE_LOGIN_CONFIRM);
									topPanel.showProgress(false);
								}
							});
						} else {
							handleRegisterError(authRegisterResponse);
						}
					}
				}, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
					@Override
					public void onError(Throwable throwable) {
						busy(false);
						if(ErrorUtils.isConnectionError(throwable)) {
							showError(getString(R.string.err_no_internet));
						}
						topPanel.showProgress(false);
						Log.e(TAG, "doRegister ", throwable);
					}
				});
	}

	private void handleRegisterError(AuthRegisterResponse authRegisterResponse) {
		topPanel.showProgress(false);
		final AuthError error = authRegisterResponse.getError();
		switch(error.getCode()) {
			//case 107:
			// doLogin();
			//	break;

			default:
				showError(error.getMessage());
		}
	}

	private void showError(final String message) {
		editPhone.setError(message);
		topPanel.showProgress(false);
		busy(false);
	}

	@Override
	protected void onStop() {
		super.onStop();
		topPanel.showProgress(false);
		busy(false);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OmnomObservable.unsubscribe(mProceedSubscription);
		OmnomObservable.unsubscribe(mRegisterSubscription);
	}

	private boolean validate() {
		if(TextUtils.isEmpty(editPhone.getText())) {
			showError(getString(R.string.you_forgot_to_enter_phone));
			return false;
		}
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_login;
	}
}
