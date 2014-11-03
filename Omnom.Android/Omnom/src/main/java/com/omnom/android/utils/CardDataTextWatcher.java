package com.omnom.android.utils;

import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Ch3D on 02.11.2014.
 */
public abstract class CardDataTextWatcher implements TextWatcher {

	private final EditText mView;

	private String moveString;

	public CardDataTextWatcher(EditText view) {
		mView = view;
	}

	public abstract int getMaxLength();

	public abstract int getDelimiterLength();

	@Override
	public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
		moveString = null;
	}

	@Override
	public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
		if(s.toString().length() > getMaxLength() + getDelimiterLength()) {
			moveString = String.valueOf(s.charAt(s.length() - 1));
		}
	}

	public void focusNextView() {
		final EditText view = (EditText) mView.focusSearch(View.FOCUS_DOWN);
		if(view != null) {
			if(view.getText().length() == 0 && !TextUtils.isEmpty(moveString)) {
				view.setText(moveString);
				view.setSelection(view.getText().length());
			}
			view.requestFocus();
		}
	}

	public void focusPrevView() {
		final View view = mView.focusSearch(View.FOCUS_UP);
		if(view != null) {
			view.requestFocus();
		}
	}
}
