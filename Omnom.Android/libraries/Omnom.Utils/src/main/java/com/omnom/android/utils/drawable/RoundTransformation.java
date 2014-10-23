package com.omnom.android.utils.drawable;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

/**
 * Created by Ch3D on 29.08.2014.
 */
public class RoundTransformation implements Transformation {

	public static RoundTransformation create(int cornerRadius, int margin) {
		return new RoundTransformation(cornerRadius, margin);
	}

	private final int mCornerRadius;
	private final int mMargin;

	private RoundTransformation(int cornerRadius, int margin) {
		mCornerRadius = cornerRadius;
		mMargin = margin;
	}

	@Override
	public Bitmap transform(Bitmap source) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

		Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		canvas.drawRoundRect(new RectF(mMargin, mMargin, source.getWidth() - mMargin, source.getHeight() - mMargin), mCornerRadius,
		                     mCornerRadius, paint);

		if(source != output) {
			source.recycle();
		}

		return output;
	}

	@Override
	public String key() {
		return "rounded(radius=" + mCornerRadius + ", margin=" + mMargin + ")";
	}
}
