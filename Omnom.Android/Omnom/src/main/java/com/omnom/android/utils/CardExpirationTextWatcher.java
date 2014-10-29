package com.omnom.android.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.omnom.android.activity.TextListener;
import com.omnom.android.utils.utils.StringUtils;

/**
 * Created by Ch3D on 28.10.2014.
 */
public class CardExpirationTextWatcher implements TextWatcher {

	private static final String DELIMITER_DATE = "/";

	private static final int MAX_LENGTH = 4;

	private EditText mView;

	private TextListener mListener;

	public CardExpirationTextWatcher(EditText view, TextListener listener) {
		mView = view;
		mListener = listener;
	}

	@Override
	public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

	}

	@Override
	public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {

	}

	@Override
	public void afterTextChanged(final Editable s) {
		mView.removeTextChangedListener(this);
		final String text = s.toString().replace(DELIMITER_DATE, StringUtils.EMPTY_STRING);

		StringBuilder formatted = new StringBuilder();
		int count = 0;
		final int length = Math.min(MAX_LENGTH, text.length());
		for(int i = 0; i < length; ++i) {
			if(Character.isDigit(text.charAt(i))) {
				if(count % 2 == 0 && count > 0) {
					formatted.append(DELIMITER_DATE);
				}
				formatted.append(text.charAt(i));
				++count;
			}
		}
		mView.setText(formatted.toString());
		mView.setSelection(formatted.length());
		mListener.onTextChanged(s.toString());
		mView.addTextChangedListener(this);
	}
}
