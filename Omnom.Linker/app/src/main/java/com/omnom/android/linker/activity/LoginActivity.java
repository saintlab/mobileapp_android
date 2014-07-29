package com.omnom.android.linker.activity;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.omnom.android.linker.R;
import com.omnom.android.linker.utils.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {
	@InjectView(R.id.txt_email)
	protected AutoCompleteTextView mEmailView;

	@InjectView(R.id.edit_password)
	protected EditText mPasswordView;

	@InjectView(R.id.root)
	protected View mRootView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ButterKnife.inject(this);

		populateAutoComplete();
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
		// TODO:
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus) {
			ValueAnimator colorAnim = ObjectAnimator.ofInt(mRootView, "backgroundColor", getResources().getColor(android.R.color.holo_red_light),
			                                               getResources().getColor(android.R.color.white));
			colorAnim.setDuration(AnimationUtils.DURATION_LONG);
			colorAnim.setEvaluator(new ArgbEvaluator());
			colorAnim.start();
		}
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

		mEmailView.setAdapter(adapter);
	}

	private interface ProfileQuery {
		String[] PROJECTION = {ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.IS_PRIMARY,};

		int ADDRESS = 0;
		int IS_PRIMARY = 1;
	}
}



