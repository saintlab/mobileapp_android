package com.omnom.android.utils;

import android.text.Editable;

import com.omnom.android.activity.TextListener;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.view.ErrorEditText;

/**
 * Created by Ch3D on 28.10.2014.
 */
public class CardExpirationTextWatcher extends CardDataTextWatcher {

	private static final String DELIMITER_DATE = "/";

	private static final int MAX_LENGTH = 4;

	private ErrorEditText mView;

	private TextListener mListener;

	private String moveString;

	public CardExpirationTextWatcher(ErrorEditText view, TextListener listener) {
		super(view);
		mView = view;
		mListener = listener;
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
		final int length1 = formatted.length();
		mView.setSelection(length1);
		if(length1 == MAX_LENGTH + DELIMITER_DATE.length()) {
			focusNextView();
		} else if(length1 == 0) {
			focusPrevView();
		}
		mListener.onTextChanged(s.toString());
		mView.addTextChangedListener(this);
	}

	@Override
	public int getMaxLength() {
		return MAX_LENGTH;
	}

	@Override
	public int getDelimiterLength() {
		return DELIMITER_DATE.length();
	}
}
