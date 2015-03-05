package com.omnom.android.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.utils.ObservableUtils;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.view.HeaderView;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

public class ConfirmPhoneActivity extends BaseOmnomActivity {

	public static final int TYPE_LOGIN = 0;

	public static final int TYPE_REGISTER = 1;

	public static final int TYPE_DEFAULT = -1;

	private static final String TAG = ConfirmPhoneActivity.class.getSimpleName();

	private class Watcher implements TextWatcher {
		private EditText mEditText;

		private Watcher(EditText editText) {
			mEditText = editText;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(s.length() > 0) {
				final int nextFocusForwardId = mEditText.getNextFocusForwardId();
				if(nextFocusForwardId != View.NO_ID) {
					findViewById(nextFocusForwardId).requestFocus();
				}
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	}

	private class OnKeyListener implements View.OnKeyListener {
		private EditText mEditText;

		private OnKeyListener(EditText editText) {
			mEditText = editText;
		}

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
				final int nextFocusLeftId = mEditText.getNextFocusLeftId();
				mEditText.setText(StringUtils.EMPTY_STRING);
				if(nextFocusLeftId != View.NO_ID) {
					EditText editText = (EditText) findViewById(nextFocusLeftId);
					editText.setText(StringUtils.EMPTY_STRING);
					editText.requestFocus();
				}
				return true;
			}
			return false;
		}
	}

	@InjectView(R.id.text)
	protected TextView text;

	@InjectView(R.id.digit_1)
	protected EditText edit1;

	@InjectView(R.id.digit_2)
	protected EditText edit2;

	@InjectView(R.id.digit_3)
	protected EditText edit3;

	@InjectView(R.id.digit_4)
	protected EditText edit4;

	@InjectView(R.id.panel_digits)
	protected View panelDigits;

	@InjectView(R.id.panel_top)
	protected HeaderView topPanel;

	@InjectView(R.id.btn_request_code)
	protected Button btnRequestCode;

	private String phone;

	private boolean mFirstStart = true;

	private int type;

	private Subscription mConfirmSubscription;

	private Subscription mRequestCodeSubscription;

	@Override
	public void initUi() {
		topPanel.setRigthButtonVisibile(false);
		topPanel.setTitle(R.string.enter);
		topPanel.setContentVisibility(false, true);
		topPanel.setPaging(UserRegisterActivity.FAKE_PAGE_COUNT, 1);

		edit1.addTextChangedListener(new Watcher(edit1));
		edit2.addTextChangedListener(new Watcher(edit2));
		edit3.addTextChangedListener(new Watcher(edit3));
		edit4.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length() > 0) {
					doConfirm();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		edit2.setOnKeyListener(new OnKeyListener(edit2));
		edit3.setOnKeyListener(new OnKeyListener(edit3));
		edit4.setOnKeyListener(new OnKeyListener(edit4));
		text.setText(getString(R.string.confirm_code_sms_text, phone));
		AndroidUtils.showKeyboard(edit1);
	}

	private void doConfirm() {
		btnRequestCode.setEnabled(false);
		mConfirmSubscription = AndroidObservable.bindActivity(this, getAuthObservable()).subscribe(new Action1<AuthResponse>() {
			@Override
			public void call(final AuthResponse authResponse) {
				if(!authResponse.hasError()) {
					((OmnomApplication) getApplication()).cacheAuthToken(authResponse.getToken());
					topPanel.setContentVisibility(false, false);
					finish();
					EnteringActivity.start(ConfirmPhoneActivity.this, R.anim.fake_fade_in_instant, R.anim.fake_fade_out_instant, 0, type);
				} else {
					edit1.setText(StringUtils.EMPTY_STRING);
					edit2.setText(StringUtils.EMPTY_STRING);
					edit3.setText(StringUtils.EMPTY_STRING);
					edit4.setText(StringUtils.EMPTY_STRING);
					edit1.requestFocus();
					final Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
					panelDigits.startAnimation(animation);
				}
				btnRequestCode.setEnabled(true);
			}
		}, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
			@Override
			public void onError(Throwable throwable) {
				Log.e(TAG, "doConfirm ", throwable);
				finish();
			}
		});
	}

	private Observable<AuthResponse> getAuthObservable() {
		Observable<AuthResponse> observable;
		if(type == TYPE_REGISTER) {
			observable = authenticator.confirm(phone, getCode());
		} else if(type == TYPE_LOGIN) {
			observable = authenticator.authorizePhone(phone, getCode());
		} else {
			throw new RuntimeException("Wrong confirm type = " + type);
		}
		return observable;
	}

	private String getCode() {
		return edit1.getText().toString() + edit2.getText().toString() + edit3.getText().toString() + edit4.getText().toString();
	}

	@Override
	protected void handleIntent(Intent intent) {
		super.handleIntent(intent);
		phone = intent.getStringExtra(EXTRA_PHONE);
		type = intent.getIntExtra(EXTRA_CONFIRM_TYPE, TYPE_REGISTER);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OmnomObservable.unsubscribe(mConfirmSubscription);
		OmnomObservable.unsubscribe(mRequestCodeSubscription);
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
		if(mFirstStart) {
			startRequestCodeTimeout();
		}
		mFirstStart = false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		AndroidUtils.hideKeyboard(this);
	}

	@OnClick(R.id.btn_request_code)
	protected void onRequestCode() {
		mRequestCodeSubscription =
				AndroidObservable.bindActivity(this, getRequestCodeObservable()).subscribe(new Action1<AuthResponse>() {
					@Override
					public void call(AuthResponse authResponse) {
						// handle result if necessary
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						Log.w(TAG, "onRequestCode", throwable);
					}
				});
		startRequestCodeTimeout();
	}

	private Observable<AuthResponse> getRequestCodeObservable() {
		Observable<AuthResponse> observable;
		if(type == TYPE_REGISTER) {
			observable = authenticator.confirmResend(phone);
		} else if(type == TYPE_LOGIN) {
			observable = authenticator.authorizePhone(phone, StringUtils.EMPTY_STRING);
		} else {
			throw new RuntimeException("Wrong confirm type = " + type);
		}
		return observable;
	}

	private void startRequestCodeTimeout() {
		btnRequestCode.setEnabled(false);
		postDelayed(getResources().getInteger(R.integer.default_sms_request_timeout), new Runnable() {
			@Override
			public void run() {
				btnRequestCode.setEnabled(true);
			}
		});
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_confirm_phone;
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
}
