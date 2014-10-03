package com.omnom.util.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omnom.android.utils.R;
import com.omnom.util.utils.StringUtils;
import com.omnom.util.utils.ViewUtils;

import hugo.weaving.DebugLog;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

/**
 * Created by Ch3D on 29.09.2014.
 */
public class ErrorEdit extends LinearLayout {
	public static final int INPUT_TYPE_TEXT = 0;
	public static final int INPUT_TYPE_PHONE = 1;
	public static final int INPUT_TYPE_EMAIL = 2;
	public static final int INPUT_TYPE_NAME = 3;

	public static final int FONT_TYPE_REGULAR = 0;
	public static final int FONT_TYPE_MEDIUM = 1;
	public static final int FONT_TYPE_REGULAR_LE = 2;

	private ErrorEditText editView;
	private TextView errorTextView;
	private Button btnClear;

	private boolean mShowClear;
	private final TextWatcher onTextChanged = new TextWatcher() {
		@Override
		@DebugLog
		public void afterTextChanged(final Editable editable) {
			clearError();
			if(mShowClear && editView.isFocused()) {
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
	private String mHintText;
	private Drawable mClearDrawable;
	private int mInputType;
	private int mFontType;

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
			mInputType = a.getInt(R.styleable.ErrorEdit_inputType, INPUT_TYPE_TEXT);
			mFontType = a.getInt(R.styleable.ErrorEdit_font, FONT_TYPE_REGULAR);
		} finally {
			a.recycle();
		}
	}

	private void init() {
		setOrientation(VERTICAL);
		final View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_error_edit_text, this);
		editView = (ErrorEditText) view.findViewById(R.id.edit);
		initInputType();
		editView.addTextChangedListener(onTextChanged);
		editView.setHint(mHintText);
		initFontType();
		editView.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					if(editView.getText().length() > 0 && mShowClear) {
						ViewUtils.setVisible(btnClear, true);
					}
				} else {
					ViewUtils.setVisible(btnClear, false);
				}
			}
		});
		errorTextView = (TextView) view.findViewById(R.id.error);
		btnClear = (Button) view.findViewById(R.id.btn_clear);
		btnClear.setBackgroundDrawable(mClearDrawable);
		btnClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editView.setText(StringUtils.EMPTY_STRING);
			}
		});
	}

	private void initFontType() {
		switch(mFontType) {
			case FONT_TYPE_REGULAR:
				CalligraphyUtils.applyFontToTextView(getContext(), editView, "fonts/Futura-OSF-Omnom-Regular.otf");
				break;

			case FONT_TYPE_MEDIUM:
				CalligraphyUtils.applyFontToTextView(getContext(), editView, "fonts/Futura-OSF-Omnom-Medium.otf");
				break;

			case FONT_TYPE_REGULAR_LE:
				CalligraphyUtils.applyFontToTextView(getContext(), editView, "fonts/Futura-LSF-Omnom-LE-Regular.otf");
				break;
		}
	}

	private void initInputType() {
		switch(mInputType) {
			case INPUT_TYPE_TEXT:
				editView.setInputType(InputType.TYPE_CLASS_TEXT);
				break;

			case INPUT_TYPE_PHONE:
				editView.setInputType(InputType.TYPE_CLASS_PHONE);
				break;

			case INPUT_TYPE_EMAIL:
				editView.setInputType(InputType.TYPE_CLASS_TEXT
						                      | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
						                      | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
				break;

			case INPUT_TYPE_NAME:
				editView.setInputType(InputType.TYPE_CLASS_TEXT
						                      | InputType.TYPE_TEXT_VARIATION_PERSON_NAME
						                      | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
				break;
		}
	}

	public void setError(final int strId) {
		errorTextView.setText(strId);
		editView.setError(true);
	}

	public void setError(final String msg) {
		errorTextView.setText(msg);
		editView.setError(true);
	}

	public String getText() {
		return editView.getText().toString();
	}

	public EditText getEditText() {
		return editView;
	}
}
