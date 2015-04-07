package com.omnom.android.utils.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.EditText;

import com.omnom.android.utils.R;

public class ErrorEditText extends EditText {
	private int mDefaultDrawableId = 0;

	private ColorStateList mTextColors = null;

	public ErrorEditText(Context context) {
		super(context);
		init();
	}

	public ErrorEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ErrorEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mTextColors = getTextColors();
	}

	@Override
	public boolean isInEditMode() {
		return false;
	}

	public void setError(boolean error) {
		setBackgroundResource(error ? R.drawable.textfield_error_mtrl_alpha : getDefaultDrawable());
		if(!error && mTextColors != null) {
			setTextColor(mTextColors);
		}
	}

	public void setError(boolean error, String errorText) {
		if(error) {
			setText(errorText);
			setTextColor(getResources().getColor(R.color.error_red));
		}
		setError(error);
	}

	private int getDefaultDrawable() {
		TypedValue typedValue = new TypedValue();
		if(mDefaultDrawableId == 0) {
			getContext().getTheme().resolveAttribute(android.R.attr.editTextStyle, typedValue, true);
			int[] attribute = new int[]{android.R.attr.background};
			final TypedArray array = getContext().obtainStyledAttributes(typedValue.resourceId, attribute);
			int resId = array.getResourceId(0, -1);
			array.recycle();
			mDefaultDrawableId = (resId == -1) ? R.drawable.edit_text : resId;
		}
		return mDefaultDrawableId;
	}
}
