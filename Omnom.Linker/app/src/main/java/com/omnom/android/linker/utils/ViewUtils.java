package com.omnom.android.linker.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.EditText;

import butterknife.ButterKnife;

/**
 * Created by Ch3D on 29.07.2014.
 */
public class ViewUtils {

	public static final ButterKnife.Setter<View, Boolean> VISIBLITY = new ButterKnife.Setter<View, Boolean>() {
		@Override
		public void set(View view, Boolean value, int index) {
			view.setVisibility(value ? View.VISIBLE : View.GONE);
		}
	};

	public static int dipToPixels(final Context context, final float dips) {
		final float scale = context.getResources().getDisplayMetrics().density;
		final int paddingInPixels = (int) ((dips * scale) + 0.5f);
		return paddingInPixels;
	}

	public static void fixPasswordTypeface(EditText editText) {
		editText.setTypeface(Typeface.DEFAULT, 0);
	}

	public static String getTextValue(EditText view) {
		return view.getText().toString().trim();
	}
}
