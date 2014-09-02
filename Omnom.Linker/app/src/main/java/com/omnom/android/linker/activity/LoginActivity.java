package com.omnom.android.linker.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.omnom.android.linker.LinkerApplication;
import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.activity.base.Extras;
import com.omnom.android.linker.api.observable.LinkerObeservableApi;
import com.omnom.android.linker.utils.AndroidUtils;
import com.omnom.android.linker.utils.AnimationUtils;
import com.omnom.android.linker.utils.StringUtils;
import com.omnom.android.linker.utils.UserDataHolder;
import com.omnom.android.linker.utils.ViewUtils;
import com.omnom.android.linker.widget.ErrorEditText;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.functions.Action1;

import static com.omnom.android.linker.utils.AndroidUtils.showToast;
import static com.omnom.android.linker.utils.ViewUtils.getTextValue;

public class LoginActivity extends BaseActivity {
	private static final String TAG = LoginActivity.class.getSimpleName();

	public static void start(final Context context) {
		start(context, null);
	}

	public static void start(final Context context, UserDataHolder dataHolder) {
		start(context, dataHolder, Extras.EXTRA_ERROR_AUTHTOKEN_EXPIRED);
	}

	public static void start(final Context context, UserDataHolder dataHolder, int error) {
		Intent intent = new Intent(context, LoginActivity.class);
		if(dataHolder != null) {
			intent.putExtra(Extras.EXTRA_USERNAME, dataHolder.getUsername());
			intent.putExtra(Extras.EXTRA_PASSWORD, dataHolder.getPassword());
		}
		intent.putExtra(Extras.EXTRA_ERROR_CODE, error);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	private static class ErrorTextWatcher implements TextWatcher {
		private final ErrorEditText view;
		private final TextView      errView;
		private       LoginActivity activity;

		private ErrorTextWatcher(LoginActivity activity, ErrorEditText view, TextView errView) {
			this.activity = activity;
			this.view = view;
			this.errView = errView;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			activity.clearError(view, errView);
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	}

	@InjectView(R.id.edit_email)
	protected ErrorEditText mEditLogin;

	@InjectView(R.id.edit_password)
	protected ErrorEditText mEditPassword;

	@InjectView(R.id.txt_email_error)
	protected TextView mTextLoginError;

	@InjectView(R.id.txt_password_error)
	protected TextView mTextPasswordError;

	@InjectView(R.id.panel_top)
	protected View mPanelTop;

	@InjectView(R.id.panel_bottom)
	protected View mPanelBottom;

	@Inject
	protected LinkerApplication app;

	@Inject
	protected LinkerObeservableApi api;

	@Override
	public void initUi() {
		if(getIntent() != null) {
			mEditLogin.setText(getIntent().getStringExtra(EXTRA_USERNAME));
			mEditPassword.setText(getIntent().getStringExtra(EXTRA_PASSWORD));
			int errorCode = getIntent().getIntExtra(EXTRA_ERROR_CODE, -1);
			onAuthError(errorCode);
		}

		mEditLogin.addTextChangedListener(new ErrorTextWatcher(this, mEditLogin, mTextLoginError));
		ViewUtils.fixPasswordTypeface(mEditPassword, mEditLogin);
		mEditPassword.addTextChangedListener(new ErrorTextWatcher(this, mEditPassword, mTextPasswordError));
		mEditPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if(id == R.id.login || id == EditorInfo.IME_NULL) {
					performLogin();
					return true;
				}
				return false;
			}
		});
		mPanelTop.postDelayed(new Runnable() {
			@Override
			public void run() {
				AnimationUtils.animateAlpha(mPanelTop, true);
				AnimationUtils.animateAlpha(mPanelBottom, true);
			}
		}, getResources().getInteger(R.integer.login_animation_duration));
	}

	private void onAuthError(int errorCode) {
		if(errorCode != -1) {
			switch(errorCode) {
				case EXTRA_ERROR_WRONG_PASSWORD:
					setError(mEditPassword, mTextPasswordError, R.string.error_invalid_password);
					break;

				case EXTRA_ERROR_WRONG_USERNAME:
					setError(mEditLogin, mTextLoginError, R.string.error_invalid_email);
					break;
			}
		}
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_login;
	}

	@OnClick(R.id.btn_remind_password)
	protected void performRemindPassword() {
		if(validate(R.string.please_enter_username, mEditLogin)) {
			api.remindPassword(getTextValue(mEditLogin)).subscribe(new Action1<String>() {
				@Override
				public void call(String result) {
					showToast(LoginActivity.this, R.string.remind_password_sent);
				}
			});
		}
	}

	@OnClick(R.id.btn_login)
	protected void performLogin() {
		if(validate(R.string.error_email_and_password_required, mEditLogin, mEditPassword)) {
			final Intent intent = new Intent(this, ValidationActivity.class);
			intent.putExtra(EXTRA_USERNAME, getTextValue(mEditLogin));
			intent.putExtra(EXTRA_PASSWORD, getTextValue(mEditPassword));
			startActivity(intent);
			finish();
		}
	}

	private void setError(ErrorEditText view, TextView errView, int resId) {
		view.setError(true);
		errView.setVisibility(View.VISIBLE);
		errView.setText(resId);
	}

	private void clearError(ErrorEditText view, TextView errView) {
		view.setError(false);
		errView.setText(StringUtils.EMPTY_STRING);
	}

	private boolean validate(int errResId, EditText... views) {
		if(!AndroidUtils.hasConnection(this)) {
			showToast(this, R.string.please_check_internet_connection);
			return false;
		}
		for(EditText edit : views) {
			if(TextUtils.isEmpty(edit.getText().toString().trim())) {
				showToast(this, errResId);
				return false;
			}
		}
		return true;
	}
}

