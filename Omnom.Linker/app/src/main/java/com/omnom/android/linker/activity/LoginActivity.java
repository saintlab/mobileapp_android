package com.omnom.android.linker.activity;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.omnom.android.linker.R;
import com.omnom.android.linker.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

	private interface ProfileQuery {
		String[] PROJECTION = {ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.IS_PRIMARY,};

		int ADDRESS = 0;
		int IS_PRIMARY = 1;
	}

	private static class ErrorTextWatcher implements TextWatcher {
		private TextView view;

		private ErrorTextWatcher(TextView view) {
			this.view = view;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			view.setText(StringUtils.EMPTY_STRING);
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
			ButterKnife.apply(activity.loginViews, ENABLED, true);
			switch (result) {
				case RESULT_CODE_LOGIN_ERROR:
					activity.mTextEmailError.setText(R.string.error_invalid_email);
					break;

				case RESULT_CODE_PASSWORD_ERROR:
					activity.mTextPasswordError.setText(R.string.error_invalid_password);
					break;

				case RESULT_CODE_SERVER_UNAVAILABLE:
					Toast.makeText(activity, activity.getString(R.string.server_do_not_respond), Toast.LENGTH_LONG).show();
					break;

				case RESULT_CODE_SUCCESS:
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

	@InjectViews({R.id.txt_email, R.id.edit_password, R.id.btn_login, R.id.btn_remind_password})
	protected List<View> loginViews;

	@InjectView(R.id.txt_email)
	protected AutoCompleteTextView mEditEmail;

	@InjectView(R.id.edit_password)
	protected EditText mEditPassword;

	@InjectView(R.id.txt_email_error)
	protected TextView mTextEmailError;

	@InjectView(R.id.txt_password_error)
	protected TextView mTextPasswordError;

	@InjectView(R.id.view_connecting)
	protected View mViewConnecting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ButterKnife.inject(this);

		populateAutoComplete();
		mEditEmail.addTextChangedListener(new ErrorTextWatcher(mTextEmailError));
		mEditPassword.addTextChangedListener(new ErrorTextWatcher(mTextPasswordError));
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
	}

	@OnClick(R.id.btn_login)
	protected void performLogin() {
		if (!validate()) {
			return;
		}
		new LoginAsyncTask(this).execute(mEditEmail.getText().toString().trim(), mEditPassword.getText().toString().trim());
	}

	private boolean validate() {
		if (!hasConnection()) {
			Toast.makeText(this, getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
			return false;
		}
		String email = mEditEmail.getText().toString().trim();
		String password = mEditPassword.getText().toString().trim();
		if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
			Toast.makeText(this, getString(R.string.error_email_and_password_required), Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private boolean hasConnection() {
		final ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	private void populateAutoComplete() {
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int index, Bundle bundle) {
		return new CursorLoader(this,
		                        // Retrieve data rows for the device user's 'profile' contact.
		                        Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
		                                             ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

		                        // Select only email addresses.
		                        ContactsContract.Contacts.Data.MIMETYPE + " = ?",
		                        new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE},

		                        // Show primary email addresses first. Note that there won't be
		                        // a primary email address if the user hasn't specified one.
		                        ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		List<String> emails = new ArrayList<String>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			emails.add(cursor.getString(ProfileQuery.ADDRESS));
			cursor.moveToNext();
		}
		addEmailsToAutoComplete(emails);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {

	}

	private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
		ArrayAdapter<String> adapter =
				new ArrayAdapter<String>(LoginActivity.this, android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

		mEditEmail.setAdapter(adapter);
	}
}

