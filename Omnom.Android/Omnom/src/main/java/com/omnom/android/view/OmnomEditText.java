package com.omnom.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

/**
 * Created by mvpotter on 1/30/2015.
 */
public class OmnomEditText extends EditText {

	public OmnomEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public OmnomEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public OmnomEditText(Context context) {
		super(context);
	}

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		return new OmnomInputConnection(super.onCreateInputConnection(outAttrs), true);
	}

	private class OmnomInputConnection extends InputConnectionWrapper {

		public OmnomInputConnection(InputConnection target, boolean mutable) {
			super(target, mutable);
		}

		@Override
		public boolean sendKeyEvent(KeyEvent event) {
			return super.sendKeyEvent(event);
		}

		@Override
		public boolean deleteSurroundingText(int beforeLength, int afterLength) {
			if (beforeLength == 1 && afterLength == 0) {
				return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
						&& sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
			}

			return super.deleteSurroundingText(beforeLength, afterLength);
		}

	}

}
