package com.omnom.android.utils.utils;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by Ch3D on 06.01.2015.
 */
public class ClickSpan extends ClickableSpan {

    public interface OnClickListener {
		void onClick();
	}

    private boolean isUnderlined;

	private final OnClickListener mListener;

    public ClickSpan(final OnClickListener listener) {
        this(true, listener);
    }

	public ClickSpan(final boolean isUnderlined, final OnClickListener listener) {
        this.isUnderlined = isUnderlined;
        this.mListener = listener;
	}

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(isUnderlined);
    }

    @Override
	public void onClick(View widget) {
		if(mListener != null) {
			mListener.onClick();
		}
	}

}
