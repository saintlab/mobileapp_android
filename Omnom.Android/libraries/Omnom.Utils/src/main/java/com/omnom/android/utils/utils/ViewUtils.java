package com.omnom.android.utils.utils;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import butterknife.ButterKnife;

import static com.omnom.android.utils.utils.AnimationUtils.animateAlpha;
import static com.omnom.android.utils.utils.AnimationUtils.animateAlpha2;

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

	public static final ButterKnife.Setter<View, Boolean> VISIBLITY2 = new ButterKnife.Setter<View, Boolean>() {
		@Override
		public void set(View view, Boolean value, int index) {
			view.setVisibility(value ? View.VISIBLE : View.INVISIBLE);
		}
	};

	public static final ButterKnife.Setter<View, Boolean> VISIBLITY_ALPHA = new ButterKnife.Setter<View, Boolean>() {
		@Override
		public void set(View view, Boolean value, int index) {
			animateAlpha(view, value);
		}
	};

	public static final ButterKnife.Setter<View, Boolean> VISIBLITY_ALPHA2 = new ButterKnife.Setter<View, Boolean>() {
		@Override
		public void set(View view, Boolean value, int index) {
			animateAlpha2(view, value);
		}
	};

	public static final ButterKnife.Setter<View, Boolean> VISIBLITY_ALPHA_NOW = new ButterKnife.Setter<View, Boolean>() {
		@Override
		public void set(View view, Boolean value, int index) {
			if(view != null) {
				view.setAlpha(value ? 1 : 0);
				setVisible(view, value);
			}
		}
	};

	public static final ButterKnife.Setter<View, Boolean> ENABLED = new ButterKnife.Setter<View, Boolean>() {
		@Override
		public void set(View view, Boolean value, int index) {
			view.setEnabled(value);
		}
	};

	public static int dipToPixels(final Context context, final float dips) {
		final float scale = context.getResources().getDisplayMetrics().density;
		final int paddingInPixels = (int) ((dips * scale) + 0.5f);
		return paddingInPixels;
	}

	public static void setVisible(View view, boolean visible) {
		if(view != null) {
			view.setVisibility(visible ? View.VISIBLE : View.GONE);
			view.setTag(visible);
		}
	}

	public static void setVisible2(View view, boolean visible) {
		if(view != null) {
			view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
			view.setTag(visible);
		}
	}

	public static void setVisible(List<View> buttonViews, boolean visible) {
		ButterKnife.apply(buttonViews, VISIBLITY, visible);
	}

	public static void fixPasswordTypeface(EditText editText, EditText like) {
		editText.setTypeface(like.getTypeface());
	}

	public static String getTextValue(EditText view) {
		return view.getText().toString().trim();
	}

	public static void setHeight(final View view, final int value) {
		if(view == null || view.getLayoutParams() == null) {
			return;
		}
		view.getLayoutParams().height = value;
		view.requestLayout();
	}
}
