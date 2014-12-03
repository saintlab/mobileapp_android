package com.omnom.android.utils.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Shader;
import android.os.Environment;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.omnom.android.utils.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by xCh3Dx on 03.12.2014.
 */
public class MultiplyImageView extends ImageView {
	private Paint paint;
	private Paint fillPaint;
	private int radius;
	private int offset;
	private int fillAlpha;

	public MultiplyImageView(Context context) {
		super(context);
	}

	public MultiplyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MultiplyImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public static Bitmap convertToMutable(Bitmap imgIn) {
		try {
			//this is the file going to use temporally to save the bytes.
			// This file will not be a image, it will store the raw image data.
			File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

			//Open an RandomAccessFile
			//Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
			//into AndroidManifest.xml file
			RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

			// get the width and height of the source bitmap.
			int width = imgIn.getWidth();
			int height = imgIn.getHeight();
			Bitmap.Config type = imgIn.getConfig();

			//Copy the byte to the file
			//Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
			FileChannel channel = randomAccessFile.getChannel();
			MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes() * height);
			imgIn.copyPixelsToBuffer(map);
			//recycle the source bitmap, this will be no longer used.
			imgIn.recycle();
			System.gc();// try to force the bytes from the imgIn to be released

			//Create a new bitmap to load the bitmap again. Probably the memory will be available.
			imgIn = Bitmap.createBitmap(width, height, type);
			map.position(0);
			//load it back from temporary
			imgIn.copyPixelsFromBuffer(map);
			//close the temporary file and channel , then delete that also
			channel.close();
			randomAccessFile.close();

			// delete the temp file
			file.delete();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return imgIn;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (paint == null) {
			final Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
			final Canvas bgCanvas = new Canvas(bitmap);
			super.onDraw(bgCanvas);

			final Shader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			paint = new Paint();
			paint.setShader(shader);
			final ColorFilter filter = new PorterDuffColorFilter(getResources().getColor(R.color.error_red), PorterDuff.Mode.MULTIPLY);
			paint.setColorFilter(filter);

			fillPaint = new Paint();
			fillPaint.setColor(getResources().getColor(R.color.error_red));
			fillPaint.setAlpha(0);
		}
		// paint.setAlpha(255 - fillAlpha);
		fillPaint.setAlpha(fillAlpha);
		canvas.drawCircle(getWidth() / 2, (getHeight() / 2) - offset, radius, paint);
		canvas.drawCircle(getWidth() / 2, (getHeight() / 2) - offset, radius, fillPaint);
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setFillAlpha(int fillAlpha) {
		this.fillAlpha = fillAlpha;
	}

	public int getFillAlpha() {
		return fillAlpha;
	}
}
