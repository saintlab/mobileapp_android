package com.omnom.android.utils.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.omnom.android.utils.R;

/**
 * Created by xCh3Dx on 03.12.2014.
 */
public class MultiplyImageView extends ImageView {
	private Paint mPaint;

	private Paint mFillPaint;

	private int mRadius;

	private int mOffset;

	private int mFillAlpha;

	private int mColor;

	public MultiplyImageView(Context context) {
		super(context);
	}

	public MultiplyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		processAttrs(attrs);
	}

	public MultiplyImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		processAttrs(attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(mPaint == null) {
			final Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
			final Canvas bgCanvas = new Canvas(bitmap);
			super.onDraw(bgCanvas);

			final Shader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			mPaint = new Paint();
			mPaint.setShader(shader);
			final ColorFilter filter = new PorterDuffColorFilter(mColor, PorterDuff.Mode.MULTIPLY);
			mPaint.setColorFilter(filter);
			mPaint.setDither(true);

			mFillPaint = new Paint();
			mFillPaint.setColor(mColor);
			mFillPaint.setAlpha(0);
			mFillPaint.setDither(true);
		}
		mFillPaint.setAlpha(mFillAlpha);
		final int cx = getWidth() / 2;
		final int cy = (getHeight() / 2) - mOffset;
		canvas.drawCircle(cx, cy, mRadius, mPaint);
		canvas.drawCircle(cx, cy, mRadius, mFillPaint);
	}

	private void processAttrs(AttributeSet attrs) {
		final TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.MultiplyImageView, 0, 0);
		try {
			mColor = a.getColor(R.styleable.MultiplyImageView_multiplyColor, getResources().getColor(R.color.loader_bg_transparent));
		} finally {
			a.recycle();
		}
	}

	public void setFillAlpha(int mFillAlpha) {
		this.mFillAlpha = mFillAlpha;
	}

	public void setOffset(int mOffset) {
		this.mOffset = mOffset;
	}

	public void setRadius(int mRadius) {
		this.mRadius = mRadius;
	}
}
