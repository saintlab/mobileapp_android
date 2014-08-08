package com.omnom.android.linker.activity;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.omnom.android.linker.BuildConfig;
import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.utils.AndroidUtils;
import com.omnom.android.linker.utils.StringUtils;
import com.omnom.android.linker.utils.ViewUtils;
import com.omnom.android.linker.widget.ErrorEditText;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

import static com.omnom.android.linker.utils.AndroidUtils.showToast;
import static com.omnom.android.linker.utils.AndroidUtils.showToastLong;

public class LoginActivity extends BaseActivity {

	private static class ErrorTextWatcher implements TextWatcher {

		private LoginActivity activity;
		private final ErrorEditText view;
		private final TextView errView;

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

	private static class LoginAsyncTask extends AsyncTask<String, Integer, Integer> {
		private static final int RESULT_CODE_SUCCESS = 0;
		private static final int RESULT_CODE_LOGIN_ERROR = 1;
		private static final int RESULT_CODE_PASSWORD_ERROR = 2;
		private static final int RESULT_CODE_SERVER_UNAVAILABLE = 3;

		private LoginActivity activity;

		private LoginAsyncTask(LoginActivity activity) {
			this.activity = activity;
		}

		@Override
		protected void onPreExecute() {
			activity.mViewConnecting.setVisibility(View.VISIBLE);
			ButterKnife.apply(activity.loginViews, ENABLED, false);
		}

		@Override
		protected Integer doInBackground(String... params) {
			assert params.length == 2;
			String login = params[0];
			String password = params[1];

			SystemClock.sleep(2000);

			return RESULT_CODE_SUCCESS;
		}

		@Override
		protected void onPostExecute(Integer result) {
			activity.mViewConnecting.setVisibility(View.GONE);
			switch (result) {
				case RESULT_CODE_LOGIN_ERROR:
					activity.setError(activity.mEditLogin, activity.mTextLoginError, R.string.error_invalid_email);
					break;

				case RESULT_CODE_PASSWORD_ERROR:
					activity.setError(activity.mEditPassword, activity.mTextPasswordError, R.string.error_invalid_password);
					break;

				case RESULT_CODE_SERVER_UNAVAILABLE:
					ButterKnife.apply(activity.loginViews, ENABLED, true);
					showToastLong(activity, R.string.server_do_not_respond);
					break;

				case RESULT_CODE_SUCCESS:
					activity.startActivity(ValidationActivity.class);
					break;
			}
		}
	}

	private static final ButterKnife.Setter<View, Boolean> ENABLED = new ButterKnife.Setter<View, Boolean>() {
		@Override
		public void set(View view, Boolean value, int index) {
			view.setEnabled(value);
		}
	};

	@InjectViews({R.id.edit_email, R.id.edit_password, R.id.btn_login, R.id.btn_remind_password})
	protected List<View> loginViews;

	@InjectView(R.id.edit_email)
	protected ErrorEditText mEditLogin;

	@InjectView(R.id.edit_password)
	protected ErrorEditText mEditPassword;

	@InjectView(R.id.txt_email_error)
	protected TextView mTextLoginError;

	@InjectView(R.id.txt_password_error)
	protected TextView mTextPasswordError;

	@InjectView(R.id.view_connecting)
	protected View mViewConnecting;

	@Override
	public void initUi() {
		mEditLogin.addTextChangedListener(new ErrorTextWatcher(this, mEditLogin, mTextLoginError));
		ViewUtils.fixPasswordTypeface(mEditPassword);
		mEditPassword.addTextChangedListener(new ErrorTextWatcher(this, mEditPassword, mTextPasswordError));
		mEditPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					performLogin();
					return true;
				}
				return false;
			}
		});
		if(BuildConfig.DEBUG) {
			mEditPassword.setText("123");
			mEditLogin.setText("123");
		}
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_login;
	}

	@OnClick(R.id.btn_login)
	protected void performLogin() {
		if (!validate()) {
			return;
		}
		new LoginAsyncTask(this).execute(mEditLogin.getText().toString().trim(), mEditPassword.getText().toString().trim());
	}

	private void setError(ErrorEditText view, TextView errView, int resId) {
		ButterKnife.apply(loginViews, ENABLED, true);
		view.setError(true);
		errView.setVisibility(View.VISIBLE);
		errView.setText(resId);
	}

	private void clearError(ErrorEditText view, TextView errView) {
		view.setError(false);
		errView.setText(StringUtils.EMPTY_STRING);
	}

	private boolean validate() {
		if (!AndroidUtils.hasConnection(this)) {
			showToast(this, R.string.please_check_internet_connection);
			return false;
		}
		if (!AndroidUtils.isLocationEnabled(this)) {
			AndroidUtils.startLocationSettings(this);
			return false;
		}
		String email = mEditLogin.getText().toString().trim();
		String password = mEditPassword.getText().toString().trim();
		if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
			showToast(this, R.string.error_email_and_password_required);
			return false;
		}
		return true;
	}
}

