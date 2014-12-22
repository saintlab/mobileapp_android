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

/**
 * Created by Ch3D on 18.12.2014.
 */
public class AmountEditText extends EditText {
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
					setSelection(selectionEnd - 1);
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
				if(str.endsWith(".") || str.endsWith(",")) {
					getText().delete(getSelectionEnd() - 1, getSelectionEnd());
					setSelection(getSelectionEnd() - 1);
					return;
				}
				final String currencySuffix = getCurrencySuffix();
				if(!str.endsWith(currencySuffix)) {
					str = str + currencySuffix;
				}
				if (str.length() > 2 && str.charAt(0) == '0') {
					str = str.substring(1);
				}
				if(str.equals(currencySuffix) || str.startsWith(".") || str.startsWith(",")) {
					str = "0" + str;
				}
				setText(str);
				setSelection(str.length() - 1);
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

	private String getCurrencySuffix() {
		return getContext().getString(R.string.currency_ruble);
	}
}
