package com.omnom.android.view.stateful;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by mvpotter on 2/16/2015.
 */
public class StatefulTextView extends TextView {

	private boolean isKeepStatePressed;

	public StatefulTextView(Context context) {
		super(context);
	}

	public StatefulTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StatefulTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setKeepStatePressed(boolean isKeepStatePressed) {
		this.isKeepStatePressed = isKeepStatePressed;
	}

	@Override
	public void setPressed(boolean pressed) {
		super.setPressed(isKeepStatePressed || pressed);
	}

}
