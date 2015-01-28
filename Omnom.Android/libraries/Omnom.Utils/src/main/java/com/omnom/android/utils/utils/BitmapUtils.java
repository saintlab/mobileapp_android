package com.omnom.android.utils.utils;

import android.graphics.Bitmap;

import com.omnom.android.utils.utils.filters.FastBlur;

/**
 * Created by mvpotter on 1/28/2015.
 */
public class BitmapUtils {

	public static Bitmap blur(final Bitmap background, final int radius) {
		Bitmap blurredBackground = background;
		blurredBackground = FastBlur.doBlur(blurredBackground, radius, false);
		return blurredBackground;
	}

}
