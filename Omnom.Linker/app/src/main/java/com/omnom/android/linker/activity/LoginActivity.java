package com.omnom.android.linker.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.omnom.android.linker.LinkerApplication;
import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.utils.AndroidUtils;
import com.omnom.android.linker.utils.StringUtils;
import com.omnom.android.linker.utils.ViewUtils;
import com.omnom.android.linker.widget.ErrorEditText;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

import static com.omnom.android.linker.utils.AndroidUtils.showToast;
import static com.omnom.android.linker.utils.ViewUtils.getTextValue;

public class LoginActivity extends BaseActivity {

	public static final String EXTRA_USERNAME   = "com.omnom.android.linker.username";
	public static final String EXTRA_PASSWORD   = "com.omnom.android.linker.password";
	public static final String EXTRA_ERROR_CODE = "com.omnom.android.linker.error.code";

	public static final int EXTRA_ERROR_WRONG_PASSWORD = 0;
	public static final int EXTRA_ERROR_WRONG_USERNAME = 1;

	private static final String TAG = LoginActivity.class.getSimpleName();

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

	@Inject
	protected LinkerApplication app;

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

	@OnClick(R.id.btn_login)
	protected void performLogin() {
		if(!validate()) {
			return;
		}
		final Intent intent = new Intent(this, ValidationActivity.class);
		intent.putExtra(EXTRA_USERNAME, getTextValue(mEditLogin));
		intent.putExtra(EXTRA_PASSWORD, getTextValue(mEditPassword));
		startActivity(intent);
		finish();
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

	private boolean validate() {
		if(!AndroidUtils.hasConnection(this)) {
			showToast(this, R.string.please_check_internet_connection);
			return false;
		}
		String email = mEditLogin.getText().toString().trim();
		String password = mEditPassword.getText().toString().trim();
		if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
			showToast(this, R.string.error_email_and_password_required);
			return false;
		}
		return true;
	}
}

