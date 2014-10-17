package com.omnom.android.utils.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by Ch3D on 17.10.2014.
 */
public class AmountEditText extends EditText {
	public AmountEditText(Context context) {
		super(context);
	}

	public AmountEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AmountEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		System.err.println(">>>>> " + keyCode);
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			clearFocus();
			requestFocus(FOCUS_FORWARD);
		}
		return super.onKeyPreIme(keyCode, event);
	}
}
