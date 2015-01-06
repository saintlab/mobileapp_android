package com.omnom.android.utils.utils;

import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by Ch3D on 06.01.2015.
 */
public class ClickSpan extends ClickableSpan {
	public interface OnClickListener {
		void onClick();
	}

	private OnClickListener mListener;

	public ClickSpan(OnClickListener listener) {
		mListener = listener;
	}

	@Override
	public void onClick(View widget) {
		if(mListener != null) {
			mListener.onClick();
		}
	}
}
