package com.omnom.util.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.omnom.android.utils.R;

public class ErrorEditText extends EditText {
	public ErrorEditText(Context context) {
		super(context);
	}

	public ErrorEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ErrorEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isInEditMode() {
		return false;
	}

	public void setError(boolean error) {
		setBackgroundResource(error ? R.drawable.textfield_error_mtrl_alpha : R.drawable.edit_text);
	}
}
