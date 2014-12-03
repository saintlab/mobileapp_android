package com.omnom.android.utils.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by xCh3Dx on 03.12.2014.
 */
public class MultiplyDrawable extends Drawable {

	private Drawable baseDrawable;

	public MultiplyDrawable(Drawable drawable) {
		this.baseDrawable = drawable;
	}



	@Override
	public void draw(Canvas canvas) {
	}

	@Override
	public void setAlpha(int alpha) {

	}

	@Override
	public void setColorFilter(ColorFilter cf) {

	}

	@Override
	public int getOpacity() {
		return 0;
	}
}
