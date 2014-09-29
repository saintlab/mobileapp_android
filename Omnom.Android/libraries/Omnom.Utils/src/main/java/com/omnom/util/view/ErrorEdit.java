package com.omnom.util.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omnom.android.utils.R;
import com.omnom.util.utils.StringUtils;
import com.omnom.util.utils.ViewUtils;

/**
 * Created by Ch3D on 29.09.2014.
 */
public class ErrorEdit extends LinearLayout {
	private ErrorEditText editView;
	private TextView errorTextView;
	private Button btnClear;

	private boolean mShowClear;
	private String mHintText;
	private Drawable mClearDrawable;

	private final TextWatcher onTextChanged = new TextWatcher() {
		@Override
		public void afterTextChanged(final Editable editable) {
			clearError();
			if(mShowClear) {
				ViewUtils.setVisible(btnClear, editable.length() > 0);
			}
		}

		@Override
		public void beforeTextChanged(final CharSequence charSequence, final int i, final int i2, final int i3) {
		}

		@Override
		public void onTextChanged(final CharSequence charSequence, final int i, final int i2, final int i3) {
		}
	};

	public ErrorEdit(Context context) {
		super(context);
		init();
	}

	public ErrorEdit(Context context, AttributeSet attrs) {
		super(context, attrs);
		processAttrs(attrs);
		init();
	}

	public ErrorEdit(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void clearError() {
		editView.setError(false);
		errorTextView.setText(StringUtils.EMPTY_STRING);
	}

	private void processAttrs(AttributeSet attrs) {
		TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ErrorEdit, 0, 0);
		try {
			mShowClear = a.getBoolean(R.styleable.ErrorEdit_showClear, false);
			mHintText = a.getString(R.styleable.ErrorEdit_hint);
			mClearDrawable = a.getDrawable(R.styleable.ErrorEdit_iconClear);
		} finally {
			a.recycle();
		}
	}

	private void init() {
		setOrientation(VERTICAL);
		final View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_error_edit_text, this);
		editView = (ErrorEditText) view.findViewById(R.id.edit);
		editView.addTextChangedListener(onTextChanged);
		editView.setHint(mHintText);
		errorTextView = (TextView) view.findViewById(R.id.error);
		btnClear = (Button) view.findViewById(R.id.btn_clear);
		btnClear.setBackground(mClearDrawable);
		btnClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editView.setText(StringUtils.EMPTY_STRING);
			}
		});
	}

	public void setError(final int strId) {
		errorTextView.setText(strId);
		editView.setError(true);
	}

	public String getText() {
		return editView.getText().toString();
	}
}
