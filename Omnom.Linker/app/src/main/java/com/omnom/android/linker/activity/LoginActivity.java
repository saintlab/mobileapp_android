package com.omnom.android.linker.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.omnom.android.linker.BuildConfig;
import com.omnom.android.linker.LinkerApplication;
import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.api.observable.LinkerObeservableApi;
import com.omnom.android.linker.utils.AndroidUtils;
import com.omnom.android.linker.utils.StringUtils;
import com.omnom.android.linker.utils.ViewUtils;
import com.omnom.android.linker.widget.ErrorEditText;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import rx.functions.Action0;
import rx.functions.Action1;

import static com.omnom.android.linker.utils.AndroidUtils.showToast;
import static com.omnom.android.linker.utils.ViewUtils.getTextValue;

public class LoginActivity extends BaseActivity {

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

	@Inject
	protected LinkerObeservableApi api;

	@Inject
	protected LinkerApplication app;

	@Override
	public void initUi() {
		mEditLogin.addTextChangedListener(new ErrorTextWatcher(this, mEditLogin, mTextLoginError));
		ViewUtils.fixPasswordTypeface(mEditPassword);
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
		if(!validate()) {
			return;
		}
		mViewConnecting.setVisibility(View.VISIBLE);
		ButterKnife.apply(loginViews, ViewUtils.ENABLED, false);

		api.authenticate(getTextValue(mEditLogin), getTextValue(mEditPassword)).subscribe(new Action1<String>() {
			@Override
			public void call(String result) {
				startActivity(ValidationActivity.class);
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				// TODO:
				setError(mEditPassword, mTextPasswordError, R.string.error_invalid_password);
				mViewConnecting.setVisibility(View.GONE);
				Log.e(TAG, "authenticate()", throwable);
			}
		}, new Action0() {
			@Override
			public void call() {
				ButterKnife.apply(loginViews, ViewUtils.ENABLED, true);
			}
		});
	}

	private void setError(ErrorEditText view, TextView errView, int resId) {
		ButterKnife.apply(loginViews, ViewUtils.ENABLED, true);
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

