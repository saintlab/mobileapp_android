package com.omnom.android.utils;

import android.text.Editable;

import com.omnom.android.activity.TextListener;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.view.ErrorEditText;

/**
 * Created by Ch3D on 28.10.2014.
 */
public class CardNumberTextWatcher extends CardDataTextWatcher {

	public static final String DELIMITER_CARD_NUMBER = "  ";

	public static final int MAX_LENGTH = 16;

	private ErrorEditText mView;

	private TextListener mListener;

	public CardNumberTextWatcher(ErrorEditText view, TextListener listener) {
		super(view);
		mView = view;
		mListener = listener;
	}

	@Override
	public void afterTextChanged(final Editable s) {
		mView.removeTextChangedListener(this);
		final String text = s.toString().replace(DELIMITER_CARD_NUMBER, StringUtils.EMPTY_STRING);

		StringBuilder formatted = new StringBuilder();
		int count = 0;
		final int length = Math.min(MAX_LENGTH, text.length());
		for(int i = 0; i < length; ++i) {
			if(Character.isDigit(text.charAt(i))) {
				if(count % 4 == 0 && count > 0) {
					formatted.append(DELIMITER_CARD_NUMBER);
				}
				formatted.append(text.charAt(i));
				++count;
			}
		}
		mView.setText(formatted.toString());
		mView.setSelection(formatted.length());
		final int length1 = text.length();
		if(length1 >= MAX_LENGTH) {
			focusNextView();
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
		return DELIMITER_CARD_NUMBER.length() * 3;
	}
}
