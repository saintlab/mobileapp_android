package com.omnom.android.listener;

import android.text.InputType;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;

import java.text.DecimalFormat;

/**
 * Workaround to give a chance to enter comma decimal separator for european user locales.
 */
public class DecimalKeyListener extends DigitsKeyListener {

	private final char decimalSeparator = new DecimalFormat().getDecimalFormatSymbols().getDecimalSeparator();
	private final char[] acceptedCharacters =
			new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
						 decimalSeparator};

	@Override
	protected char[] getAcceptedChars() {
		return acceptedCharacters;
	}

	@Override
	public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
		final CharSequence filtered = super.filter(source, start, end, dest, dstart, dend);
		if (filtered == null && source.equals(String.valueOf(decimalSeparator)) && contains(dest, decimalSeparator)) {
			return "";
		}
		return super.filter(source, start, end, dest, dstart, dend);
	}

	public int getInputType() {
		return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT;
	}

	private boolean contains(final Spanned text, final char character) {
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == character) {
				return true;
			}
		}

		return false;
	}

}