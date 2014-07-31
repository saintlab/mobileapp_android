package com.omnom.android.linker.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.EditText;

/**
 * Created by Ch3D on 29.07.2014.
 */
public class ViewUtils {
	public static int dipToPixels(final Context context, final float dips) {
		final float scale = context.getResources().getDisplayMetrics().density;
		final int paddingInPixels = (int) ((dips * scale) + 0.5f);
		return paddingInPixels;
	}

	public static void fixPasswordTypeface(EditText editText) {
		editText.setTypeface(Typeface.DEFAULT, 0);
	}
}
