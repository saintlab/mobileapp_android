package com.omnom.android.linker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.omnom.android.linker.R;

/**
 * Created by Ch3D on 07.08.2014.
 */
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
