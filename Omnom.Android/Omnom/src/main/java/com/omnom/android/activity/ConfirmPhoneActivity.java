package com.omnom.android.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.omnom.android.MainActivity;
import com.omnom.android.R;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.view.ViewPagerIndicatorCircle;
import com.omnom.util.activity.BaseActivity;
import com.omnom.util.utils.AndroidUtils;
import com.omnom.util.utils.StringUtils;
import com.omnom.util.utils.ViewUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import rx.Observable;
import rx.functions.Action1;

public class ConfirmPhoneActivity extends BaseActivity {

	public static final int TYPE_LOGIN = 0;
	public static final int TYPE_REGISTER = 1;

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

	@InjectView(R.id.btn_right)
	protected Button btnRight;

	@InjectView(R.id.title)
	protected TextView textTitle;

	@InjectViews({R.id.title, R.id.page_indicator, R.id.btn_right})
	protected List<View> topViews;

	@InjectView(R.id.page_indicator)
	protected ViewPagerIndicatorCircle pageIndicator;

	@Inject
	protected AuthService authenticator;

	private String phone;
	private boolean mFirstStart = true;
	private int type;

	@Override
	public void initUi() {
		ViewUtils.setVisible(btnRight, false);
		textTitle.setText(R.string.enter);

		ButterKnife.apply(topViews, ViewUtils.VISIBLITY_ALPHA_NOW, false);
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
		text.setText(getString(R.string.confirm_code_sms_text, phone));
		pageIndicator.setFake(true, UserRegisterActivity.FAKE_PAGE_COUNT);
		pageIndicator.setCurrentItem(1);
		AndroidUtils.showKeyboard(edit1);
	}

	private void doConfirm() {
		Observable<AuthResponse> observable;
		if(type == TYPE_REGISTER) {
			observable = authenticator.confirm(phone, getCode());
		} else if(type == TYPE_LOGIN) {
			observable = authenticator.authorizePhone(phone, getCode());
		} else {
			throw new RuntimeException("Wrong confirm type = " + type);
		}

		observable.subscribe(new Action1<AuthResponse>() {
			@Override
			public void call(final AuthResponse authResponse) {
				if(!authResponse.hasError()) {
					getPreferences().setAuthToken(getActivity(), authResponse.getToken());
					ButterKnife.apply(topViews, ViewUtils.VISIBLITY_ALPHA, false);
					postDelayed(350, new Runnable() {
						@Override
						public void run() {
							final Intent intent = new Intent(ConfirmPhoneActivity.this, MainActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent, R.anim.slide_in_right, R.anim.slide_out_left, true);
						}
					});
				} else {
					edit1.setText(StringUtils.EMPTY_STRING);
					edit2.setText(StringUtils.EMPTY_STRING);
					edit3.setText(StringUtils.EMPTY_STRING);
					edit4.setText(StringUtils.EMPTY_STRING);
					edit1.requestFocus();
					final Animation animation = android.view.animation.AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
					panelDigits.startAnimation(animation);
				}
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				Log.e(TAG, "doConfirm", throwable);
				finish();
			}
		});
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
	protected void onResume() {
		super.onResume();
		if(pageIndicator.getAlpha() == 0) {
			pageIndicator.postDelayed(new Runnable() {
				@Override
				public void run() {
					ButterKnife.apply(topViews, ViewUtils.VISIBLITY_ALPHA, true);
				}

			}, mFirstStart ? getResources().getInteger(android.R.integer.config_longAnimTime) :
					                          getResources().getInteger(android.R.integer.config_mediumAnimTime));
		}
		mFirstStart = false;
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
