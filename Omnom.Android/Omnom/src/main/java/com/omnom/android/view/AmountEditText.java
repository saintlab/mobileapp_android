package com.omnom.android.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.omnom.android.R;
import com.omnom.android.listener.DecimalKeyListener;
import com.omnom.android.utils.utils.AmountHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by Ch3D on 18.12.2014.
 */
public class AmountEditText extends EditText {

	public static final int AMOUNT_UPPER_LIMIT = 999999;

	private NumberFormat numberFormat;

	private String decimalSeparator;

	public AmountEditText(final Context context) {
		super(context);
		init();
	}

	public AmountEditText(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AmountEditText(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		updateSeparator();
		setCursorVisible(false);

		// disable copy -paste
		setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(final View v) {
				return true;
			}
		});
		setCustomSelectionActionModeCallback(new ActionMode.Callback() {
			@Override
			public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
				return false;
			}

			@Override
			public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
				return false;
			}

			@Override
			public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
				return false;
			}

			@Override
			public void onDestroyActionMode(final ActionMode mode) {
			}
		});

		// move selection before currency sign
		setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
				final int selectionEnd = getSelectionEnd();
				final int selectionStart = getSelectionStart();
				if(selectionStart == selectionEnd && selectionEnd == getText().length()) {
					setSelection(selectionEnd - getCurrencySuffix().length());
				}
				return false;
			}
		});

		addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				removeTextChangedListener(this);
				String str = s.toString();
				// TODO: refactor the following code to make it readable
				if(str.endsWith(decimalSeparator)) {
					final int suffixLength = getCurrencySuffix().length();
					getText().delete(getSelectionEnd() - suffixLength, getSelectionEnd());
					setSelection(getSelectionEnd() - suffixLength);
					return;
				}
				// Add currency suffics if necessary
				final String currencySuffix = getCurrencySuffix();
				if(!str.endsWith(currencySuffix)) {
					str = str + currencySuffix;
				}
				// Remove leading zero in case of integer value
				if (str.length() > 2 && str.charAt(0) == '0') {
					str = str.substring(1);
				}
				// Add leading zero if the amount is empty or starts with floating point
				if(str.equals(currencySuffix) || str.startsWith(decimalSeparator)) {
					str = "0" + str;
				}
				// If amount exceeds upper limit make it equals to it
				double amount;
				try {
					amount = numberFormat.parse(str.substring(0, str.length() - getCurrencySuffix().length())).doubleValue();
				} catch (ParseException e) {
					amount = 0;
				}
				if (amount > AMOUNT_UPPER_LIMIT) {
					str = AMOUNT_UPPER_LIMIT + currencySuffix;
				} else {
					// Reduce number of digits after floating point if it exceeds 2
					int decimalSeparatorIndex = str.indexOf(decimalSeparator);
					if (decimalSeparatorIndex > 0 && str.length() > decimalSeparatorIndex + 4) {
						str = str.substring(0, decimalSeparatorIndex + 3) + currencySuffix;
					}
				}
				setText(str);
				setSelection(str.length() - getCurrencySuffix().length());
				addTextChangedListener(this);
			}
		});

		// move cursor before currency sign
		setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					int length = getText().length();
					if(length >= 2) {
						setSelection(length - 2);
					}
				}
			}
		});
	}

	public void updateSeparator() {
		numberFormat = NumberFormat.getNumberInstance();
		decimalSeparator = String.valueOf(((DecimalFormat) numberFormat).getDecimalFormatSymbols().getDecimalSeparator());
		final String amount = getText().toString();
		final String previousSeparator = AmountHelper.getSeparator(amount);
		setKeyListener(new DecimalKeyListener());
		if (previousSeparator != null && !previousSeparator.equals(decimalSeparator)) {
			setText(amount.replace(previousSeparator, decimalSeparator));
		}
	}

	private String getCurrencySuffix() {
		return getContext().getString(R.string.currency_suffix_ruble);
	}

}
