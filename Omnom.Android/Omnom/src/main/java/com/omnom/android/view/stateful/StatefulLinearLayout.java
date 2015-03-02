package com.omnom.android.view.stateful;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by mvpotter on 2/16/2015.
 */
public class StatefulLinearLayout extends LinearLayout {

	private boolean isKeepStatePressed;

	public StatefulLinearLayout(Context context) {
		super(context);
	}

	public StatefulLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StatefulLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
